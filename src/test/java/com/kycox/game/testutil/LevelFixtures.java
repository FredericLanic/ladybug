package com.kycox.game.testutil;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kycox.game.constant.contract.LevelStructure;
import com.kycox.game.constant.level.data.SecondGenerationLevel1;
import com.kycox.game.constant.level.data.SecondGenerationLevel2;
import com.kycox.game.constant.level.data.SecondGenerationLevel3;
import com.kycox.game.constant.level.data.SecondGenerationLevel4;
import com.kycox.game.constant.level.data.SecondGenerationLevel5;
import com.kycox.game.constant.level.data.SecondGenerationLevel6;
import com.kycox.game.constant.level.data.SecondGenerationLevel7;
import com.kycox.game.constant.level.data.SecondGenerationLevel8;
import com.kycox.game.engine.screendata.ManageLevel;
import com.kycox.game.engine.screendata.ScreenBlock;
import com.kycox.game.engine.screendata.ScreenData;

/**
 * Outils partagés par les tests qui ont besoin d'un vrai labyrinthe.
 * <p>
 * Aucun besoin de Spring : un {@link ScreenData} se construit à la main à
 * partir d'un niveau.
 */
public final class LevelFixtures {
	private LevelFixtures() {
	}

	/** Les niveaux actifs du jeu (ceux annotés {@code @Component}). */
	public static List<LevelStructure> activeLevels() {
		return List.of(new SecondGenerationLevel1(), new SecondGenerationLevel2(), new SecondGenerationLevel3(),
		        new SecondGenerationLevel4(), new SecondGenerationLevel5(), new SecondGenerationLevel6(),
		        new SecondGenerationLevel7(), new SecondGenerationLevel8());
	}

	/** Labyrinthe chargé comme au début d'une partie, sans l'aléatoire (méga-gommes, rendu). */
	public static ScreenData screenDataOf(LevelStructure level) {
		var screenData = new ScreenData(new ManageLevel(List.of(level)));
		screenData.setLevelMap(1, false);
		return screenData;
	}

	/**
	 * Distances (en nombre de cases) depuis {@code start}, par un parcours en
	 * largeur. Volontairement écrit sans rien reprendre de {@code Dijkstra} : il
	 * sert de référence indépendante. Les téléporteurs ne sont pas suivis.
	 */
	public static Map<Point, Integer> distancesFrom(Point start, ScreenData screenData) {
		Map<Point, Integer> distances = new HashMap<>();
		var queue = new ArrayDeque<Point>();
		distances.put(start, 0);
		queue.add(start);
		while (!queue.isEmpty()) {
			var current = queue.poll();
			for (var next : openNeighbours(current, screenData)) {
				if (!distances.containsKey(next)) {
					distances.put(next, distances.get(current) + 1);
					queue.add(next);
				}
			}
		}
		return distances;
	}

	/** Cases voisines atteignables en un pas (pas de mur entre les deux). */
	public static List<Point> openNeighbours(Point point, ScreenData screenData) {
		ScreenBlock block = screenData.getScreenBlock(point);
		var neighbours = new java.util.ArrayList<Point>();
		if (!block.isBorderUp()) {
			neighbours.add(new Point(point.x, point.y - 1));
		}
		if (!block.isBorderDown()) {
			neighbours.add(new Point(point.x, point.y + 1));
		}
		if (!block.isBorderLeft()) {
			neighbours.add(new Point(point.x - 1, point.y));
		}
		if (!block.isBorderRight()) {
			neighbours.add(new Point(point.x + 1, point.y));
		}
		return neighbours;
	}

	/** Nom court du niveau, pour l'affichage des tests paramétrés. */
	public static String nameOf(LevelStructure level) {
		return level.getClass().getSimpleName();
	}
}
