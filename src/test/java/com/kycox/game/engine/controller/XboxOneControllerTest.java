package com.kycox.game.engine.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.kycox.game.body.GhostsGroup;
import com.kycox.game.body.ghost.Blinky;
import com.kycox.game.body.ladybug.Ladybug;
import com.kycox.game.constant.contract.GameModelForController;
import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.constant.xbox.XboxOneConstants;
import com.kycox.game.engine.controller.xboxone.Pad;
import com.kycox.game.engine.controller.xboxone.PadState;
import com.kycox.game.engine.controller.xboxone.XBoxOneControllerManager;
import com.kycox.game.engine.controller.xboxone.XboxRequest;
import com.kycox.game.engine.message.GameMessaging;
import com.kycox.game.input.InputSource;
import com.kycox.game.input.PlayerInput;
import com.kycox.game.input.PlayerInputs;
import com.kycox.game.preferences.LocalPreferences;
import com.kycox.game.properties.GameProperties;
import com.kycox.game.snapshot.GameSnapshot;
import com.kycox.game.testutil.Snapshots;

/**
 * Boutons de la manette → actions sur le jeu (étapes 3.5.3d et 3.5.5), avec de
 * fausses manettes : aucune manette n'a besoin d'être branchée.
 */
class XboxOneControllerTest {
	private final XboxRequest ladybugPad = new XboxRequest();
	private final XboxRequest ghostPad = new XboxRequest();
	private final GameModelForController model = mock(GameModelForController.class);
	private final XBoxOneControllerManager padManager = mock(XBoxOneControllerManager.class);
	private final GameMessaging messaging = mock(GameMessaging.class);
	private final GameProperties properties = mock(GameProperties.class);
	private final LocalPreferences preferences = new LocalPreferences();
	private final Ladybug ladybug = mock(Ladybug.class);
	/** La boîte aux lettres de Ladybug (5.1) : la manette y écrit directement. */
	private final PlayerInput ladybugInput = new PlayerInput(() -> "Ladybug");
	/** La boîte aux lettres du fantôme humain (5.2) : la manette 2 y écrit directement. */
	private final PlayerInput ghostInput = new PlayerInput(() -> "Blinky");
	private final GhostsGroup ghostsGroup = mock(GhostsGroup.class);
	private XboxOneController controller;
	/** La photo de la partie que reçoit la manette (4.6) : rien de spécial par défaut. */
	private GameSnapshot snapshot = Snapshots.inGame(Snapshots.ladybug(false, false), List.of());

	@BeforeEach
	void setUp() {
		when(ladybug.getPlayerInput()).thenReturn(ladybugInput);
		var humanGhost = mock(Blinky.class);
		when(humanGhost.getPlayerInput()).thenReturn(ghostInput);
		when(ghostsGroup.getHumanGhosts()).thenReturn(List.of(humanGhost));
		controller = new XboxOneController(padManager, messaging, properties, preferences,
		        new PlayerInputs(ladybug, ghostsGroup), ladybugPad, ghostPad);
		when(model.getLadybug()).thenReturn(ladybug);
		when(model.getUnComputedGhost()).thenReturn(Optional.empty());
		// comme au démarrage du jeu : les manettes sont lues une première fois au repos ;
		// depuis D28 (20.1), un bouton déjà enfoncé au branchement ne compte pas
		ladybugPad.readCurrentState(Pad.connected().build());
		ghostPad.readCurrentState(Pad.connected().build());
	}

	private void press(PadState ladybugState, PadState ghostState) {
		ladybugPad.readCurrentState(ladybugState);
		ghostPad.readCurrentState(ghostState);
		controller.handleRequests(model, snapshot);
	}

	private void press(Pad ladybugState) {
		press(ladybugState.build(), PadState.disconnected());
	}

	// --- écran de présentation

	@Test
	void presentationButtons() {
		when(model.isProgramPresentation()).thenReturn(true);
		press(Pad.connected().a().x().back());
		verify(model).startGame();
		verify(model).setMultiPlayers(false);
		assertThat(preferences.isShowHelpForXboxes()).isTrue();
	}

	@Test
	void presentationYOpensTheLobbyAndBQuits() {
		when(model.isProgramPresentation()).thenReturn(true);
		press(Pad.connected().y().b());
		verify(model).openLobby(); // C53 : Y ouvre le salon
		verify(model).programForceExit();
		assertThat(preferences.isShowHelpForXboxes()).isFalse();
	}

	// --- question « reprendre le niveau ? »

	@Test
	void keepPreviousLevelQuestion() {
		// convention Xbox (20.1) : A = oui, reprendre ; B = non, nouvelle partie
		when(model.isProgramAskKeepPreviousGameLevel()).thenReturn(true);
		press(Pad.connected().a());
		verify(model).initializeLevelNumAndStartGame(false);

		press(Pad.connected().b());
		verify(model).initializeLevelNumAndStartGame(true);
	}

	// --- en partie

	@Test
	void leftStickMovesLadybugInEveryDirection() {
		when(model.isInGame()).thenReturn(true);
		press(Pad.connected().leftStick(-1f, 0));
		assertThat(ladybugInput.requested()).isEqualTo(GameMainConstants.POINT_LEFT);
		press(Pad.connected().leftStick(1f, 0));
		assertThat(ladybugInput.requested()).isEqualTo(GameMainConstants.POINT_RIGHT);
		press(Pad.connected().leftStick(0, 1f)); // convention de PadState : positif = haut
		assertThat(ladybugInput.requested()).isEqualTo(GameMainConstants.POINT_UP);
		press(Pad.connected().leftStick(0, -1f));
		assertThat(ladybugInput.requested()).isEqualTo(GameMainConstants.POINT_DOWN);
		assertThat(ladybugInput.lastSource()).contains(InputSource.XBOX_PAD_1);
	}

	@Test
	void secondPadMovesTheHumanGhost() {
		when(model.isInGame()).thenReturn(true);
		press(PadState.disconnected(), Pad.connected().rightStick(-1f, 0).build());
		assertThat(ghostInput.requested()).isEqualTo(GameMainConstants.POINT_LEFT);
		press(PadState.disconnected(), Pad.connected().leftStick(1f, 0).build());
		assertThat(ghostInput.requested()).isEqualTo(GameMainConstants.POINT_RIGHT);
		press(PadState.disconnected(), Pad.connected().leftStick(0, 1f).build());
		assertThat(ghostInput.requested()).isEqualTo(GameMainConstants.POINT_UP);
		press(PadState.disconnected(), Pad.connected().leftStick(0, -1f).build());
		assertThat(ghostInput.requested()).isEqualTo(GameMainConstants.POINT_DOWN);
		assertThat(ghostInput.lastSource()).contains(InputSource.XBOX_PAD_2);
		assertThat(ladybugInput.hasRequest()).as("la manette 2 ne pilote pas Ladybug").isFalse();
	}

	@Test
	void startPausesAndBackAsksToEndTheGame() {
		when(model.isInGame()).thenReturn(true);
		press(Pad.connected().back().start());
		verify(model).setGameInPause();
		verify(model).askForceEndGame();
	}

	@Test
	void endGameQuestion() {
		// convention Xbox (20.1) : A = oui, quitter ; B = non, continuer
		when(model.isGameAskForceEndGame()).thenReturn(true);
		press(Pad.connected().b());
		verify(model).setInGame();
		press(Pad.connected().a());
		verify(model).forceStopGame();
	}

	@Test
	void vibrationsOnMegaPointAndTeleport() {
		when(model.isInGame()).thenReturn(true);
		snapshot = Snapshots.inGame(Snapshots.ladybug(true, true), List.of(Snapshots.humanGhost(true, true)));

		press(Pad.connected());

		verify(padManager).vibrate(XboxOneConstants.LADYBUG_XBOXONE_INDEX, 0f, 0.5f, 100);
		verify(padManager).vibrate(XboxOneConstants.LADYBUG_XBOXONE_INDEX, 0f, 0.5f, 50);
		verify(padManager).vibrate(XboxOneConstants.GHOST_XBOXONE_INDEX, 0f, 0.5f, 50);
		verify(padManager).vibrate(XboxOneConstants.GHOST_XBOXONE_INDEX, 0f, 0.5f, 100);
	}

	// --- partout

	@Test
	void dpadChangesSoundAndLooks() {
		press(Pad.connected().dpadUp().dpadDown().dpadLeft().dpadRight().rightStickClick());
		assertThat(preferences.isSoundActive()).isFalse();
		verify(messaging).addSoundMode(false);
		verify(properties).changeLadybugSkin();
		verify(properties).changeGhostHeadBand();
		verify(properties).changeGhostHat();
		verify(messaging).addIamLadybug();
	}

	@Test
	void ghostPadRightStickClickSaysIAmTheGhost() {
		press(PadState.disconnected(), Pad.connected().rightStickClick().build());
		verify(messaging).addIamGhost();
	}

	/**
	 * D28 (20.1) : un bouton tenu d'un écran à l'autre n'agit qu'une fois. Avec la
	 * convention Xbox, A sert à « reprendre » puis à « oui, quitter » : A tenu
	 * depuis la question ne doit pas confirmer la sortie demandée plus tard.
	 */
	@Test
	void aButtonHeldFromOneScreenToTheNextDoesNotActAgain() {
		var aHeld = Pad.connected().a();
		when(model.isProgramAskKeepPreviousGameLevel()).thenReturn(true);
		press(aHeld);
		verify(model).initializeLevelNumAndStartGame(false);

		// écrans de transition, puis en jeu : A toujours tenu
		when(model.isProgramAskKeepPreviousGameLevel()).thenReturn(false);
		press(aHeld);
		when(model.isInGame()).thenReturn(true);
		press(aHeld);
		press(Pad.connected().a().back()); // Back : « quitter la partie ? »
		verify(model).askForceEndGame();

		when(model.isInGame()).thenReturn(false);
		when(model.isGameAskForceEndGame()).thenReturn(true);
		press(aHeld);
		verify(model, never()).forceStopGame();

		press(Pad.connected()); // A relâché…
		press(aHeld); // … puis appuyé : là, oui
		verify(model).forceStopGame();
	}

	/**
	 * Le salon à la manette (20.3), une ligne en deux temps : personnage au stick
	 * gauche, A ; commandes au stick gauche, A ; le stick droit change de ligne et de
	 * colonne ; la croix n'y sert pas. Sur « appuie sur A », la manette qui appuie prend
	 * la ligne et vibre. B revient en arrière ; ≡ lance la partie ; la croix n'y
	 * change ni le son ni les apparences.
	 */
	@Test
	void theLobbyWithAPad() {
		// les deux manettes restent branchées : l'une agit, l'autre est au repos
		var lobby = new com.kycox.game.engine.model.Lobby(() -> List.of("Clyde", "Blinky", "Inky", "Pinky"));
		when(model.isProgramLobby()).thenReturn(true);
		when(model.getLobby()).thenReturn(lobby);
		var rest = Pad.connected().build();

		press(Pad.connected().rightStick(0, -1f).build(), rest); // stick droit en bas : ligne 2
		press(Pad.connected().dpadDown().build(), rest); // la croix n'agit pas dans le salon
		assertThat(lobby.activeRow()).isEqualTo(1);
		press(Pad.connected().leftStick(1f, 0).build(), rest); // stick gauche à droite : Clyde, ZQSD
		press(Pad.connected().a().build(), rest); // personnage validé : place aux commandes
		assertThat(lobby.activeColumn()).isEqualTo(com.kycox.game.engine.model.Lobby.Column.CONTROLS);
		press(Pad.connected().b().build(), rest); // B : retour au personnage
		assertThat(lobby.activeColumn()).isEqualTo(com.kycox.game.engine.model.Lobby.Column.CHARACTER);
		press(Pad.connected().a().build(), rest); // de nouveau : commandes
		press(Pad.connected().leftStick(-1f, 0).build(), rest); // ZQSD -> (flèches prises) -> « appuie sur A »
		assertThat(lobby.rows().get(1).waitingForPad()).isTrue();

		press(rest, Pad.connected().a().build()); // la manette 2 prend la ligne
		assertThat(lobby.rows().get(1).device()).isEqualTo(InputSource.XBOX_PAD_2);
		verify(padManager, org.mockito.Mockito.atLeastOnce()).vibrate(org.mockito.ArgumentMatchers.eq(1),
		        org.mockito.ArgumentMatchers.anyFloat(), org.mockito.ArgumentMatchers.anyFloat(),
		        org.mockito.ArgumentMatchers.anyInt());
		press(Pad.connected().a().build(), rest); // commandes validées : ligne suivante
		assertThat(lobby.activeRow()).isEqualTo(2);
		verify(messaging, never()).addSoundMode(org.mockito.ArgumentMatchers.anyBoolean());

		// stick droit : à droite les commandes, à gauche le personnage
		press(Pad.connected().rightStick(1f, 0).build(), rest);
		assertThat(lobby.activeColumn()).isEqualTo(com.kycox.game.engine.model.Lobby.Column.CONTROLS);
		press(rest, rest);
		press(Pad.connected().rightStick(-1f, 0).build(), rest);
		assertThat(lobby.activeColumn()).isEqualTo(com.kycox.game.engine.model.Lobby.Column.CHARACTER);

		// stick droit en haut / en bas : la ligne, comme la croix (convention PadState : positif = haut)
		var row = lobby.activeRow();
		press(Pad.connected().rightStick(0, -1f).build(), rest);
		assertThat(lobby.activeRow()).isEqualTo(row + 1);
		press(rest, rest);
		press(Pad.connected().rightStick(0, 1f).build(), rest);
		assertThat(lobby.activeRow()).isEqualTo(row);

		press(Pad.connected().start().build(), rest);
		verify(model).startFromLobby();
	}

	@Test
	void connectionMessages() {
		// manettes d'abord débranchées : ce test vérifie justement le branchement
		ladybugPad.readCurrentState(PadState.disconnected());
		ghostPad.readCurrentState(PadState.disconnected());
		press(Pad.connected().build(), Pad.connected().build());
		verify(messaging).addXboxLadybugConnected();
		verify(messaging).addXboxGhostConnected();
		assertThat(preferences.isAtLeastOneXboxOneConnected()).isTrue();

		press(PadState.disconnected(), PadState.disconnected());
		verify(messaging).addXboxLadybugDisconnected();
		verify(messaging).addXboxGhostDisconnected();
	}

	@Test
	void noPadMeansNoAction() {
		when(model.isInGame()).thenReturn(true);
		press(PadState.disconnected(), PadState.disconnected());
		assertThat(preferences.isAtLeastOneXboxOneConnected()).isFalse();
		assertThat(ladybugInput.hasRequest()).isFalse();
		assertThat(ghostInput.hasRequest()).isFalse();
	}
}
