package com.kycox.game.testutil;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.kycox.game.engine.model.GameModel;

/**
 * Journal d'une partie (étape 3.5.2b, décision C31) : ce qui change, battement
 * par battement, en texte lisible, pour comparer deux parties ligne à ligne.
 *
 * <p>Le journal n'ajoute rien au jeu : il observe le modèle après chaque
 * battement et note les différences avec le battement précédent :
 * <pre>
 * b0064 STATUS PROGRAM_PRESENTATION
 * b0233 SCORE 1 (+1)
 * b0250 STATE ladybug=(6,11) NORMAL | Clyde=(7,7) NORMAL | Blinky=(7,6) NORMAL | …
 * b0412 GHOST Pinky SCARED
 * b0530 LIVES 2
 * </pre>
 * Une ligne {@code STATE} toutes les {@value #STATE_PERIOD} battements (1 s) donne
 * les positions de tous les personnages.
 */
public final class GameJournal {
	/** Une ligne d'état toutes les 25 battements, soit une seconde de jeu. */
	static final int STATE_PERIOD = 25;

	private final List<String> lines = new ArrayList<>();
	private Snapshot previous;

	public GameJournal(long seed) {
		lines.add("SEED " + seed);
	}

	/** Note ce qui a changé au battement {@code beat}. */
	void record(int beat, GameModel model) {
		var current = Snapshot.of(model);
		var prefix = String.format("b%04d ", beat);
		var before = previous != null ? previous : Snapshot.EMPTY;
		if (!Objects.equals(before.status, current.status)) {
			lines.add(prefix + "STATUS " + current.status);
		}
		if (before.level != current.level) {
			lines.add(prefix + "LEVEL " + current.level);
		}
		if (before.score != current.score) {
			var delta = current.score - before.score;
			lines.add(prefix + "SCORE " + current.score + " (" + (delta >= 0 ? "+" : "") + delta + ")");
		}
		if (before.lives != current.lives) {
			lines.add(prefix + "LIVES " + current.lives);
		}
		if (!Objects.equals(before.ladybugStatus, current.ladybugStatus)) {
			lines.add(prefix + "LADYBUG " + current.ladybugStatus);
		}
		current.ghostStatus.forEach((name, status) -> {
			if (!Objects.equals(before.ghostStatus.get(name), status)) {
				lines.add(prefix + "GHOST " + name + " " + status);
			}
		});
		if (beat % STATE_PERIOD == 0) {
			lines.add(prefix + "STATE " + current.state());
		}
		previous = current;
	}

	public List<String> lines() {
		return List.copyOf(lines);
	}

	public String text() {
		return String.join("\n", lines) + "\n";
	}

	/** Ce que le journal regarde dans le modèle, à un battement donné. */
	private record Snapshot(String status, int level, int score, int lives, String ladybugStatus,
	        Point ladybugBlock, Map<String, String> ghostStatus, Map<String, Point> ghostBlock) {

		static final Snapshot EMPTY = new Snapshot(null, -1, 0, -1, null, null, Map.of(), Map.of());

		static Snapshot of(GameModel model) {
			var ghostStatus = new LinkedHashMap<String, String>();
			var ghostBlock = new LinkedHashMap<String, Point>();
			model.getGroupGhosts().getGhosts().forEach(ghost -> {
				ghostStatus.put(ghost.getName(), String.valueOf(ghost.getStatus()));
				ghostBlock.put(ghost.getName(), new Point(ghost.getPositionBlock()));
			});
			var ladybug = model.getLadybug();
			return new Snapshot(String.valueOf(model.getCurrentProgramStatus().getGameStatus()),
			        model.getCurrentProgramStatus().getNumLevel(), model.getGameScore().getScore(),
			        ladybug.getLeftLifes(), String.valueOf(ladybug.getStatus()), new Point(ladybug.getPositionBlock()),
			        ghostStatus, ghostBlock);
		}

		String state() {
			var text = new StringBuilder("ladybug=" + block(ladybugBlock) + " " + ladybugStatus);
			ghostBlock.forEach((name, block) -> text.append(" | ").append(name).append('=').append(block(block))
			        .append(' ').append(ghostStatus.get(name)));
			return text.toString();
		}

		private static String block(Point point) {
			return "(" + point.x + "," + point.y + ")";
		}
	}
}
