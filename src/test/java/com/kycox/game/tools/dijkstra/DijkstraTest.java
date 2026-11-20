package com.kycox.game.tools.dijkstra;

import com.kycox.game.tools.Position;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.kycox.game.constant.contract.LevelStructure;
import com.kycox.game.constant.level.data.SecondGenerationLevel1;
import com.kycox.game.engine.screendata.ScreenBlock;
import com.kycox.game.testutil.LevelFixtures;

/**
 * Tests de {@link Dijkstra}, le calcul du plus court chemin utilisé par les
 * fantômes.
 * <p>
 * La référence est un parcours en largeur écrit dans
 * {@link LevelFixtures#distancesFrom}, sans rien reprendre de Dijkstra : si les
 * deux calculs sont d'accord sur tous les niveaux, Dijkstra est juste.
 */
class DijkstraTest {

	static Stream<Arguments> levels() {
		return LevelFixtures.activeLevels().stream().map(l -> Arguments.of(LevelFixtures.nameOf(l), l));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("levels")
	@DisplayName("Du point de régénération vers chaque case : chemin le plus court et sans traverser de mur")
	void shortestValidPathToEveryReachableBlock(String name, LevelStructure level) {
		var screenData = LevelFixtures.screenDataOf(level);
		var start = level.getGhostRegenerateBlockPoint();
		var expectedDistances = LevelFixtures.distancesFrom(start, screenData);

		expectedDistances.forEach((target, expectedDistance) -> {
			var path = Dijkstra.getShorterWay(start, target, screenData);

			assertThat(path).as("chemin vers %s", target).startsWith(start).endsWith(target);
			// un chemin de N pas contient N + 1 cases
			assertThat(path).as("longueur du chemin vers %s", target).hasSize(expectedDistance + 1);
			for (var i = 0; i + 1 < path.size(); i++) {
				assertThat(LevelFixtures.openNeighbours(path.get(i), screenData))
				        .as("pas de %s à %s sans mur", path.get(i), path.get(i + 1)).contains(path.get(i + 1));
			}
		});
	}

	@Test
	@DisplayName("Vers la case de départ : un chemin d'une seule case")
	void pathToItselfIsSingleBlock() {
		var level = new SecondGenerationLevel1();
		var screenData = LevelFixtures.screenDataOf(level);
		var start = level.getInitLadybugBlockPos();

		assertThat(Dijkstra.getShorterWay(start, start, screenData)).containsExactly(start);
	}

	@Test
	@DisplayName("Les téléporteurs ne sont pas des raccourcis pour Dijkstra")
	void teleportsAreIgnored() {
		// SecondGenerationLevel1 : téléporteurs en (0, 7) et (14, 7)
		var level = new SecondGenerationLevel1();
		var screenData = LevelFixtures.screenDataOf(level);
		var left = new Position(0, 7);
		var right = new Position(14, 7);

		var path = Dijkstra.getShorterWay(left, right, screenData);

		// Limite connue : un fantôme traverse tout le labyrinthe au lieu de prendre
		// le téléporteur (1 pas)
		assertThat(path).hasSizeGreaterThan(2);
	}

	@Test
	@DisplayName("Limite connue : une case inaccessible comme cible fait planter le calcul")
	void unreachableTargetThrows() {
		var level = new SecondGenerationLevel1();
		var screenData = LevelFixtures.screenDataOf(level);
		var wall = level.getLstBlocks().stream().map(ScreenBlock::getCoordinate)
		        .filter(p -> screenData.getScreenBlock(p).isNotAccessible()).findFirst().orElseThrow();

		// Comportement actuel documenté : NullPointerException dans
		// UnitDijkstra.getShorterWay (aucune case précédente). Dans le jeu, la cible
		// est toujours Ladybug ou le point de régénération, donc toujours accessible.
		assertThatThrownBy(() -> Dijkstra.getShorterWay(level.getInitLadybugBlockPos(), wall, screenData))
		        .isInstanceOf(NullPointerException.class);
	}
}
