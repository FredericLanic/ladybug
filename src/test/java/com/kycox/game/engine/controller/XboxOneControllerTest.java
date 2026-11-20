package com.kycox.game.engine.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
import com.kycox.game.preferences.LocalPreferences;
import com.kycox.game.properties.GameProperties;

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
	private XboxOneController controller;

	@BeforeEach
	void setUp() {
		controller = new XboxOneController(padManager, messaging, properties, preferences, ladybugPad, ghostPad);
		when(model.getLadybug()).thenReturn(ladybug);
		when(model.getUnComputedGhost()).thenReturn(Optional.empty());
	}

	private void press(PadState ladybugState, PadState ghostState) {
		ladybugPad.readCurrentState(ladybugState);
		ghostPad.readCurrentState(ghostState);
		controller.handleRequests(model);
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
	void presentationYChoosesTwoPlayersAndBQuits() {
		when(model.isProgramPresentation()).thenReturn(true);
		press(Pad.connected().y().b());
		verify(model).setMultiPlayers(true);
		verify(model).programForceExit();
		assertThat(preferences.isShowHelpForXboxes()).isFalse();
	}

	// --- question « reprendre le niveau ? »

	@Test
	void keepPreviousLevelQuestion() {
		when(model.isProgramAskKeepPreviousGameLevel()).thenReturn(true);
		press(Pad.connected().x());
		verify(model).initializeLevelNumAndStartGame(true);

		press(Pad.connected().y());
		verify(model).initializeLevelNumAndStartGame(false);
	}

	// --- en partie

	@Test
	void leftStickMovesLadybugInEveryDirection() {
		when(model.isInGame()).thenReturn(true);
		press(Pad.connected().leftStick(-1f, 0));
		verify(model).setLadybugRequest(GameMainConstants.POINT_LEFT);
		press(Pad.connected().leftStick(1f, 0));
		verify(model).setLadybugRequest(GameMainConstants.POINT_RIGHT);
		press(Pad.connected().leftStick(0, 1f)); // convention de PadState : positif = haut
		verify(model).setLadybugRequest(GameMainConstants.POINT_UP);
		press(Pad.connected().leftStick(0, -1f));
		verify(model).setLadybugRequest(GameMainConstants.POINT_DOWN);
	}

	@Test
	void secondPadMovesTheHumanGhost() {
		when(model.isInGame()).thenReturn(true);
		press(PadState.disconnected(), Pad.connected().rightStick(-1f, 0).build());
		verify(model).setGhostRequest(GameMainConstants.POINT_LEFT);
		press(PadState.disconnected(), Pad.connected().leftStick(1f, 0).build());
		verify(model).setGhostRequest(GameMainConstants.POINT_RIGHT);
		press(PadState.disconnected(), Pad.connected().leftStick(0, 1f).build());
		verify(model).setGhostRequest(GameMainConstants.POINT_UP);
		press(PadState.disconnected(), Pad.connected().leftStick(0, -1f).build());
		verify(model).setGhostRequest(GameMainConstants.POINT_DOWN);
	}

	@Test
	void startPausesAndXAsksToEndTheGame() {
		when(model.isInGame()).thenReturn(true);
		press(Pad.connected().x().start());
		verify(model).setGameInPause();
		verify(model).askForceEndGame();
	}

	@Test
	void endGameQuestion() {
		when(model.isGameAskForceEndGame()).thenReturn(true);
		press(Pad.connected().b());
		verify(model).setInGame();
		press(Pad.connected().y());
		verify(model).forceStopGame();
	}

	@Test
	void vibrationsOnMegaPointAndTeleport() {
		when(model.isInGame()).thenReturn(true);
		when(ladybug.isEatenAMegaPoint()).thenReturn(true);
		when(ladybug.isToBeTeleported()).thenReturn(true);
		var ghost = mock(Blinky.class);
		when(ghost.isToBeTeleported()).thenReturn(true);
		when(ghost.isDying()).thenReturn(true);
		when(model.getUnComputedGhost()).thenReturn(Optional.of(ghost));

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

	@Test
	void connectionMessages() {
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
		verify(model, never()).setLadybugRequest(any());
		verify(model, never()).setGhostRequest(any());
	}
}
