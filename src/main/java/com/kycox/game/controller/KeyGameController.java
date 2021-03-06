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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kycox.game.constant.Constants;
import com.kycox.game.contract.IGameModelForController;
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
	private static final Log		logger = LogFactory.getLog(KeyGameController.class);
	@Inject
	private IGameModelForController	gameModel;
	@Inject
	private GameProperties gameProperties;

	/**
	 * Action sur les touches Gestion des touches pressées
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		if (gameModel.isInGame()) {	manageKeysInGame(keyCode); } 
		else { manageKeysInPresentation(keyCode); }
	}

	/**
	 * Gestion des touches in game
	 *
	 * @param keyCode
	 */
	private void manageKeysInGame(int keyCode) {
		switch (keyCode) {
			// Mouvement L
			case KeyEvent.VK_LEFT -> gameModel.setLadybugRequest(Constants.POINT_LEFT);
			case KeyEvent.VK_RIGHT -> gameModel.setLadybugRequest(Constants.POINT_RIGHT);
			case KeyEvent.VK_UP -> gameModel.setLadybugRequest(Constants.POINT_UP);
			case KeyEvent.VK_DOWN -> gameModel.setLadybugRequest(Constants.POINT_DOWN);
			// Mouvement du fantôme (joueur 2)
			case KeyEvent.VK_Z -> gameModel.setGhostRequest(Constants.POINT_UP);
			case KeyEvent.VK_S -> gameModel.setGhostRequest(Constants.POINT_DOWN);
			case KeyEvent.VK_Q -> gameModel.setGhostRequest(Constants.POINT_LEFT);
			case KeyEvent.VK_D -> gameModel.setGhostRequest(Constants.POINT_RIGHT);
			// Son
			case KeyEvent.VK_F2 -> gameModel.startStopSoundActive();
			case KeyEvent.VK_F3 ->  gameProperties.changeLadybugSkin();
			case KeyEvent.VK_F4 ->  gameProperties.changeGhostHeadBand();
			// Arret de la partie
			case KeyEvent.VK_ESCAPE -> gameModel.forceStopGame();
			// Partie en pause
			case KeyEvent.VK_PAUSE -> gameModel.gameInPause();
			default -> logger.info(keyCode + " key not managed");
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
			case KeyEvent.VK_S -> gameModel.startGame();
			case KeyEvent.VK_F2 -> gameModel.startStopSoundActive();
			case KeyEvent.VK_C -> gameModel.getCurrentGameStatus().setConfiguration();
			case KeyEvent.VK_ESCAPE -> System.exit(0); // FIXME : c'est au modèle de sortir proprement du jeu
			default -> logger.info(keyCode + " key not managed");
		}
	}
}