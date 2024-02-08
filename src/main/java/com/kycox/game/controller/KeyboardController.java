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
package com.kycox.game.controller;

import com.kycox.game.constant.GameMainConstants;
import com.kycox.game.contract.GameModelForController;
import com.kycox.game.properties.GameProperties;
import org.springframework.stereotype.Component;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

@Component
public class KeyboardController extends KeyAdapter {
	private final GameModelForController gameModelForController;
	private final GameProperties gameProperties;

	public KeyboardController(GameModelForController gameModelForController, GameProperties gameProperties) {
		this.gameModelForController = gameModelForController;
		this.gameProperties = gameProperties;
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
            gameModelForController.setShowHelpForKeys(false);
        }
	}

	private void manageCommonKeys(int keyCode) {
		switch (keyCode) {
			case KeyEvent.VK_F1 -> gameModelForController.setShowHelpForKeys(true);
			case KeyEvent.VK_F2 -> gameModelForController.startStopSoundActive();
			// Mode débug
			case KeyEvent.VK_ENTER -> gameModelForController.changeDebugMode();
			case KeyEvent.VK_F3 -> gameProperties.changeLadybugSkin();
			case KeyEvent.VK_F4 -> gameProperties.changeGhostHeadBand();
			case KeyEvent.VK_F5 -> gameProperties.changeGhostHat();
			case KeyEvent.VK_F7 -> gameModelForController.manageGhostCamouflage();
 		}
	}

	private void manageKeysInGame(int keyCode) {
		switch (keyCode) {
			// Mouvement L
			case KeyEvent.VK_LEFT -> gameModelForController.setLadybugRequest(GameMainConstants.POINT_LEFT);
			case KeyEvent.VK_RIGHT -> gameModelForController.setLadybugRequest(GameMainConstants.POINT_RIGHT);
			case KeyEvent.VK_UP -> gameModelForController.setLadybugRequest(GameMainConstants.POINT_UP);
			case KeyEvent.VK_DOWN -> gameModelForController.setLadybugRequest(GameMainConstants.POINT_DOWN);
			// Mouvement du fantôme (joueur 2)
			case KeyEvent.VK_Z -> gameModelForController.setGhostRequest(GameMainConstants.POINT_UP);
			case KeyEvent.VK_S -> gameModelForController.setGhostRequest(GameMainConstants.POINT_DOWN);
			case KeyEvent.VK_Q -> gameModelForController.setGhostRequest(GameMainConstants.POINT_LEFT);
			case KeyEvent.VK_D -> gameModelForController.setGhostRequest(GameMainConstants.POINT_RIGHT);
			// Partie en pause
			case KeyEvent.VK_PAUSE -> gameModelForController.setGameInPause();
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