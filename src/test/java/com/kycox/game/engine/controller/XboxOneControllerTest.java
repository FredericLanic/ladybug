package com.kycox.game.engine.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.kycox.game.constant.contract.GameModelForController;
import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.engine.controller.xboxone.PadState;
import com.kycox.game.engine.controller.xboxone.XBoxOneControllerManager;
import com.kycox.game.engine.controller.xboxone.XboxRequest;
import com.kycox.game.engine.message.GameMessaging;
import com.kycox.game.properties.GameProperties;

/**
 * Boutons de la manette → actions sur le jeu (étape 3.5.3d), avec une fausse
 * manette ({@link PadState}) : aucune manette n'a besoin d'être branchée.
 */
class XboxOneControllerTest {
	private final XboxRequest ladybugPad = new XboxRequest();
	private final XboxRequest ghostPad = new XboxRequest();
	private final GameModelForController model = mock(GameModelForController.class);
	private XboxOneController controller;

	@BeforeEach
	void setUp() {
		controller = new XboxOneController(mock(XBoxOneControllerManager.class), mock(GameMessaging.class),
		        mock(GameProperties.class), ladybugPad, ghostPad);
	}

	/** Une manette branchée, avec seulement les boutons ou le stick indiqués. */
	private static PadState pad(boolean a, boolean b, boolean x, boolean y, boolean start, float leftX, float leftY) {
		return new PadState(true, leftX, leftY, 0, 0, a, b, x, y, false, false, false, false, false, start, false,
		        false);
	}

	private void press(PadState ladybug) {
		ladybugPad.readCurrentState(ladybug);
		ghostPad.readCurrentState(PadState.disconnected());
		controller.handleRequests(model);
	}

	@Test
	void aStartsTheGameOnThePresentationScreen() {
		when(model.isProgramPresentation()).thenReturn(true);
		press(pad(true, false, false, false, false, 0, 0));
		verify(model).startGame();
	}

	@Test
	void yChoosesTwoPlayersOnThePresentationScreen() {
		when(model.isProgramPresentation()).thenReturn(true);
		press(pad(false, false, false, true, false, 0, 0));
		verify(model).setMultiPlayers(true);
	}

	@Test
	void leftStickMovesLadybugInGame() {
		when(model.isInGame()).thenReturn(true);
		when(model.getLadybug()).thenReturn(mock(com.kycox.game.body.ladybug.Ladybug.class));
		when(model.getUnComputedGhost()).thenReturn(java.util.Optional.empty());
		press(pad(false, false, false, false, false, -1f, 0));
		verify(model).setLadybugRequest(GameMainConstants.POINT_LEFT);
	}

	@Test
	void stickPushedUpMovesLadybugUp() {
		// convention de PadState : positif = vers le haut (Jamepad 2.x donne l'inverse)
		when(model.isInGame()).thenReturn(true);
		when(model.getLadybug()).thenReturn(mock(com.kycox.game.body.ladybug.Ladybug.class));
		when(model.getUnComputedGhost()).thenReturn(java.util.Optional.empty());
		press(pad(false, false, false, false, false, 0, 1f));
		verify(model).setLadybugRequest(GameMainConstants.POINT_UP);
	}

	@Test
	void startPausesAndXAsksToEndTheGame() {
		when(model.isInGame()).thenReturn(true);
		when(model.getLadybug()).thenReturn(mock(com.kycox.game.body.ladybug.Ladybug.class));
		when(model.getUnComputedGhost()).thenReturn(java.util.Optional.empty());
		press(pad(false, false, true, false, true, 0, 0));
		verify(model).setGameInPause();
		verify(model).askForceEndGame();
	}

	@Test
	void noPadMeansNoAction() {
		when(model.isInGame()).thenReturn(true);
		when(model.getLadybug()).thenReturn(mock(com.kycox.game.body.ladybug.Ladybug.class));
		when(model.getUnComputedGhost()).thenReturn(java.util.Optional.empty());
		ladybugPad.readCurrentState(PadState.disconnected());
		ghostPad.readCurrentState(PadState.disconnected());
		controller.handleRequests(model);
		verify(model).setAtLeastOneXboxOneConnected(false);
		verify(model, never()).setLadybugRequest(org.mockito.ArgumentMatchers.any());
	}
}
