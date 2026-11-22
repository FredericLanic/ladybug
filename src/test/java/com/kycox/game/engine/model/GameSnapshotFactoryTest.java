package com.kycox.game.engine.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.kycox.game.constant.game.GameStatus;
import com.kycox.game.testutil.GameDriver;
import com.kycox.game.tools.Position;

/**
 * Tests de {@link GameSnapshotFactory} : la photo de la partie reprend bien le
 * modèle, et personne ne peut la modifier (étape 4.6).
 */
class GameSnapshotFactoryTest {
	@TempDir
	Path saveDirectory;

	private GameDriver game;

	@BeforeEach
	void startGame() {
		game = GameDriver.start(42, saveDirectory);
		game.beatsUntil(GameStatus.PROGRAM_PRESENTATION, 200);
		game.keys().startGame();
		game.keys().initializeLevelNumAndStartGame(true);
		game.beatsUntil(GameStatus.IN_GAME, 500);
	}

	@AfterEach
	void closeGame() {
		game.close();
	}

	@Test
	@DisplayName("la photo reprend le statut, le score, Ladybug et les fantômes du modèle")
	void theSnapshotMirrorsTheModel() {
		var model = game.model();
		var snapshot = GameSnapshotFactory.of(model);

		assertThat(snapshot.status()).isEqualTo(GameStatus.IN_GAME);
		assertThat(snapshot.isInGame()).isTrue();
		assertThat(snapshot.numLevel()).isEqualTo(model.getCurrentProgramStatus().getNumLevel());
		assertThat(snapshot.score()).isEqualTo(model.getGameScore().getScore());
		assertThat(snapshot.ladybug().position()).isEqualTo(model.getLadybug().getPosition());
		assertThat(snapshot.ladybug().leftLifes()).isEqualTo(model.getLadybug().getLeftLifes());
		assertThat(snapshot.ghosts()).extracting(g -> g.name())
		        .containsExactlyElementsOf(model.getGroupGhosts().getGhosts().stream().map(g -> g.getName()).toList());
		assertThat(snapshot.litLampMode()).isEqualTo(model.getLevelState().isLitLampMode());
		assertThat(snapshot.levelRendering()).isEqualTo(model.getLevelState().getLevelRendering());
	}

	@Test
	@DisplayName("chaque case de la photo est la case du labyrinthe à la même position")
	void everyCellMatchesTheMaze() {
		var maze = game.model().getMaze();
		var snapshot = GameSnapshotFactory.of(game.model()).maze();

		assertThat(snapshot.layoutVersion()).isEqualTo(maze.getLayoutVersion());
		assertThat(snapshot.cells()).hasSize(snapshot.nbrBlocksByLine() * snapshot.nbrLines());
		for (var y = 0; y < snapshot.nbrLines(); y++) {
			for (var x = 0; x < snapshot.nbrBlocksByLine(); x++) {
				var position = new Position(x, y);
				var cell = snapshot.cellAt(position);
				var block = maze.getScreenBlock(position);
				assertThat(cell.coordinate()).isEqualTo(position);
				assertThat(cell.point()).isEqualTo(block.isPoint());
				assertThat(cell.notAccessible()).isEqualTo(block.isNotAccessible());
				assertThat(cell.borderLeft()).isEqualTo(block.isBorderLeft());
				assertThat(cell.borderDown()).isEqualTo(block.isBorderDown());
			}
		}
		assertThat(snapshot.cellAt(new Position(-1, 0))).isNull();
		assertThat(snapshot.cellAt(new Position(0, snapshot.nbrLines()))).isNull();
	}

	@Test
	@DisplayName("la photo ne bouge plus quand le jeu continue, et ses listes ne se modifient pas")
	void theSnapshotIsFrozen() {
		var snapshot = GameSnapshotFactory.of(game.model());
		var ladybugBefore = snapshot.ladybug();
		var cellsBefore = snapshot.maze().cells();

		game.beats(100);

		assertThat(snapshot.ladybug()).isSameAs(ladybugBefore);
		assertThat(snapshot.maze().cells()).isSameAs(cellsBefore);
		assertThatThrownBy(() -> snapshot.ghosts().clear()).isInstanceOf(UnsupportedOperationException.class);
		assertThatThrownBy(() -> snapshot.maze().cells().clear()).isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	@DisplayName("en solo, aucun fantôme n'est joué par un humain")
	void noHumanGhostInSoloMode() {
		var snapshot = GameSnapshotFactory.of(game.model());
		assertThat(snapshot.humanGhost()).isEmpty();
		assertThat(snapshot.nbrPlayers()).isEqualTo(1);
	}

	/** Le salon (20.2) arrive dans la photo : c'est elle que l'écran « rejoindre » lira. */
	@Test
	void theLobbyIsInTheSnapshot() {
		var lobby = game.bean(Lobby.class);
		lobby.join(com.kycox.game.input.InputSource.KEYBOARD_ZQSD);
		lobby.confirm(com.kycox.game.input.InputSource.KEYBOARD_ZQSD);

		var snapshot = GameSnapshotFactory.of(game.model());

		// le premier joueur prend Ladybug (C56) ; 4 places : Ladybug et 3 fantômes (C52)
		assertThat(snapshot.lobby().maxSeats()).isEqualTo(4);
		assertThat(snapshot.lobby().seats()).containsExactly(
		        new com.kycox.game.snapshot.LobbySnapshot.SeatSnapshot("KEYBOARD_ZQSD", "Ladybug", true));
	}
}
