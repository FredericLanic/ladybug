package com.kycox.game.controller.xboxone;

import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;
import javax.inject.Named;

import com.kycox.game.constant.Constants;
import com.kycox.game.contract.GameModelForController;
import com.kycox.game.contract.GhostForController;
import com.kycox.game.message.GameMessages;
import com.kycox.game.message.GameMessaging;
import com.kycox.game.properties.GameProperties;

@Named("XboxOneController")
public class XboxOneController extends XBoxOneControllerManager implements Observer {

	@Inject
	private GameMessaging gameMessaging;
	private GameModelForController gameModelForController;
	@Inject
	private GameProperties gameProperties;
	@Inject
	private XboxRequest xboxOneLadybug;
	@Inject
	private XboxRequest xboxOneUnComputedGhost;

	private void doVibrationsInGame() {
		var ladybugForController = gameModelForController.getLadybug();
		if (ladybugForController.isToBeTeleported() || ladybugForController.isEatenAMegaPoint()) {
			getControllerManager().doVibration(ConstantXboxOne.LADYBUG_XBOXONE_INDEX, 0f, 0.5f, 100);
		}

		var unComputedGhost = gameModelForController.getUnComputedGhost();
		if (!unComputedGhost.isEmpty()) {
			GhostForController ghostForController = unComputedGhost.get();
			if (ghostForController.isToBeTeleported()) {
				getControllerManager().doVibration(ConstantXboxOne.GHOST_XBOXONE_INDEX, 0f, 0.5f, 100);
			}
			if (ghostForController.isDying()) {
				getControllerManager().doVibration(ConstantXboxOne.GHOST_XBOXONE_INDEX, 0f, 0.5f, 35);
			}
		}
	}

	private boolean isOneXboxOneConnected() {
		return (xboxOneUnComputedGhost.isConnected() || xboxOneLadybug.isConnected());
	}

	private void manageBothXbosesOneCommon() {
		if (xboxOneLadybug.isDpadUp() || xboxOneUnComputedGhost.isDpadUp()) {
			gameModelForController.startStopSoundActive();
		}
		if (xboxOneLadybug.isDpadDown() || xboxOneUnComputedGhost.isDpadDown()) {
			gameProperties.changeLadybugSkin();
		}
		if (xboxOneLadybug.isDpadRight() || xboxOneUnComputedGhost.isDpadRight()) {
			gameProperties.changeGhostHeadBand();
		}
		if (xboxOneLadybug.isDpadLeft() || xboxOneUnComputedGhost.isDpadLeft()) {
			gameProperties.changeGhostHat();
		}
	}

	private void manageBothXboxesOneInGame() {
		if (xboxOneLadybug.isBButton() || xboxOneUnComputedGhost.isBButton()) {
			gameModelForController.forceStopGame();
		}
		if (xboxOneLadybug.isStart() || xboxOneUnComputedGhost.isStart()) {
			gameModelForController.setGameInPause();
		}
	}

	private void manageBothXboxesOneInPresentation() {
		if (xboxOneLadybug.isAButton() || xboxOneUnComputedGhost.isAButton()) {
			gameModelForController.startGame();
		}
		if (xboxOneLadybug.isBButton() || xboxOneUnComputedGhost.isBButton()) {
			gameModelForController.programForceExit();
		}
		if (xboxOneLadybug.isXButton() || xboxOneUnComputedGhost.isXButton()) {
			gameModelForController.setMultiPlayers(false);
		}
		if (xboxOneLadybug.isYButton() || xboxOneUnComputedGhost.isYButton()) {
			gameModelForController.setMultiPlayers(true);
		}
		gameModelForController
		        .setShowHelpForXboxes(xboxOneLadybug.isBackButton() || xboxOneUnComputedGhost.isBackButton());
	}

	private void manageLadybugXboxOneInGame() {
		if (!xboxOneLadybug.isConnected()) {
			return;
		}
		if (xboxOneLadybug.isRightRightStick()) {
			gameModelForController.setLadybugRequest(Constants.POINT_RIGHT);
		}
		if (xboxOneLadybug.isLeftRightStick()) {
			gameModelForController.setLadybugRequest(Constants.POINT_LEFT);
		}
		if (xboxOneLadybug.isUpRightStick()) {
			gameModelForController.setLadybugRequest(Constants.POINT_UP);
		}
		if (xboxOneLadybug.isDownRightStick()) {
			gameModelForController.setLadybugRequest(Constants.POINT_DOWN);
		}
	}

	private void manageUnComputedGhostXboxOneInGame() {
		if (!xboxOneUnComputedGhost.isConnected()) {
			return;
		}
		if (xboxOneUnComputedGhost.isRightRightStick()) {
			gameModelForController.setGhostRequest(Constants.POINT_RIGHT);
		}
		if (xboxOneUnComputedGhost.isLeftRightStick()) {
			gameModelForController.setGhostRequest(Constants.POINT_LEFT);
		}
		if (xboxOneUnComputedGhost.isUpRightStick()) {
			gameModelForController.setGhostRequest(Constants.POINT_UP);
		}
		if (xboxOneUnComputedGhost.isDownRightStick()) {
			gameModelForController.setGhostRequest(Constants.POINT_DOWN);
		}
	}

	private void manageXboxesOneInGame() {
		gameModelForController.setAtLeastOneXboxOneConnected(isOneXboxOneConnected());
		setProgramMessagesAccordingXboxOneConnectionOrDisconnection();
		readXboxOneStates();
		manageBothXbosesOneCommon();
		if (gameModelForController.isGamePresentation()) {
			manageBothXboxesOneInPresentation();
		} else if (gameModelForController.isInGame()) {
			manageBothXboxesOneInGame();
			manageLadybugXboxOneInGame();
			manageUnComputedGhostXboxOneInGame();
			doVibrationsInGame();
		}
	}

	private void readXboxOneStates() {
		xboxOneLadybug.readCurrentState(getControllerManager().getState(ConstantXboxOne.LADYBUG_XBOXONE_INDEX));
		xboxOneUnComputedGhost.readCurrentState(getControllerManager().getState(ConstantXboxOne.GHOST_XBOXONE_INDEX));
	}

	private void setProgramMessagesAccordingXboxOneConnectionOrDisconnection() {
		if (xboxOneLadybug.isHasBeenConnected()) {
			gameMessaging.put(GameMessages.XBOX_LADYBUG_CONNECTION.getMessage());
		}
		if (xboxOneLadybug.isHasBeenDisConnected()) {
			gameMessaging.put(GameMessages.XBOX_LADYBUG_DISCONNECTION.getMessage());
		}
		if (xboxOneUnComputedGhost.isHasBeenConnected()) {
			gameMessaging.put(GameMessages.XBOX_GHOST_CONNECTION.getMessage());
		}
		if (xboxOneUnComputedGhost.isHasBeenDisConnected()) {
			gameMessaging.put(GameMessages.XBOX_GHOST_DISCONNECTION.getMessage());
		}
	}

	@Override
	public void update(Observable gameModelForController, Object arg) {
		this.gameModelForController = (GameModelForController) gameModelForController;
		manageXboxesOneInGame();
	}

}
