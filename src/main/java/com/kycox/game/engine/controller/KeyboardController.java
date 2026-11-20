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
			case KeyEvent.VK_Z -> gameModelForController.setGhostRequest(GameMainConstants.POINT_UP);
			case KeyEvent.VK_S -> gameModelForController.setGhostRequest(GameMainConstants.POINT_DOWN);
			case KeyEvent.VK_Q -> gameModelForController.setGhostRequest(GameMainConstants.POINT_LEFT);
			case KeyEvent.VK_D -> gameModelForController.setGhostRequest(GameMainConstants.POINT_RIGHT);
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
			case KeyEvent.VK_2 -> gameModelForController.setMultiPlayers(true);
			case KeyEvent.VK_ESCAPE -> gameModelForController.programForceExit();
		}
	}
}