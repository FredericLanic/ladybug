package com.kycox.game.engine.controller;

import com.kycox.game.constant.contract.LadybugForController;
import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.constant.xbox.XboxOneConstants;
import com.kycox.game.constant.contract.GameModelForController;
import com.kycox.game.constant.contract.GhostForController;
import com.kycox.game.dynamic.body.ghost.Ghost;
import com.kycox.game.dynamic.body.ladybug.Ladybug;
import com.kycox.game.engine.controller.xboxone.XBoxOneControllerManager;
import com.kycox.game.engine.controller.xboxone.XboxRequest;
import com.kycox.game.engine.message.GameMessaging;
import com.kycox.game.engine.model.EventGameModel;
import com.kycox.game.tools.os.WindowsHost;
import com.kycox.game.properties.GameProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Conditional(WindowsHost.class)
public class XboxOneController extends XBoxOneControllerManager implements ApplicationListener<EventGameModel> {

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
		Optional.of(gameModelForController.getLadybug())
				.filter(LadybugForController::isEatenAMegaPoint)
				.ifPresent(ladybug -> getControllerManager().doVibration(XboxOneConstants.LADYBUG_XBOXONE_INDEX, 0f, 0.5f, 100));

		Optional.of(gameModelForController.getLadybug())
				.filter(LadybugForController::isToBeTeleported)
				.ifPresent(ladybug -> getControllerManager().doVibration(XboxOneConstants.LADYBUG_XBOXONE_INDEX, 0f, 0.5f, 50));

		gameModelForController.getUnComputedGhost()
				.filter(GhostForController::isToBeTeleported)
				.ifPresent(ghost -> getControllerManager().doVibration(XboxOneConstants.GHOST_XBOXONE_INDEX, 0f, 0.5f, 50));

		gameModelForController.getUnComputedGhost()
				.filter(GhostForController::isDying)
				.ifPresent(ghost -> getControllerManager().doVibration(XboxOneConstants.GHOST_XBOXONE_INDEX, 0f, 0.5f, 100));
	}

	private boolean isOneXboxOneConnected() {
		return (xboxOneUnComputedGhost.isConnected() || xboxOneLadybug.isConnected());
	}

	private void manageBothXboxesOneCommon() {
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
			gameMessaging.addIamLadybug();
		}
		if (xboxOneUnComputedGhost.isRightStick()) {
			gameMessaging.addIamGhost();
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
			gameModelForController.setLadybugRequest(GameMainConstants.POINT_RIGHT);
		}
		if (xboxOneLadybug.isLeftLeftStick() || xboxOneLadybug.isLeftRightStick()	) {
			gameModelForController.setLadybugRequest(GameMainConstants.POINT_LEFT);
		}
		if (xboxOneLadybug.isUpLeftStick() || xboxOneLadybug.isUpRightStick()) {
			gameModelForController.setLadybugRequest(GameMainConstants.POINT_UP);
		}
		if (xboxOneLadybug.isDownLeftStick() || xboxOneLadybug.isDownRightStick()) {
			gameModelForController.setLadybugRequest(GameMainConstants.POINT_DOWN);
		}
	}

	private void manageUnComputedGhostXboxOneInGame() {
		if (!xboxOneUnComputedGhost.isConnected()) {
			return;
		}
		if (xboxOneUnComputedGhost.isRightRightStick() || xboxOneUnComputedGhost.isRightLeftStick()) {
			gameModelForController.setGhostRequest(GameMainConstants.POINT_RIGHT);
		}
		if (xboxOneUnComputedGhost.isLeftRightStick() || xboxOneUnComputedGhost.isLeftLeftStick()) {
			gameModelForController.setGhostRequest(GameMainConstants.POINT_LEFT);
		}
		if (xboxOneUnComputedGhost.isUpRightStick() || xboxOneUnComputedGhost.isUpLeftStick()) {
			gameModelForController.setGhostRequest(GameMainConstants.POINT_UP);
		}
		if (xboxOneUnComputedGhost.isDownRightStick() || xboxOneUnComputedGhost.isDownLeftStick()) {
			gameModelForController.setGhostRequest(GameMainConstants.POINT_DOWN);
		}
	}

	private void manageXboxesOneInGame() {
		gameModelForController.setAtLeastOneXboxOneConnected(isOneXboxOneConnected());
		setProgramMessagesAccordingXboxOneConnectionOrDisconnection();
		readXboxOneStates();
		manageBothXboxesOneCommon();
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
		xboxOneLadybug.readCurrentState(getControllerManager().getState(XboxOneConstants.LADYBUG_XBOXONE_INDEX));
		xboxOneUnComputedGhost.readCurrentState(getControllerManager().getState(XboxOneConstants.GHOST_XBOXONE_INDEX));
	}

	private void setProgramMessagesAccordingXboxOneConnectionOrDisconnection() {
		if (xboxOneLadybug.isHasBeenConnected()) {
			gameMessaging.addXboxLadybugConnected();
		}
		if (xboxOneLadybug.isHasBeenDisConnected()) {
			gameMessaging.addXboxLadybugDisconnected();
		}
		if (xboxOneUnComputedGhost.isHasBeenConnected()) {
			gameMessaging.addXboxGhostConnected();
		}
		if (xboxOneUnComputedGhost.isHasBeenDisConnected()) {
			gameMessaging.addXboxGhostDisconnected();
		}
	}

	@Override
	public void onApplicationEvent(EventGameModel event) {
		Object obj = event.getSource();
		if (obj instanceof GameModelForController eventGameModelForController) {
			gameModelForController = eventGameModelForController;
			manageXboxesOneInGame();
		}
	}
}
