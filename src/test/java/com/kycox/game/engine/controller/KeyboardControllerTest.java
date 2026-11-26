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
	void oneKeepsTheSoloGame() {
		press(KeyEvent.VK_1);
		assertThat(game.model().getUnComputedGhost()).isEmpty();
	}

	/** C53 (20.3) : la touche 2 ouvre le salon, où chacun choisit son personnage. */
	@Test
	void twoOpensTheLobby() {
		press(KeyEvent.VK_2);
		assertThat(game.status()).isEqualTo(GameStatus.PROGRAM_LOBBY);
	}

	/**
	 * Le salon au clavier (20.3) : haut / bas pour la ligne, gauche / droite pour
	 * la valeur, Espace pour passer aux commandes ; Entrée lance la partie, avec les
	 * fantômes choisis joués par des humains ; Entrée n'y bascule pas le debug.
	 */
	@Test
	void theLobbyWithTheKeyboard() {
		press(KeyEvent.VK_2);
		var lobby = game.model().getLobby();
		press(KeyEvent.VK_DOWN); // ligne 2
		press(KeyEvent.VK_RIGHT); // premier fantôme, ZQSD
		press(KeyEvent.VK_RIGHT); // fantôme suivant
		var chosen = lobby.rows().get(1).character();
		press(KeyEvent.VK_SPACE); // colonne « commandes »
		press(KeyEvent.VK_RIGHT); // ZQSD -> IJKL
		assertThat(lobby.rows().get(1).device()).isEqualTo(InputSource.KEYBOARD_IJKL);

		press(KeyEvent.VK_ENTER);
		assertThat(preferences().isDebugMode()).as("Entrée ne bascule pas le debug dans le salon").isFalse();
		game.beat();
		assertThat(game.status()).isNotEqualTo(GameStatus.PROGRAM_LOBBY);
		assertThat(game.model().getGroupGhosts().getHumanGhosts()).extracting(g -> g.getName()).containsExactly(chosen);
		// 20.4 : chaque groupe de touches pilote le personnage choisi dans le salon
		assertThat(game.inputs().characterOf(InputSource.KEYBOARD_ARROWS)).contains("Ladybug");
		assertThat(game.inputs().characterOf(InputSource.KEYBOARD_IJKL)).contains(chosen);
		assertThat(game.inputs().characterOf(InputSource.KEYBOARD_ZQSD)).isEmpty();
	}

	/** 20.4 : Cox en ZQSD dans le salon ; en jeu, les flèches ne font plus rien, Q la fait aller à gauche. */
	@Test
	void ladybugFollowsTheKeysChosenInTheLobby() {
		press(KeyEvent.VK_2);
		press(KeyEvent.VK_RIGHT); // Cox : flèches -> ZQSD
		press(KeyEvent.VK_ENTER);
		game.beatsUntil(GameStatus.PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL, 10);
		press(KeyEvent.VK_N);
		game.beatsUntil(GameStatus.IN_GAME, 500);

		press(KeyEvent.VK_LEFT);
		assertThat(game.inputs().ladybug().hasRequest()).as("les flèches ne pilotent plus Cox").isFalse();
		var start = game.model().getLadybug().getPositionBlock();
		press(KeyEvent.VK_Q);
		game.beats(30);
		assertThat(game.model().getLadybug().getPositionBlock().x()).isLessThan(start.x());
		assertThat(game.inputs().ladybug().lastSource()).contains(InputSource.KEYBOARD_ZQSD);
	}

	/** Les boutons du salon (20.3 ter) : au-dessus de Cox, « jouer », que valide Espace. */
	@Test
	void thePlayButtonStartsTheGame() {
		press(KeyEvent.VK_2);
		press(KeyEvent.VK_UP); // au-dessus de Cox : la ligne des boutons, sur « jouer »
		press(KeyEvent.VK_SPACE);
		game.beat();
		assertThat(game.status()).isNotEqualTo(GameStatus.PROGRAM_LOBBY);
	}

	@Test
	void theBackButtonLeavesTheLobby() {
		press(KeyEvent.VK_2);
		press(KeyEvent.VK_UP);
		press(KeyEvent.VK_RIGHT); // « retour »
		press(KeyEvent.VK_SPACE);
		game.beat();
		assertThat(game.status()).isNotEqualTo(GameStatus.PROGRAM_LOBBY);
	}

	@Test
	void enterDoesNothingUntilTheLobbyIsReady() {
		press(KeyEvent.VK_2);
		// une ligne de fantôme attend sa manette (« appuie sur A ») : le salon n'est pas prêt
		game.model().getLobby().setConnectedPads(java.util.Set.of(InputSource.XBOX_PAD_2));
		press(KeyEvent.VK_DOWN); // ligne 2
		press(KeyEvent.VK_RIGHT); // un fantôme, ZQSD
		press(KeyEvent.VK_SPACE); // colonne « commandes »
		press(KeyEvent.VK_LEFT); // ZQSD -> (flèches de Cox) -> « appuie sur A »
		assertThat(game.model().getLobby().rows().get(1).waitingForPad()).isTrue();
		press(KeyEvent.VK_ENTER);
		game.beat();
		assertThat(game.status()).isEqualTo(GameStatus.PROGRAM_LOBBY);
	}

	@Test
	void escapeLeavesTheLobby() {
		press(KeyEvent.VK_2);
		press(KeyEvent.VK_ESCAPE);
		assertThat(game.status()).isEqualTo(GameStatus.PROGRAM_PRESENTATION);
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
