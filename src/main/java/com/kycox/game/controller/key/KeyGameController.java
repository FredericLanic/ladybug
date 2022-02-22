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
package com.kycox.game.controller.key;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.inject.Inject;
import javax.inject.Named;

import com.kycox.game.constant.Constants;
import com.kycox.game.contract.GameModelForController;
import com.kycox.game.properties.GameProperties;

/**
 * Contrôleur du jeu : MVC
 *
 * voir https://github.com/marcelschoen/gamepad4j pour brancher une manette usb
 * pour le jeu utiliser plutôt Jamepad qui me semble facilement utilisable;
 *
 */
@Named("KeyGameController")
public class KeyGameController extends KeyAdapter {
	@Inject
	private GameModelForController gameModelForController;
	@Inject
	private GameProperties gameProperties;

	/**
	 * Action sur les touches Gestion des touches pressées
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		var keyCode = e.getKeyCode();
		manageCommonKeys(keyCode);
		if (gameModelForController.isGamePresentation()) {
			manageKeysInPresentation(keyCode);
		} else if (gameModelForController.isInGame()) {
			manageKeysInGame(keyCode);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_F1 -> gameModelForController.setShowHelpForKeys(false);
		}
	}

	private void manageCommonKeys(int keyCode) {
		switch (keyCode) {
			case KeyEvent.VK_F1 -> gameModelForController.setShowHelpForKeys(true);
			case KeyEvent.VK_F2 -> gameModelForController.startStopSoundActive();
			case KeyEvent.VK_F3 -> gameProperties.changeLadybugSkin();
			case KeyEvent.VK_F4 -> gameProperties.changeGhostHeadBand();
			case KeyEvent.VK_F5 -> gameProperties.changeGhostHat();

		}
	}

	/**
	 * Gestion des touches in game
	 *
	 * @param keyCode
	 */
	private void manageKeysInGame(int keyCode) {
		switch (keyCode) {
			// Mouvement L
			case KeyEvent.VK_LEFT -> gameModelForController.setLadybugRequest(Constants.POINT_LEFT);
			case KeyEvent.VK_RIGHT -> gameModelForController.setLadybugRequest(Constants.POINT_RIGHT);
			case KeyEvent.VK_UP -> gameModelForController.setLadybugRequest(Constants.POINT_UP);
			case KeyEvent.VK_DOWN -> gameModelForController.setLadybugRequest(Constants.POINT_DOWN);
			// Mouvement du fantôme (joueur 2)
			case KeyEvent.VK_Z -> gameModelForController.setGhostRequest(Constants.POINT_UP);
			case KeyEvent.VK_S -> gameModelForController.setGhostRequest(Constants.POINT_DOWN);
			case KeyEvent.VK_Q -> gameModelForController.setGhostRequest(Constants.POINT_LEFT);
			case KeyEvent.VK_D -> gameModelForController.setGhostRequest(Constants.POINT_RIGHT);
			// Partie en pause
			case KeyEvent.VK_PAUSE -> gameModelForController.gameInPause();
			// Arret de la partie
			case KeyEvent.VK_ESCAPE -> gameModelForController.forceStopGame();
			// Mode lampe allumée
			case KeyEvent.VK_F6 -> gameModelForController.changeLitLampMode();
		}
	}

	/**
	 * Gestion des touches durant la présentation
	 *
	 * @param keyCode
	 */
	private void manageKeysInPresentation(int keyCode) {
		// Gestion des touches durant la présentation
		switch (keyCode) {
			case KeyEvent.VK_S -> gameModelForController.startGame();
			case KeyEvent.VK_1 -> gameModelForController.setMultiPlayers(false);
			case KeyEvent.VK_2 -> gameModelForController.setMultiPlayers(true);
			case KeyEvent.VK_ESCAPE -> System.exit(0); // FIXME : c'est au modèle de sortir proprement du jeu
		}
	}
}