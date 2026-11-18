package com.kycox.game.constant.level;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Point;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.kycox.game.constant.contract.LevelStructure;
import com.kycox.game.engine.screendata.ScreenBlock;
import com.kycox.game.testutil.LevelFixtures;

/**
 * Cohérence de chacun des niveaux actifs : un niveau mal saisi (une case de
 * trop, un téléporteur sans retour, un départ dans un mur) est détecté ici, sans
 * lancer le jeu.
 */
class LevelConsistencyTest {

	static Stream<Arguments> levels() {
		return LevelFixtures.activeLevels().stream().map(l -> Arguments.of(LevelFixtures.nameOf(l), l));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("levels")
	@DisplayName("Le nombre de cases correspond à largeur × hauteur")
	void dimensionsMatchBlockCount(String name, LevelStructure level) {
		assertThat(level.getLstBlocks()).hasSize(level.getNbrBlocksByLine() * level.getNbrLines());
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("levels")
	@DisplayName("Ladybug et le point de régénération des fantômes sont sur des cases praticables")
	void startPositionsAreAccessible(String name, LevelStructure level) {
		var screenData = LevelFixtures.screenDataOf(level);
		assertThat(screenData.getScreenBlock(level.getInitLadybugBlockPos()))
		        .as("départ de Ladybug").isNotNull().matches(b -> !b.isNotAccessible() && !b.isBlocked());
		assertThat(screenData.getScreenBlock(level.getGhostRegenerateBlockPoint()))
		        .as("régénération des fantômes").isNotNull().matches(b -> !b.isNotAccessible() && !b.isBlocked());
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("levels")
	@DisplayName("Les fantômes peuvent rejoindre Ladybug depuis leur point de régénération")
	void ghostsCanReachLadybug(String name, LevelStructure level) {
		var screenData = LevelFixtures.screenDataOf(level);
		var distances = LevelFixtures.distancesFrom(level.getGhostRegenerateBlockPoint(), screenData);
		assertThat(distances).containsKey(level.getInitLadybugBlockPos());
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("levels")
	@DisplayName("Toutes les gommes sont atteignables depuis le départ de Ladybug")
	void allPointsAreReachable(String name, LevelStructure level) {
		var screenData = LevelFixtures.screenDataOf(level);
		var distances = LevelFixtures.distancesFrom(level.getInitLadybugBlockPos(), screenData);
		var points = level.getLstBlocks().stream().map(ScreenBlock::getCoordinate)
		        .filter(p -> screenData.getScreenBlock(p).isPoint()).toList();
		assertThat(points).as("un niveau sans gomme serait terminé avant d'avoir commencé").isNotEmpty();
		var unreachablePoints = points.stream().filter(p -> !distances.containsKey(p)).toList();
		assertThat(unreachablePoints).as("gommes inatteignables : le niveau ne pourrait jamais être terminé").isEmpty();
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("levels")
	@DisplayName("Chaque téléporteur a son retour, au bord du labyrinthe, sur une case praticable")
	void teleportsAreSymmetric(String name, LevelStructure level) {
		var screenData = LevelFixtures.screenDataOf(level);
		level.getTeleportPoints().forEach((from, to) -> {
			assertThat(level.getTeleportPoints()).as("retour de %s", from).containsEntry(to, from);
			assertThat(isOnEdge(from, level)).as("%s au bord", from).isTrue();
			assertThat(screenData.getScreenBlock(from).isNotAccessible()).as("%s praticable", from).isFalse();
		});
	}

	private static boolean isOnEdge(Point p, LevelStructure level) {
		return p.x == 0 || p.y == 0 || p.x == level.getNbrBlocksByLine() - 1 || p.y == level.getNbrLines() - 1;
	}
}
