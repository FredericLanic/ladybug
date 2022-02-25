package com.kycox.game.controller.xboxone;

import java.util.Observable;
import java.util.Observer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.kycox.game.constant.Constants;
import com.kycox.game.contract.GameModelForController;
import com.kycox.game.properties.GameProperties;

@Named("XboxOneController")
public class XboxOneController implements Observer {

	private GameModelForController gameModelForController;
	@Inject
	private GameProperties gameProperties;

	int number = 0;
	@Inject
	private XboxRequest xboxOneLadybug;
	@Inject
	private XboxRequest xboxOneUnComputedGhost;

	@PostConstruct
	public void initialize() {
		xboxOneLadybug.setNbrXboxRequest(ConstantXboxOne.LADYBUG_XBOXONE);
		xboxOneUnComputedGhost.setNbrXboxRequest(ConstantXboxOne.GHOST_XBOXONE);
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
			System.exit(0); // FIXME : c'est au modèle de sortir proprement du jeu
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
		if (xboxOneLadybug.isRighRightStickt()) {
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
		if (xboxOneUnComputedGhost.isRighRightStickt()) {
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
		readXboxOneStates();
		manageBothXbosesOneCommon();
		if (gameModelForController.isGamePresentation()) {
			manageBothXboxesOneInPresentation();
		} else if (gameModelForController.isInGame()) {
			manageBothXboxesOneInGame();
			manageLadybugXboxOneInGame();
			manageUnComputedGhostXboxOneInGame();
		}
	}

	private void readXboxOneStates() {
		xboxOneLadybug.readCurrentState();
		xboxOneUnComputedGhost.readCurrentState();
	}

	@Override
	public void update(Observable gameModelForController, Object arg) {
		this.gameModelForController = (GameModelForController) gameModelForController;
		manageXboxesOneInGame();
	}

}
