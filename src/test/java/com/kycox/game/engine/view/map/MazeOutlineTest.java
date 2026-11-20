package com.kycox.game.engine.view.map;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.kycox.game.constant.level.data.SecondGenerationLevel1;
import com.kycox.game.engine.model.GameSnapshotFactory;
import com.kycox.game.testutil.LevelFixtures;
import com.kycox.game.tools.Position;

/**
 * Tests de {@link MazeOutline}, le contour des murs construit par la vue à
 * partir du labyrinthe (étape 4.4).
 */
class MazeOutlineTest {

	static Stream<Arguments> levels() {
		return LevelFixtures.activeLevels().stream().map(l -> Arguments.of(LevelFixtures.nameOf(l), l));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("levels")
	@DisplayName("le contour couvre chaque case du labyrinthe, avec la même forme (mur ou pas)")
	void theOutlineCopiesEveryBlock(String name, com.kycox.game.constant.contract.LevelStructure level) {
		var maze = LevelFixtures.mazeOf(level);
		var outline = new MazeOutline().of(GameSnapshotFactory.mazeOf(maze));

		assertThat(maze.getScreenBlocks()).allSatisfy(block -> {
			var drawn = outline.getBlock(block.getCoordinate());
			assertThat(drawn).isNotNull();
			assertThat(drawn.isNotAccessible()).isEqualTo(block.isNotAccessible());
		});
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("levels")
	@DisplayName("les bords extérieurs n'ont pas de bordure (ils sont dessinés à part)")
	void outerBordersAreRemoved(String name, com.kycox.game.constant.contract.LevelStructure level) {
		var maze = LevelFixtures.mazeOf(level);
		var outline = new MazeOutline().of(GameSnapshotFactory.mazeOf(maze));
		var lastX = outline.getNbrBlocksByLine() - 1;
		var lastY = outline.getNbrLines() - 1;

		for (var y = 0; y <= lastY; y++) {
			assertThat(outline.getBlock(new Position(0, y)).isBorderLeft()).isFalse();
			assertThat(outline.getBlock(new Position(lastX, y)).isBorderRight()).isFalse();
		}
		for (var x = 0; x <= lastX; x++) {
			assertThat(outline.getBlock(new Position(x, 0)).isBorderUp()).isFalse();
			assertThat(outline.getBlock(new Position(x, lastY)).isBorderDown()).isFalse();
		}
	}

	@Test
	@DisplayName("hors du labyrinthe, il n'y a pas de case")
	void noBlockOutsideTheMaze() {
		var outline = new MazeOutline().of(GameSnapshotFactory.mazeOf(LevelFixtures.mazeOf(new SecondGenerationLevel1())));

		assertThat(outline.getBlock(new Position(-1, 0))).isNull();
		assertThat(outline.getBlock(new Position(0, outline.getNbrLines()))).isNull();
	}

	@Test
	@DisplayName("manger une gomme ne reconstruit pas le contour ; un nouveau niveau, si")
	void theOutlineIsRebuiltOnlyWhenTheLayoutChanges() {
		var maze = LevelFixtures.mazeOf(new SecondGenerationLevel1());
		var outline = new MazeOutline();
		var origin = new Position(0, 0);
		OutlineBlock before = outline.of(GameSnapshotFactory.mazeOf(maze)).getBlock(origin);

		maze.getScreenBlock(origin).removePoint();
		assertThat(outline.of(GameSnapshotFactory.mazeOf(maze)).getBlock(origin)).isSameAs(before);

		maze.setLevelMap(1, false);
		assertThat(outline.of(GameSnapshotFactory.mazeOf(maze)).getBlock(origin)).isNotSameAs(before);
	}
}
