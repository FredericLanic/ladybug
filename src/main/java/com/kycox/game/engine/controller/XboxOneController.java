package com.kycox.game.engine.controller;

import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.constant.contract.GameModelForController;
import com.kycox.game.engine.controller.xboxone.XBoxOneControllerManager;
import com.kycox.game.engine.controller.xboxone.XboxRequest;
import com.kycox.game.engine.message.GameMessaging;
import com.kycox.game.engine.model.EventGameModel;
import com.kycox.game.properties.GameProperties;
import com.kycox.game.engine.model.Lobby;
import com.kycox.game.engine.model.LobbyCommand;
import com.kycox.game.input.InputSource;
import com.kycox.game.input.PlayerInputs;
import com.kycox.game.preferences.LocalPreferences;
import com.kycox.game.snapshot.GameSnapshot;
import java.util.List;
import java.util.function.Predicate;

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
	/** L'appareil de chaque manette, dans l'ordre des numéros SDL. */
	private static final List<InputSource> PAD_DEVICES = List.of(InputSource.XBOX_PAD_1, InputSource.XBOX_PAD_2,
	        InputSource.XBOX_PAD_3, InputSource.XBOX_PAD_4);

	private final XBoxOneControllerManager padManager;
	/** La manette affichée sur la ligne active du salon, pour ne la faire vibrer qu'une fois. */
	private InputSource lastHighlightedDevice;
	private final GameMessaging gameMessaging;
	private GameModelForController gameModelForController;
	private final GameProperties gameProperties;
	private final XboxRequest xboxOneLadybug;
	private final XboxRequest xboxOneUnComputedGhost;
	/** Les quatre manettes, dans l'ordre : l'indice est le numéro SDL et celui de {@link #PAD_DEVICES} (20.5.2). */
	private final List<XboxRequest> pads;
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
		// les manettes 3 et 4 n'ont pas de rôle fixe : elles n'existent que par le salon
		this.pads = List.of(xboxOneLadybug, xboxOneUnComputedGhost, new XboxRequest(), new XboxRequest());
	}

	/** Vrai si au moins une manette branchée satisfait ce critère (appui sur un bouton de menu). */
	private boolean anyPad(Predicate<XboxRequest> criterion) {
		return pads.stream().anyMatch(criterion);
	}

	/**
	 * Vibrations lues dans la photo de la partie (4.6), et non dans le modèle. Chaque
	 * manette vibre pour le personnage qu'elle pilote (table du salon, 20.4).
	 */
	private void doVibrationsInGame(GameSnapshot snapshot) {
		for (var i = 0; i < PAD_DEVICES.size(); i++) {
			doVibrationsInGame(snapshot, PAD_DEVICES.get(i), i);
		}
	}

	private void doVibrationsInGame(GameSnapshot snapshot, InputSource device, int padIndex) {
		var character = playerInputs.characterOf(device);
		if (character.isEmpty()) {
			return;
		}
		if (PlayerInputs.LADYBUG.equals(character.get())) {
			if (snapshot.ladybug().eatenAMegaPoint()) {
				padManager.vibrate(padIndex, 0f, 0.5f, 100);
			}
			if (snapshot.ladybug().toBeTeleported()) {
				padManager.vibrate(padIndex, 0f, 0.5f, 50);
			}
			return;
		}
		snapshot.ghosts().stream().filter(ghost -> ghost.name().equals(character.get()) && !ghost.computed())
		        .findFirst().ifPresent(ghost -> {
			        if (ghost.toBeTeleported()) {
				        padManager.vibrate(padIndex, 0f, 0.5f, 50);
			        }
			        if (ghost.dying()) {
				        padManager.vibrate(padIndex, 0f, 0.5f, 100);
			        }
		        });
	}

	private boolean isOneXboxOneConnected() {
		return anyPad(XboxRequest::isConnected);
	}

	private void manageBothXboxesOneCommon() {
		if (anyPad(XboxRequest::isDpadUp)) {
			gameMessaging.addSoundMode(localPreferences.toggleSound());
		}
		if (anyPad(XboxRequest::isDpadDown)) {
			gameProperties.changeLadybugSkin();
		}
		if (anyPad(XboxRequest::isDpadRight)) {
			gameProperties.changeGhostHeadBand();
		}
		if (anyPad(XboxRequest::isDpadLeft)) {
			gameProperties.changeGhostHat();
		}
		if (xboxOneLadybug.isRightStick()) {
			gameMessaging.addIamLadybug();
		}
		if (xboxOneUnComputedGhost.isRightStick()) {
			gameMessaging.addIamGhost();
		}
	}

	/**
	 * Convention des jeux Xbox (étape 20.1, choisie par Frédéric) : A = oui,
	 * B = non ; Start = pause ; Back (« Affichage ») = « quitter la partie ? ».
	 */
	private void manageBothXboxesOneInGame() {
		if (anyPad(XboxRequest::isBackPressed)) {
			gameModelForController.askForceEndGame();
		}
		if (anyPad(XboxRequest::isStart)) {
			gameModelForController.setGameInPause();
		}
	}

	/** « Reprendre l'ancien niveau ? » : A = oui, reprendre ; B = non, nouvelle partie. */
	private void manageBothXboxesOneInAskKeepPreviousGameLevel() {
		if (anyPad(XboxRequest::isAButton)) {
			gameModelForController.initializeLevelNumAndStartGame(false);
		}
		if (anyPad(XboxRequest::isBButton)) {
			gameModelForController.initializeLevelNumAndStartGame(true);
		}
	}

	private void manageBothXboxesOneInPresentation() {
		if (anyPad(XboxRequest::isAButton)) {
			gameModelForController.startGame();
		}
		if (anyPad(XboxRequest::isBButton)) {
			gameModelForController.programForceExit();
		}
		if (anyPad(XboxRequest::isXButton)) {
			gameModelForController.setMultiPlayers(false);
		}
		// plusieurs joueurs : le salon (C53)
		if (anyPad(XboxRequest::isYButton)) {
			gameModelForController.openLobby();
		}
		localPreferences
		        .setShowHelpForXboxes(anyPad(XboxRequest::isBackButton));
	}

	/**
	 * Règle C57 : une manette qui pilote un personnage vient d'être débranchée en
	 * partie → pause et message « manette 2 deconnectee : inky attend ».
	 */
	private void pauseIfPadLost(int padIndex) {
		if (!pads.get(padIndex).isHasBeenDisConnected()) {
			return;
		}
		playerInputs.characterOf(PAD_DEVICES.get(padIndex)).ifPresent(character -> {
			gameModelForController.pauseGame();
			gameMessaging.addPadLost(padIndex + 1, character);
		});
	}

	/**
	 * Les déplacements d'une manette (les deux sticks), envoyés au personnage
	 * qu'elle pilote d'après la table du salon (20.4).
	 */
	private void managePadInGame(XboxRequest pad, InputSource device) {
		if (!pad.isConnected()) {
			return;
		}
		playerInputs.of(device).ifPresent(input -> {
			if (pad.isRightLeftStick() || pad.isRightRightStick()) {
				input.request(GameMainConstants.POINT_RIGHT, device);
			}
			if (pad.isLeftLeftStick() || pad.isLeftRightStick()) {
				input.request(GameMainConstants.POINT_LEFT, device);
			}
			if (pad.isUpLeftStick() || pad.isUpRightStick()) {
				input.request(GameMainConstants.POINT_UP, device);
			}
			if (pad.isDownLeftStick() || pad.isDownRightStick()) {
				input.request(GameMainConstants.POINT_DOWN, device);
			}
		});
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
		// dans le salon, la croix choisit un personnage : pas de son ni d'apparence
		if (gameModelForController.isProgramLobby()) {
			signalConnectedPads();
			for (var i = 0; i < pads.size(); i++) {
				manageXboxInLobby(pads.get(i), PAD_DEVICES.get(i));
			}
			vibrateTheHighlightedPad();
			return;
		}
		manageBothXboxesOneCommon();
		if (gameModelForController.isProgramPresentation()) {
			manageBothXboxesOneInPresentation();
		} else if(gameModelForController.isProgramAskKeepPreviousGameLevel()) {
			manageBothXboxesOneInAskKeepPreviousGameLevel();
		} else if (gameModelForController.isInGame()) {
			manageBothXboxesOneInGame();
			for (var i = 0; i < pads.size(); i++) {
				pauseIfPadLost(i);
				managePadInGame(pads.get(i), PAD_DEVICES.get(i));
			}
			doVibrationsInGame(snapshot);
		} else if(gameModelForController.isGameAskForceEndGame()) {
			manageBothXboxesOneInAskForceEndGame();
		}
	}

	/**
	 * Le salon (20.3), pour une manette (schéma choisi par Frédéric) : une ligne en
	 * deux temps, le personnage puis les commandes. Le stick gauche (gauche /
	 * droite) choisit la valeur de l'étape en cours ; A
	 * valide l'étape (sur « appuie sur A », la manette qui appuie prend la ligne) ;
	 * B revient en arrière ; le stick droit passe du personnage (à gauche) aux
	 * commandes (à droite) et change de ligne (en haut / en bas) ; la croix n'y sert
	 * pas ; ≡ lance la partie, ⧉ revient à la présentation. Chaque
	 * appui devient une commande du salon.
	 */
	private void manageXboxInLobby(XboxRequest pad, InputSource device) {
		if (!pad.isConnected()) {
			return;
		}
		var lobby = gameModelForController.getLobby();
		if (pad.isStart()) {
			gameModelForController.startFromLobby();
		} else if (pad.isBackPressed()) {
			gameModelForController.leaveLobby();
		} else if (pad.isAButton()) {
			var rows = lobby.rows();
			var activeWaits = lobby.activeRow() < rows.size() && rows.get(lobby.activeRow()).waitingForPad();
			if (activeWaits) {
				gameModelForController.applyLobbyCommand(new LobbyCommand.Claim(device));
				vibrate(device); // « c'est bien cette manette »
			} else {
				gameModelForController.applyLobbyCommand(new LobbyCommand.Confirm()); // ou le bouton choisi
			}
		} else if (pad.isBButton()) {
			gameModelForController.applyLobbyCommand(new LobbyCommand.Back());
		} else if (pad.isRightStickPushedUp()) {
			gameModelForController.applyLobbyCommand(new LobbyCommand.MoveRow(-1));
		} else if (pad.isRightStickPushedDown()) {
			gameModelForController.applyLobbyCommand(new LobbyCommand.MoveRow(1));
		} else if (pad.isRightStickPushedRight() && isLeftColumn(lobby.activeColumn())) {
			gameModelForController.applyLobbyCommand(new LobbyCommand.SwitchColumn()); // à droite : commandes, ou « retour »
		} else if (pad.isRightStickPushedLeft() && !isLeftColumn(lobby.activeColumn())) {
			gameModelForController.applyLobbyCommand(new LobbyCommand.SwitchColumn()); // à gauche : personnage, ou « jouer »
		} else if (pad.isLeftStickPushedLeft()) {
			gameModelForController.applyLobbyCommand(new LobbyCommand.ChangeValue(-1));
		} else if (pad.isLeftStickPushedRight()) {
			gameModelForController.applyLobbyCommand(new LobbyCommand.ChangeValue(1));
		}
	}

	/** La colonne de gauche : le personnage, ou le bouton « jouer » sur la ligne des boutons. */
	private static boolean isLeftColumn(Lobby.Column column) {
		return column == Lobby.Column.CHARACTER || column == Lobby.Column.PLAY;
	}

	/** Le salon ne propose que les manettes branchées (de 1 à 4). */
	private void signalConnectedPads() {
		var connected = new java.util.HashSet<InputSource>();
		for (var i = 0; i < pads.size(); i++) {
			if (pads.get(i).isConnected()) {
				connected.add(PAD_DEVICES.get(i));
			}
		}
		gameModelForController.getLobby().setConnectedPads(connected);
	}

	/**
	 * Quand la ligne active affiche une manette, celle-ci vibre brièvement : son
	 * joueur sait que c'est la sienne (les manettes Xbox n'ont pas de voyant de
	 * numéro de joueur).
	 */
	private void vibrateTheHighlightedPad() {
		var lobby = gameModelForController.getLobby();
		var rows = lobby.rows();
		var highlighted = lobby.activeRow() < rows.size() ? rows.get(lobby.activeRow()).device() : null;
		if (highlighted != lastHighlightedDevice) {
			lastHighlightedDevice = highlighted;
			vibrate(highlighted);
		}
	}

	private void vibrate(InputSource device) {
		var index = device == null ? -1 : PAD_DEVICES.indexOf(device);
		if (index >= 0) {
			padManager.vibrate(index, 0.4f, 0.4f, 150);
		}
	}

	/** « Quitter la partie ? » : A = oui, quitter ; B = non, continuer. */
	private void manageBothXboxesOneInAskForceEndGame() {
		if (anyPad(XboxRequest::isBButton)) {
			gameModelForController.setInGame();
		}
		if (anyPad(XboxRequest::isAButton)) {
			gameModelForController.forceStopGame();
		}
	}

	private void readXboxOneStates() {
		padManager.update();
		for (var i = 0; i < pads.size(); i++) {
			pads.get(i).readCurrentState(padManager.state(i));
		}
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
