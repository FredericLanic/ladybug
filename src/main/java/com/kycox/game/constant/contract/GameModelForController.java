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

public interface GameModelForController extends GameCommon {
	void changeLitLampMode();

	void forceStopGame();

	Ladybug getLadybug();

	Optional<Ghost> getUnComputedGhost();

	boolean isProgramPresentation();

	void programForceExit();


	void setGameInPause();

	void setGhostRequest(Position point);

	void setLadybugRequest(Position point);

	void setMultiPlayers(boolean isMuliPlayers);



	void startGame();


    void initNumLevel();

	boolean isProgramAskKeepPreviousGameLevel();

	void initializeLevelNumAndStartGame(boolean initialize);

	void askForceEndGame();

	boolean isGameAskForceEndGame();

	void setInGame();


	void manageGhostCamouflage();
}