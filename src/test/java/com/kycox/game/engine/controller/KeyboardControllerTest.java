package com.kycox.game.engine.controller;

import com.kycox.game.tools.Position;
import static org.assertj.core.api.Assertions.assertThat;

import java.awt.event.KeyEvent;
import java.nio.file.Path;

import javax.swing.JPanel;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.kycox.game.constant.game.GameStatus;
import com.kycox.game.engine.message.GameMessaging;
import com.kycox.game.input.InputSource;
import com.kycox.game.preferences.LocalPreferences;
import com.kycox.game.properties.GameProperties;
import com.kycox.game.testutil.GameDriver;

/**
 * Chaque touche du clavier, envoyée au vrai {@link KeyboardController} sur le vrai
 * jeu sans écran (étape 3.5.3c). Seul Échap sur l'écran de présentation n'est pas
 * testé : il ferme le programme ({@code System.exit}) ; le robot le vérifie.
 */
class KeyboardControllerTest {
	private static final JPanel SOURCE = new JPanel();

	@TempDir
	Path saveDirectory;

	private GameDriver game;
	private KeyboardController keyboard;

	@BeforeEach
	void startGame() {
		game = GameDriver.start(42, saveDirectory);
		keyboard = new KeyboardController(game.keys(), game.bean(GameProperties.class),
		        game.bean(LocalPreferences.class), game.bean(GameMessaging.class), game.inputs());
		game.beatsUntil(GameStatus.PROGRAM_PRESENTATION, 200);
		game.beat();
	}

	/** Réglages de ce poste : depuis 4.5, hors du modèle. */
	private LocalPreferences preferences() {
		return game.bean(LocalPreferences.class);
	}

	@AfterEach
	void closeGame() {
		game.close();
	}

	private void press(int keyCode) {
		keyboard.keyPressed(new KeyEvent(SOURCE, KeyEvent.KEY_PRESSED, 0, 0, keyCode, KeyEvent.CHAR_UNDEFINED));
	}

	private void release(int keyCode) {
		keyboard.keyReleased(new KeyEvent(SOURCE, KeyEvent.KEY_RELEASED, 0, 0, keyCode, KeyEvent.CHAR_UNDEFINED));
	}

	/** S puis N, comme un joueur : niveau 1, en jeu. */
	private void startLevelOne() {
		press(KeyEvent.VK_S);
		game.beatsUntil(GameStatus.PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL, 10);
		press(KeyEvent.VK_N);
		game.beatsUntil(GameStatus.IN_GAME, 500);
	}

	@Test
	void twoThenOneChooseTheNumberOfPlayers() {
		press(KeyEvent.VK_2);
		assertThat(game.model().getUnComputedGhost()).isPresent();
		press(KeyEvent.VK_1);
		assertThat(game.model().getUnComputedGhost()).isEmpty();
	}

	@Test
	void sThenNStartsLevelOne() {
		startLevelOne();
		assertThat(game.model().getCurrentProgramStatus().getNumLevel()).isEqualTo(1);
	}

	@Test
	void arrowsMoveLadybug() {
		startLevelOne();
		var start = game.model().getLadybug().getPositionBlock();
		press(KeyEvent.VK_LEFT);
		game.beats(30);
		assertThat(game.model().getLadybug().getPositionBlock().x()).isLessThan(start.x());
		assertThat(game.inputs().ladybug().lastSource()).contains(InputSource.KEYBOARD_ARROWS);
	}

	@Test
	void pOrPauseFreezesThenResumesTheGame() {
		startLevelOne();
		press(KeyEvent.VK_LEFT);
		game.beats(5);

		press(KeyEvent.VK_P);
		assertThat(game.model().getCurrentProgramStatus().isGameInPause()).isTrue();
		var frozen = game.model().getLadybug().getPosition();
		game.beats(50);
		assertThat(game.model().getLadybug().getPosition()).isEqualTo(frozen);

		press(KeyEvent.VK_PAUSE); // l'autre touche, même effet
		assertThat(game.model().getCurrentProgramStatus().isGameInPause()).isFalse();
		game.beats(20);
		assertThat(game.model().getLadybug().getPosition()).isNotEqualTo(frozen);
	}

	@Test
	void escapeThenNGoesBackToTheGame() {
		startLevelOne();
		press(KeyEvent.VK_ESCAPE);
		game.beat();
		assertThat(game.status()).isEqualTo(GameStatus.ASK_FORCE_END_GAME);
		press(KeyEvent.VK_N);
		game.beat();
		assertThat(game.status()).isEqualTo(GameStatus.IN_GAME);
	}

	@Test
	void escapeThenOEndsTheGame() {
		startLevelOne();
		press(KeyEvent.VK_ESCAPE);
		game.beat();
		press(KeyEvent.VK_O);
		game.beatsUntil(GameStatus.PROGRAM_PRESENTATION, 200);
	}

	@Test
	void f1ShowsTheHelpWhileHeldDown() {
		press(KeyEvent.VK_F1);
		assertThat(preferences().isShowHelpForKeys()).isTrue();
		release(KeyEvent.VK_F1);
		assertThat(preferences().isShowHelpForKeys()).isFalse();
	}

	@Test
	void f2SwitchesTheSound() {
		var before = preferences().isSoundActive();
		press(KeyEvent.VK_F2);
		assertThat(preferences().isSoundActive()).isNotEqualTo(before);
	}

	@Test
	void f3ToF5ChangeTheLooks() {
		var properties = game.bean(GameProperties.class);
		var skin = properties.hasLadybugSkin();
		var headband = properties.hasGhostHeadBand();
		var hat = properties.hasGhostHat();

		press(KeyEvent.VK_F3);
		press(KeyEvent.VK_F4);
		press(KeyEvent.VK_F5);

		assertThat(properties.hasLadybugSkin()).isNotEqualTo(skin);
		assertThat(properties.hasGhostHeadBand()).isNotEqualTo(headband);
		assertThat(properties.hasGhostHat()).isNotEqualTo(hat);
	}

	@Test
	void f6SwitchesTheLampModeInGame() {
		startLevelOne();
		var before = game.model().getLevelState().isLitLampMode();
		press(KeyEvent.VK_F6);
		assertThat(game.model().getLevelState().isLitLampMode()).isNotEqualTo(before);
	}

	@Test
	void f7CamouflagesTheGhosts() {
		var ghost = game.model().getGroupGhosts().getGhosts().get(0);
		var before = ghost.isCamouflage();
		press(KeyEvent.VK_F7);
		assertThat(ghost.isCamouflage()).isNotEqualTo(before);
	}

	@Test
	void enterSwitchesTheDebugMode() {
		var before = preferences().isDebugMode();
		press(KeyEvent.VK_ENTER);
		assertThat(preferences().isDebugMode()).isNotEqualTo(before);
	}
}
