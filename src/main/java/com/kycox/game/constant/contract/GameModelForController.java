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
package com.kycox.game.constant.contract;

import com.kycox.game.tools.Position;
import com.kycox.game.body.ghost.Ghost;
import com.kycox.game.body.ladybug.Ladybug;

import java.util.Optional;

import com.kycox.game.engine.model.Lobby;

public interface GameModelForController extends GameCommon {
	void changeLitLampMode();

	void forceStopGame();

	Ladybug getLadybug();

	Optional<Ghost> getUnComputedGhost();

	boolean isProgramPresentation();

	void programForceExit();


	void setGameInPause();



	void setMultiPlayers(boolean isMuliPlayers);

	/** Le salon (20.3) : les contrôleurs lui envoient des commandes ({@code LobbyCommand}). */
	Lobby getLobby();

	/** Ouvre le salon depuis la présentation (touche 2, Y à la manette, C53). */
	void openLobby();

	boolean isProgramLobby();

	/** Quitte le salon : retour à la présentation (la configuration est gardée). */
	void leaveLobby();

	/** Lance la partie si le salon est prêt (Cox a un joueur, chaque ligne a ses commandes). */
	void startFromLobby();

	/**
	 * Envoie une commande au salon, puis fait ce qu'il demande (20.3 ter) : les
	 * boutons « jouer » et « retour » lancent la partie ou ramènent à la
	 * présentation. Le salon décide, le jeu agit.
	 */
	default void applyLobbyCommand(com.kycox.game.engine.model.LobbyCommand command) {
		switch (getLobby().apply(command)) {
			case PLAY -> startFromLobby();
			case BACK -> leaveLobby();
			case NONE -> {
				// rien d'autre à faire
			}
		}
	}



	void startGame();


    void initNumLevel();

	boolean isProgramAskKeepPreviousGameLevel();

	void initializeLevelNumAndStartGame(boolean initialize);

	void askForceEndGame();

	boolean isGameAskForceEndGame();

	void setInGame();


	void manageGhostCamouflage();
}