package com.kycox.game.testutil;

import com.kycox.game.tools.Position;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import com.kycox.game.body.ghost.Ghost;
import com.kycox.game.constant.ghost.GhostStatus;
import com.kycox.game.engine.model.GameModel;
import com.kycox.game.engine.maze.Maze;

/**
 * Bot qui vise (étape 3.5.2e) : il joue Ladybug à la place d'un humain, à
 * chaque battement, en suivant trois règles simples, par ordre de priorité :
 * <ol>
 * <li><b>fuir</b> : un fantôme dangereux à {@value #DANGER_DISTANCE} cases ou
 * moins → aller vers la case voisine la plus éloignée des fantômes ;</li>
 * <li><b>chasser</b> : après une méga-gomme, un fantôme effrayé à
 * {@value #HUNT_DISTANCE} cases ou moins → aller le manger ;</li>
 * <li><b>manger</b> : sinon, aller vers la gomme la plus proche.</li>
 * </ol>
 * Les distances sont des plus courts chemins dans le labyrinthe (parcours en
 * largeur), pas des distances à vol d'oiseau. Le bot ne tire jamais au hasard :
 * avec une graine fixée, la partie reste déterministe.
 *
 * <p><b>Sans allers-retours</b> (21/11/2026, vu par Frédéric dans la démo
 * {@link BotDemo}) : avec un seul seuil, le bot fuyait à 3 cases, repartait
 * manger à 4… vers le fantôme, refuyait à 3 : haut, bas, haut, bas. Trois
 * remèdes :
 * <ul>
 * <li><b>hystérésis</b>, comme un thermostat : il se met à fuir à
 * {@value #DANGER_DISTANCE} cases, mais ne se remet à manger qu'au-delà de
 * {@value #SAFE_DISTANCE} ;</li>
 * <li>en mangeant ou en chassant, il <b>contourne</b> les cases à
 * {@value #KEEP_AWAY} cases ou moins d'un fantôme dangereux ;</li>
 * <li>en fuyant, à égalité, il <b>garde sa direction</b> plutôt que de faire
 * demi-tour ;</li>
 * <li>surtout, il ne <b>décide que pile au centre d'une case</b> : entre deux
 * cases, il garde le cap. Avant, dès que Ladybug entrait dans la case d'une
 * gomme, le bot visait déjà la suivante, demandait un demi-tour (accepté en
 * cours de case, verticalement) et repartait sans l'avoir mangée.</li>
 * </ul>
 */
public final class AimingBot {
	static final int DANGER_DISTANCE = 3;
	static final int SAFE_DISTANCE = 6;
	static final int KEEP_AWAY = 2;
	static final int HUNT_DISTANCE = 8;

	/** Vrai entre « fantôme à 3 cases » et « plus aucun fantôme à 6 cases ». */
	private boolean fleeing;
	/** Dernière direction demandée, pour ne pas faire demi-tour sans raison. */
	private Position lastDirection;

	/** Direction à demander pour Ladybug à ce battement (ou {@code null} : rien à faire). */
	public Position decide(GameModel model) {
		var direction = choose(model);
		if (direction != null) {
			lastDirection = direction;
		}
		return direction;
	}

	private Position choose(GameModel model) {
		// entre deux cases, garder le cap : décider seulement pile au centre d'une
		// case, là où le jeu mange la gomme et accepte un virage (sinon, le bot
		// visait une autre gomme avant d'avoir mangé celle de la case, et faisait
		// demi-tour : haut, bas, haut, bas)
		if (!model.getLadybug().isPerfectOnABlock() && lastDirection != null) {
			return lastDirection;
		}
		var screen = model.getMaze();
		var ladybug = model.getLadybug().getPositionBlock();
		if (screen.getScreenBlock(ladybug) == null) {
			return null;
		}
		var ghosts = model.getGroupGhosts().getGhosts();
		var dangerous = ghosts.stream().filter(AimingBot::isDangerous).map(g -> g.getPositionBlock()).toList();
		var fromLadybug = paths(ladybug, screen, Set.of());

		var nearestDanger = dangerous.stream().mapToInt(g -> fromLadybug.distances.getOrDefault(g, Integer.MAX_VALUE))
		        .min().orElse(Integer.MAX_VALUE);
		if (nearestDanger <= DANGER_DISTANCE) {
			fleeing = true;
		} else if (nearestDanger > SAFE_DISTANCE) {
			fleeing = false;
		}
		if (fleeing) {
			return flee(ladybug, dangerous, screen);
		}

		// manger ou chasser en contournant les fantômes dangereux ; à défaut, au plus court
		var avoiding = paths(ladybug, screen, nearGhosts(dangerous, screen));
		var edible = ghosts.stream().filter(AimingBot::isEdible).map(g -> g.getPositionBlock())
		        .filter(p -> fromLadybug.distances.getOrDefault(p, Integer.MAX_VALUE) <= HUNT_DISTANCE).toList();
		if (!edible.isEmpty()) {
			var hunt = avoiding.firstStepTowards(edible::contains, ladybug);
			return hunt != null ? hunt : fromLadybug.firstStepTowards(edible::contains, ladybug);
		}
		Predicate<Position> gum = p -> {
			var block = screen.getScreenBlock(p);
			return block != null && (block.isPoint() || block.isMegaPoint());
		};
		var eat = avoiding.firstStepTowards(gum, ladybug);
		return eat != null ? eat : fromLadybug.firstStepTowards(gum, ladybug);
	}

	/** Cases à {@value #KEEP_AWAY} cases ou moins d'un fantôme dangereux. */
	private static Set<Position> nearGhosts(List<Position> dangerous, Maze screen) {
		var near = new HashSet<Position>();
		for (var ghost : dangerous) {
			paths(ghost, screen, Set.of()).distances.forEach((cell, distance) -> {
				if (distance <= KEEP_AWAY) {
					near.add(cell);
				}
			});
		}
		return near;
	}

	static boolean isDangerous(Ghost ghost) {
		return ghost.getStatus() == GhostStatus.NORMAL || ghost.getStatus() == GhostStatus.REGENERATED
		        || ghost.getStatus() == GhostStatus.AUTOGENERATED;
	}

	static boolean isEdible(Ghost ghost) {
		return ghost.getStatus() == GhostStatus.SCARED || ghost.getStatus() == GhostStatus.FLASH;
	}

	/**
	 * Case voisine la plus éloignée du fantôme dangereux le plus proche ; à
	 * égalité, celle qui garde la direction en cours (pas de demi-tour inutile).
	 */
	private Position flee(Position ladybug, List<Position> dangerous, Maze screen) {
		var fromGhosts = dangerous.stream().map(g -> paths(g, screen, Set.of())).toList();
		Position best = null;
		var bestDistance = -1;
		for (var neighbour : openNeighbours(ladybug, screen)) {
			var distance = fromGhosts.stream()
			        .mapToInt(p -> p.distances.getOrDefault(neighbour, Integer.MAX_VALUE)).min()
			        .orElse(Integer.MAX_VALUE);
			var step = new Position(neighbour.x() - ladybug.x(), neighbour.y() - ladybug.y());
			if (distance > bestDistance || (distance == bestDistance && step.equals(lastDirection))) {
				bestDistance = distance;
				best = neighbour;
			}
		}
		return best == null ? null : new Position(best.x() - ladybug.x(), best.y() - ladybug.y());
	}

	/**
	 * Plus courts chemins depuis une case (parcours en largeur, ordre de voisins
	 * fixe), sans passer par les cases {@code avoided}.
	 */
	private static Paths paths(Position start, Maze screen, Set<Position> avoided) {
		var paths = new Paths();
		var queue = new ArrayDeque<Position>();
		paths.distances.put(start, 0);
		queue.add(start);
		while (!queue.isEmpty()) {
			var current = queue.poll();
			for (var next : openNeighbours(current, screen)) {
				if (!paths.distances.containsKey(next) && !avoided.contains(next)) {
					paths.distances.put(next, paths.distances.get(current) + 1);
					paths.parents.put(next, current);
					paths.order.add(next);
					queue.add(next);
				}
			}
		}
		return paths;
	}

	/** Voisins accessibles, dans le labyrinthe (les sorties de téléporteur sont ignorées). */
	private static List<Position> openNeighbours(Position point, Maze screen) {
		var block = screen.getScreenBlock(point);
		var neighbours = new ArrayList<Position>();
		if (block == null) {
			return neighbours;
		}
		if (!block.isBorderUp()) {
			neighbours.add(new Position(point.x(), point.y() - 1));
		}
		if (!block.isBorderDown()) {
			neighbours.add(new Position(point.x(), point.y() + 1));
		}
		if (!block.isBorderLeft()) {
			neighbours.add(new Position(point.x() - 1, point.y()));
		}
		if (!block.isBorderRight()) {
			neighbours.add(new Position(point.x() + 1, point.y()));
		}
		neighbours.removeIf(p -> screen.getScreenBlock(p) == null || screen.getScreenBlock(p).isNotAccessible());
		return neighbours;
	}

	private static final class Paths {
		final Map<Position, Integer> distances = new HashMap<>();
		final Map<Position, Position> parents = new HashMap<>();
		/** Cases dans l'ordre de découverte (la plus proche d'abord). */
		final List<Position> order = new ArrayList<>();

		/** Premier pas vers la case la plus proche qui vérifie {@code target}. */
		Position firstStepTowards(Predicate<Position> target, Position start) {
			for (var candidate : order) {
				if (target.test(candidate)) {
					var step = candidate;
					while (!parents.get(step).equals(start)) {
						step = parents.get(step);
					}
					return new Position(step.x() - start.x(), step.y() - start.y());
				}
			}
			return null;
		}
	}
}
