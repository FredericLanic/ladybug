/**
  Copyright (C) 2021  Frédéric Lanic frederic.lanic@outlook.fr

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.kycox.game.engine.controller;

import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.constant.contract.GameModelForController;
import com.kycox.game.engine.message.GameMessaging;
import com.kycox.game.properties.GameProperties;
import com.kycox.game.engine.model.LobbyCommand;
import com.kycox.game.input.InputSource;
import com.kycox.game.input.PlayerInputs;
import com.kycox.game.preferences.LocalPreferences;
import com.kycox.game.launcher.GuiComponent;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

@GuiComponent
public class KeyboardController extends KeyAdapter {
	private final GameModelForController gameModelForController;
	private final GameProperties gameProperties;
	/** Réglages de ce poste (aide, son, debug), écrits sans passer par le modèle (4.5). */
	private final LocalPreferences localPreferences;
	private final GameMessaging gameMessaging;
	/** Les ordres de déplacement vont directement au joueur concerné (5.1). */
	private final PlayerInputs playerInputs;

	public KeyboardController(GameModelForController gameModelForController, GameProperties gameProperties,
	        LocalPreferences localPreferences, GameMessaging gameMessaging, PlayerInputs playerInputs) {
		this.playerInputs = playerInputs;
		this.gameModelForController = gameModelForController;
		this.gameProperties = gameProperties;
		this.localPreferences = localPreferences;
		this.gameMessaging = gameMessaging;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		var keyCode = e.getKeyCode();
		// dans le salon, Entrée lance la partie : elle n'y bascule pas le mode debug
		if (gameModelForController.isProgramLobby()) {
			manageKeysInLobby(keyCode);
			return;
		}
		manageCommonKeys(keyCode);
		if (gameModelForController.isProgramPresentation()) {
			manageKeysInPresentation(keyCode);
		} else if (gameModelForController.isInGame()) {
			manageKeysInGame(keyCode);
		} else if(gameModelForController.isProgramAskKeepPreviousGameLevel()) {
			manageKeysInAskKeepPreviousGameLevel(keyCode);
		} else if (gameModelForController.isGameAskForceEndGame()) {
			manageKeysInAskEndGame(keyCode);
		}
	}

	private void manageKeysInAskEndGame(int keyCode) {
		switch (keyCode) {
			case KeyEvent.VK_Y, KeyEvent.VK_O -> gameModelForController.forceStopGame();
			case KeyEvent.VK_N -> gameModelForController.setInGame();
		}
	}

	private void manageKeysInAskKeepPreviousGameLevel(int keyCode) {
		switch (keyCode) {
			case KeyEvent.VK_Y, KeyEvent.VK_O -> gameModelForController.initializeLevelNumAndStartGame(false);
			case KeyEvent.VK_N -> gameModelForController.initializeLevelNumAndStartGame(true);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F1) {
            localPreferences.setShowHelpForKeys(false);
        }
	}

	private void manageCommonKeys(int keyCode) {
		switch (keyCode) {
			case KeyEvent.VK_F1 -> localPreferences.setShowHelpForKeys(true);
			case KeyEvent.VK_F2 -> gameMessaging.addSoundMode(localPreferences.toggleSound());
			// Mode débug
			case KeyEvent.VK_ENTER -> localPreferences.toggleDebugMode();
			case KeyEvent.VK_F3 -> gameProperties.changeLadybugSkin();
			case KeyEvent.VK_F4 -> gameProperties.changeGhostHeadBand();
			case KeyEvent.VK_F5 -> gameProperties.changeGhostHat();
			case KeyEvent.VK_F7 -> gameModelForController.manageGhostCamouflage();
 		}
	}

	private void manageKeysInGame(int keyCode) {
		switch (keyCode) {
			// Mouvement L
			case KeyEvent.VK_LEFT -> playerInputs.ladybug().request(GameMainConstants.POINT_LEFT, InputSource.KEYBOARD_ARROWS);
			case KeyEvent.VK_RIGHT -> playerInputs.ladybug().request(GameMainConstants.POINT_RIGHT, InputSource.KEYBOARD_ARROWS);
			case KeyEvent.VK_UP -> playerInputs.ladybug().request(GameMainConstants.POINT_UP, InputSource.KEYBOARD_ARROWS);
			case KeyEvent.VK_DOWN -> playerInputs.ladybug().request(GameMainConstants.POINT_DOWN, InputSource.KEYBOARD_ARROWS);
			// Mouvement du fantôme (joueur 2)
			case KeyEvent.VK_Z -> playerInputs.humanGhost().ifPresent(ghost -> ghost.request(GameMainConstants.POINT_UP, InputSource.KEYBOARD_ZQSD));
			case KeyEvent.VK_S -> playerInputs.humanGhost().ifPresent(ghost -> ghost.request(GameMainConstants.POINT_DOWN, InputSource.KEYBOARD_ZQSD));
			case KeyEvent.VK_Q -> playerInputs.humanGhost().ifPresent(ghost -> ghost.request(GameMainConstants.POINT_LEFT, InputSource.KEYBOARD_ZQSD));
			case KeyEvent.VK_D -> playerInputs.humanGhost().ifPresent(ghost -> ghost.request(GameMainConstants.POINT_RIGHT, InputSource.KEYBOARD_ZQSD));
			// Partie en pause
			// P en plus de Pause : la touche Pause n'existe pas sur un clavier Mac (C33)
			case KeyEvent.VK_PAUSE, KeyEvent.VK_P -> gameModelForController.setGameInPause();
			// Arret de la partie
			case KeyEvent.VK_ESCAPE -> gameModelForController.askForceEndGame();
			// Mode lampe allumée
			case KeyEvent.VK_F6 -> gameModelForController.changeLitLampMode();
		}
	}

	private void manageKeysInPresentation(int keyCode) {
		// Gestion des touches durant la présentation
		switch (keyCode) {
			case KeyEvent.VK_S -> gameModelForController.startGame();
			case KeyEvent.VK_1 -> gameModelForController.setMultiPlayers(false);
			// plusieurs joueurs : le salon, où chacun choisit son appareil et son personnage (C53, C56)
			case KeyEvent.VK_2 -> gameModelForController.openLobby();
			case KeyEvent.VK_ESCAPE -> gameModelForController.programForceExit();
		}
	}

	/**
	 * Le salon (20.3) : flèches (ou ZQSD, ou IJKL) haut / bas pour la ligne,
	 * gauche / droite pour la valeur ; Espace valide l'étape (personnage, puis
	 * commandes, ou le bouton de la dernière ligne), Retour arrière revient en
	 * arrière ; raccourcis : Entrée lance la partie (si le salon est prêt), Échap
	 * revient à la présentation. Chaque touche devient une commande du salon.
	 */
	private void manageKeysInLobby(int keyCode) {
		switch (keyCode) {
			case KeyEvent.VK_ENTER -> gameModelForController.startFromLobby();
			case KeyEvent.VK_ESCAPE -> gameModelForController.leaveLobby();
			default -> lobbyCommandOf(keyCode).ifPresent(gameModelForController::applyLobbyCommand);
		}
	}

	private static java.util.Optional<LobbyCommand> lobbyCommandOf(int keyCode) {
		return java.util.Optional.ofNullable(switch (keyCode) {
			case KeyEvent.VK_UP, KeyEvent.VK_Z, KeyEvent.VK_I -> new LobbyCommand.MoveRow(-1);
			case KeyEvent.VK_DOWN, KeyEvent.VK_S, KeyEvent.VK_K -> new LobbyCommand.MoveRow(1);
			case KeyEvent.VK_LEFT, KeyEvent.VK_Q, KeyEvent.VK_J -> new LobbyCommand.ChangeValue(-1);
			case KeyEvent.VK_RIGHT, KeyEvent.VK_D, KeyEvent.VK_L -> new LobbyCommand.ChangeValue(1);
			case KeyEvent.VK_SPACE -> new LobbyCommand.Confirm(); // comme A à la manette
			case KeyEvent.VK_BACK_SPACE -> new LobbyCommand.Back(); // comme B à la manette
			default -> null;
		});
	}
}
