package com.kycox.game.testutil;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.constant.game.GameStatus;

/**
 * Le banc d'essai pilote le vrai jeu, sans écran (étape 3.5.2a).
 */
class GameDriverTest {
	@TempDir
	Path saveDirectory;

	@Test
	void theGameGoesFromPresentationToInGameLikeWithTheKeyboard() {
		try (var game = GameDriver.start(42, saveDirectory)) {
			var presentation = game.beatsUntil(GameStatus.PROGRAM_PRESENTATION, 200);
			// 2,5 s d'écran d'accueil = 63 battements
			assertThat(presentation).isBetween(63, 66);

			game.beat();
			game.keys().startGame(); // touche S
			game.beatsUntil(GameStatus.PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL, 10);
			game.keys().initializeLevelNumAndStartGame(true); // touche N : niveau 1
			var startBeat = game.beatCount();
			var inGame = game.beatsUntil(GameStatus.IN_GAME, 500);

			// 2,5 s de début de partie + 4,2 s de musique ≈ 169 battements
			assertThat(inGame - startBeat).isBetween(165, 175);
		}
	}

	@Test
	void pressingStartBeforeThePresentationHasBeatenDoesNotCrash() {
		// dette D23 (corrigée en 4.2) : les actions des fantômes n'existaient qu'après le
		// premier battement de l'écran de présentation ; « S » pressé plus tôt plantait
		// (le clavier et la manette n'envoient « S » que pendant la présentation)
		try (var game = GameDriver.start(42, saveDirectory)) {
			game.beatsUntil(GameStatus.PROGRAM_PRESENTATION, 200);
			game.keys().startGame(); // touche S, avant le premier battement de présentation
			game.beatsUntil(GameStatus.PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL, 10);
			game.keys().initializeLevelNumAndStartGame(true);

			assertThat(game.beatsUntil(GameStatus.IN_GAME, 500)).isPositive();
		}
	}

	@Test
	void ladybugMovesWhenTheTestPressesAnArrow() {
		try (var game = GameDriver.start(42, saveDirectory)) {
			startLevelOne(game);
			var before = game.model().getLadybug().getPositionBlock();

			game.keys().setLadybugRequest(GameMainConstants.POINT_LEFT); // flèche gauche
			game.beats(30);

			assertThat(game.model().getLadybug().getPositionBlock()).isNotEqualTo(before);
		}
	}

	@Test
	void theSavedLevelGoesInTheTestDirectory() {
		try (var game = GameDriver.start(42, saveDirectory)) {
			startLevelOne(game);
		}
		assertThat(Files.exists(saveDirectory.resolve("numLevel.txt"))).isTrue();
	}

	static void startLevelOne(GameDriver game) {
		game.beatsUntil(GameStatus.PROGRAM_PRESENTATION, 200);
		game.beat();
		game.keys().startGame();
		game.beatsUntil(GameStatus.PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL, 10);
		game.keys().initializeLevelNumAndStartGame(true);
		game.beatsUntil(GameStatus.IN_GAME, 500);
	}
}
