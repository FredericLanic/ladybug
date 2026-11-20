package com.kycox.game.engine.controller;

import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.constant.xbox.XboxOneConstants;
import com.kycox.game.constant.contract.GameModelForController;
import com.kycox.game.engine.controller.xboxone.XBoxOneControllerManager;
import com.kycox.game.engine.controller.xboxone.XboxRequest;
import com.kycox.game.engine.message.GameMessaging;
import com.kycox.game.engine.model.EventGameModel;
import com.kycox.game.properties.GameProperties;
import com.kycox.game.input.InputSource;
import com.kycox.game.input.PlayerInputs;
import com.kycox.game.preferences.LocalPreferences;
import com.kycox.game.snapshot.GameSnapshot;
import com.kycox.game.snapshot.GhostSnapshot;
import org.springframework.context.ApplicationListener;
import com.kycox.game.launcher.GuiComponent;


/**
 * La manette Xbox pilote le jeu (comme le clavier, mais avec les boutons).
 *
 * <p>Étape 3.5.3d : active sur tous les systèmes (avant : Windows seulement) ; le
 * gestionnaire Jamepad est <b>utilisé</b> au lieu d'être hérité (composition, dette
 * D19). La traduction « boutons → actions » ({@link #handleRequests}) se teste
 * avec une fausse manette ({@code PadState}).
 */
@GuiComponent
public class XboxOneController implements ApplicationListener<EventGameModel> {

	private final XBoxOneControllerManager padManager;
	private final GameMessaging gameMessaging;
	private GameModelForController gameModelForController;
	private final GameProperties gameProperties;
	private final XboxRequest xboxOneLadybug;
	private final XboxRequest xboxOneUnComputedGhost;
	/** Réglages de ce poste (aide, son, manette branchée), écrits sans passer par le modèle (4.5). */
	private final LocalPreferences localPreferences;
	/** Les ordres de déplacement vont directement au joueur concerné (5.1). */
	private final PlayerInputs playerInputs;

	public XboxOneController(XBoxOneControllerManager padManager,
							 GameMessaging gameMessaging,
							 GameProperties gameProperties,
							 LocalPreferences localPreferences,
							 PlayerInputs playerInputs,
							 XboxRequest xboxOneLadybug,
							 XboxRequest xboxOneUnComputedGhost) {
		this.playerInputs = playerInputs;
		this.padManager = padManager;
		this.gameMessaging = gameMessaging;
		this.gameProperties = gameProperties;
		this.localPreferences = localPreferences;
		this.xboxOneLadybug = xboxOneLadybug;
		this.xboxOneUnComputedGhost = xboxOneUnComputedGhost;
	}

	/** Vibrations lues dans la photo de la partie (4.6), et non dans le modèle. */
	private void doVibrationsInGame(GameSnapshot snapshot) {
		if (snapshot.ladybug().eatenAMegaPoint()) {
			padManager.vibrate(XboxOneConstants.LADYBUG_XBOXONE_INDEX, 0f, 0.5f, 100);
		}
		if (snapshot.ladybug().toBeTeleported()) {
			padManager.vibrate(XboxOneConstants.LADYBUG_XBOXONE_INDEX, 0f, 0.5f, 50);
		}
		snapshot.humanGhost()
				.filter(GhostSnapshot::toBeTeleported)
				.ifPresent(ghost -> padManager.vibrate(XboxOneConstants.GHOST_XBOXONE_INDEX, 0f, 0.5f, 50));
		snapshot.humanGhost()
				.filter(GhostSnapshot::dying)
				.ifPresent(ghost -> padManager.vibrate(XboxOneConstants.GHOST_XBOXONE_INDEX, 0f, 0.5f, 100));
	}

	private boolean isOneXboxOneConnected() {
		return (xboxOneUnComputedGhost.isConnected() || xboxOneLadybug.isConnected());
	}

	private void manageBothXboxesOneCommon() {
		if (xboxOneLadybug.isDpadUp() || xboxOneUnComputedGhost.isDpadUp()) {
			gameMessaging.addSoundMode(localPreferences.toggleSound());
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
		localPreferences
		        .setShowHelpForXboxes(xboxOneLadybug.isBackButton() || xboxOneUnComputedGhost.isBackButton());
	}

	private void manageLadybugXboxOneInGame() {
		if (!xboxOneLadybug.isConnected()) {
			return;
		}
		if (xboxOneLadybug.isRightLeftStick() || xboxOneLadybug.isRightRightStick()) {
			playerInputs.ladybug().request(GameMainConstants.POINT_RIGHT, InputSource.XBOX_PAD_1);
		}
		if (xboxOneLadybug.isLeftLeftStick() || xboxOneLadybug.isLeftRightStick()	) {
			playerInputs.ladybug().request(GameMainConstants.POINT_LEFT, InputSource.XBOX_PAD_1);
		}
		if (xboxOneLadybug.isUpLeftStick() || xboxOneLadybug.isUpRightStick()) {
			playerInputs.ladybug().request(GameMainConstants.POINT_UP, InputSource.XBOX_PAD_1);
		}
		if (xboxOneLadybug.isDownLeftStick() || xboxOneLadybug.isDownRightStick()) {
			playerInputs.ladybug().request(GameMainConstants.POINT_DOWN, InputSource.XBOX_PAD_1);
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

	/**
	 * Traduit l'état des deux manettes (déjà lu) en actions sur le jeu. Séparé de
	 * la lecture du matériel pour être testé avec une fausse manette. Les
	 * commandes passent par le modèle ; ce qui est lu (vibrations) vient de la
	 * photo de la partie (4.6).
	 */
	void handleRequests(GameModelForController model, GameSnapshot snapshot) {
		gameModelForController = model;
		localPreferences.setAtLeastOneXboxOneConnected(isOneXboxOneConnected());
		setProgramMessagesAccordingXboxOneConnectionOrDisconnection();
		manageBothXboxesOneCommon();
		if (gameModelForController.isProgramPresentation()) {
			manageBothXboxesOneInPresentation();
		} else if(gameModelForController.isProgramAskKeepPreviousGameLevel()) {
			manageBothXboxesOneInAskKeepPreviousGameLevel();
		} else if (gameModelForController.isInGame()) {
			manageBothXboxesOneInGame();
			manageLadybugXboxOneInGame();
			manageUnComputedGhostXboxOneInGame();
			doVibrationsInGame(snapshot);
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
		padManager.update();
		xboxOneLadybug.readCurrentState(padManager.state(XboxOneConstants.LADYBUG_XBOXONE_INDEX));
		xboxOneUnComputedGhost.readCurrentState(padManager.state(XboxOneConstants.GHOST_XBOXONE_INDEX));
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
		// Manette indisponible (échec du chargement de Jamepad) : rien à lire
		if (!padManager.isAvailable()) {
			return;
		}
		Object obj = event.getSource();
		if (obj instanceof GameModelForController eventGameModelForController) {
			readXboxOneStates();
			handleRequests(eventGameModelForController, event.getSnapshot());
		}
	}
}
