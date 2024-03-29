package com.kycox.game.controller.xboxone;

import com.kycox.game.constant.Constants;
import com.kycox.game.contract.GameModelForController;
import com.kycox.game.contract.GhostForController;
import com.kycox.game.message.GameMessages;
import com.kycox.game.message.GameMessaging;
import com.kycox.game.properties.GameProperties;
import org.springframework.stereotype.Component;

import java.util.Observable;
import java.util.Observer;

@Component
public class XboxOneController extends XBoxOneControllerManager implements Observer {

	private final GameMessaging gameMessaging;
	private GameModelForController gameModelForController;
	private final GameProperties gameProperties;
	private final XboxRequest xboxOneLadybug;
	private final XboxRequest xboxOneUnComputedGhost;

	public XboxOneController(GameMessaging gameMessaging,
							 GameProperties gameProperties,
							 XboxRequest xboxOneLadybug,
							 XboxRequest xboxOneUnComputedGhost) {
		this.gameMessaging = gameMessaging;
		this.gameProperties = gameProperties;
		this.xboxOneLadybug = xboxOneLadybug;
		this.xboxOneUnComputedGhost = xboxOneUnComputedGhost;
	}

	private void doVibrationsInGame() {
		var ladybugForController = gameModelForController.getLadybug();
		if (ladybugForController.isToBeTeleported() || ladybugForController.isEatenAMegaPoint()) {
			getControllerManager().doVibration(ConstantXboxOne.LADYBUG_XBOXONE_INDEX, 0f, 0.5f, 100);
		}

		var unComputedGhost = gameModelForController.getUnComputedGhost();
		if (unComputedGhost.isPresent()) {
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
		if (xboxOneLadybug.isRightStick()) {
			gameMessaging.put(GameMessages.I_AM_LADYBUG.getMessage());
		}
		if (xboxOneUnComputedGhost.isRightStick()) {
			gameMessaging.put(GameMessages.I_AM_GHOST.getMessage());
		}
	}

	private void manageBothXboxesOneInGame() {
		if (xboxOneLadybug.isXButton() || xboxOneUnComputedGhost.isXButton()) {
			gameModelForController.askForceEndGame();
		}
		if (xboxOneLadybug.isStart() || xboxOneUnComputedGhost.isStart()) {
			gameModelForController.setGameInPause();
		}
	}

	private void manageBothXboxesOneInAskKeepPreviousGameLevel() {
		if (xboxOneLadybug.isXButton() || xboxOneUnComputedGhost.isXButton()) {
			gameModelForController.initializeLevelNumAndStartGame(true);
		}
		if (xboxOneLadybug.isYButton() || xboxOneUnComputedGhost.isYButton()) {
			gameModelForController.initializeLevelNumAndStartGame(false);
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
		if (xboxOneLadybug.isRightLeftStick() || xboxOneLadybug.isRightRightStick()) {
			gameModelForController.setLadybugRequest(Constants.POINT_RIGHT);
		}
		if (xboxOneLadybug.isLeftLeftStick() || xboxOneLadybug.isLeftRightStick()	) {
			gameModelForController.setLadybugRequest(Constants.POINT_LEFT);
		}
		if (xboxOneLadybug.isUpLeftStick() || xboxOneLadybug.isUpRightStick()) {
			gameModelForController.setLadybugRequest(Constants.POINT_UP);
		}
		if (xboxOneLadybug.isDownLeftStick() || xboxOneLadybug.isDownRightStick()) {
			gameModelForController.setLadybugRequest(Constants.POINT_DOWN);
		}
	}

	private void manageUnComputedGhostXboxOneInGame() {
		if (!xboxOneUnComputedGhost.isConnected()) {
			return;
		}
		if (xboxOneUnComputedGhost.isRightRightStick() || xboxOneUnComputedGhost.isRightLeftStick()) {
			gameModelForController.setGhostRequest(Constants.POINT_RIGHT);
		}
		if (xboxOneUnComputedGhost.isLeftRightStick() || xboxOneUnComputedGhost.isLeftLeftStick()) {
			gameModelForController.setGhostRequest(Constants.POINT_LEFT);
		}
		if (xboxOneUnComputedGhost.isUpRightStick() || xboxOneUnComputedGhost.isUpLeftStick()) {
			gameModelForController.setGhostRequest(Constants.POINT_UP);
		}
		if (xboxOneUnComputedGhost.isDownRightStick() || xboxOneUnComputedGhost.isDownLeftStick()) {
			gameModelForController.setGhostRequest(Constants.POINT_DOWN);
		}
	}

	private void manageXboxesOneInGame() {
		gameModelForController.setAtLeastOneXboxOneConnected(isOneXboxOneConnected());
		setProgramMessagesAccordingXboxOneConnectionOrDisconnection();
		readXboxOneStates();
		manageBothXbosesOneCommon();
		if (gameModelForController.isProgramPresentation()) {
			manageBothXboxesOneInPresentation();
		} else if(gameModelForController.isProgramAskKeepPreviousGameLevel()) {
			manageBothXboxesOneInAskKeepPreviousGameLevel();
		} else if (gameModelForController.isInGame()) {
			manageBothXboxesOneInGame();
			manageLadybugXboxOneInGame();
			manageUnComputedGhostXboxOneInGame();
			doVibrationsInGame();
		} else if(gameModelForController.isGameAskForceEndGame()) {
			manageBothXboxesOneInAskForceEndGame();
		}
	}

	private void manageBothXboxesOneInAskForceEndGame() {
		if (xboxOneLadybug.isBButton() || xboxOneUnComputedGhost.isBButton()) {
			gameModelForController.setInGame();
		}
		if (xboxOneLadybug.isYButton() || xboxOneUnComputedGhost.isYButton()) {
			gameModelForController.forceStopGame();
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
