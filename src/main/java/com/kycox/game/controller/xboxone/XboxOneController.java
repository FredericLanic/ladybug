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
	private XboxRequest xboxRequestLadybug;
	@Inject
	private XboxRequest xboxRequestUnComputedGhost;

	@PostConstruct
	public void initialize() {
		xboxRequestLadybug.setNbrXboxRequest(ConstanteXboxOne.LADYBUG_XBOXONE);
		xboxRequestUnComputedGhost.setNbrXboxRequest(ConstanteXboxOne.GHOST_XBOXONE);
	}

	private void manageBothXbosesOneCommon() {
		if (xboxRequestLadybug.isDpadUp() || xboxRequestUnComputedGhost.isDpadUp()) {
			gameModelForController.startStopSoundActive();
		}
		if (xboxRequestLadybug.isDpadDown() || xboxRequestUnComputedGhost.isDpadDown()) {
			gameProperties.changeLadybugSkin();
		}
		if (xboxRequestLadybug.isDpadRight() || xboxRequestUnComputedGhost.isDpadRight()) {
			gameProperties.changeGhostHeadBand();
		}
		if (xboxRequestLadybug.isDpadLeft() || xboxRequestUnComputedGhost.isDpadLeft()) {
			gameProperties.changeGhostHat();
		}
	}

	private void manageBothXboxesOneInGame() {
		if (xboxRequestLadybug.isBButton() || xboxRequestUnComputedGhost.isBButton()) {
			gameModelForController.forceStopGame();
		}
	}

	private void manageBothXboxesOneInPresentation() {
		if (xboxRequestLadybug.isAButton() || xboxRequestUnComputedGhost.isAButton()) {
			gameModelForController.startGame();
		}
		if (xboxRequestLadybug.isBButton() || xboxRequestUnComputedGhost.isBButton()) {
			System.exit(0); // FIXME : c'est au modèle de sortir proprement du jeu
		}
		if (xboxRequestLadybug.isXButton() || xboxRequestUnComputedGhost.isXButton()) {
			gameModelForController.setMultiPlayers(false);
		}
		if (xboxRequestLadybug.isYButton() || xboxRequestUnComputedGhost.isYButton()) {
			gameModelForController.setMultiPlayers(true);
		}
		gameModelForController
		        .setShowHelpForXboxes(xboxRequestLadybug.isBackButton() || xboxRequestUnComputedGhost.isBackButton());
	}

	private void manageLadybugXboxOneInGame() {
		if (!xboxRequestLadybug.isConnected()) {
			return;
		}
		if (xboxRequestLadybug.isRighRightStickt()) {
			gameModelForController.setLadybugRequest(Constants.POINT_RIGHT);
		}
		if (xboxRequestLadybug.isLeftRightStick()) {
			gameModelForController.setLadybugRequest(Constants.POINT_LEFT);
		}
		if (xboxRequestLadybug.isUpRightStick()) {
			gameModelForController.setLadybugRequest(Constants.POINT_UP);
		}
		if (xboxRequestLadybug.isDownRightStick()) {
			gameModelForController.setLadybugRequest(Constants.POINT_DOWN);
		}
	}

	private void manageUnComputedGhostXboxOneInGame() {
		if (!xboxRequestUnComputedGhost.isConnected()) {
			return;
		}
		if (xboxRequestUnComputedGhost.isRighRightStickt()) {
			gameModelForController.setGhostRequest(Constants.POINT_RIGHT);
		}
		if (xboxRequestUnComputedGhost.isLeftRightStick()) {
			gameModelForController.setGhostRequest(Constants.POINT_LEFT);
		}
		if (xboxRequestUnComputedGhost.isUpRightStick()) {
			gameModelForController.setGhostRequest(Constants.POINT_UP);
		}
		if (xboxRequestUnComputedGhost.isDownRightStick()) {
			gameModelForController.setGhostRequest(Constants.POINT_DOWN);
		}
	}

	private void manageXboxesOneInGame() {
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
		xboxRequestLadybug.readCurrentState();
		xboxRequestUnComputedGhost.readCurrentState();
	}

	@Override
	public void update(Observable gameModelForController, Object arg) {
		this.gameModelForController = (GameModelForController) gameModelForController;
		manageXboxesOneInGame();
	}
}
