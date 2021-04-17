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
package com.kycox.game.model;

import com.kycox.game.constant.GameStatus;
import com.kycox.game.contract.IDoActionAfterTimer;
import com.kycox.game.contract.IGameStatusForController;
import com.kycox.game.contract.IGameStatusForGameSounds;
import com.kycox.game.contract.IGameStatusForGameView;

import lombok.Getter;
import lombok.Setter;

public class CurrentGameStatus implements IGameStatusForGameView, IGameStatusForGameSounds, IGameStatusForController, IDoActionAfterTimer {
	private GameStatus gameStatus;
	@Getter
	@Setter
	private int		   numLevel;
	
	@Override
	public boolean isNoGame() {
		return gameStatus == GameStatus.NO_GAME;
	}
	
	public void setNoGame() {
		gameStatus = GameStatus.NO_GAME;
	}

	public boolean isLevelStart() {
		return gameStatus == GameStatus.LEVEL_START;
	}
	
	public void setLevelStart() {
		gameStatus = GameStatus.LEVEL_START;
	}
	
	public boolean isGameBegin() {
		return gameStatus == GameStatus.GAME_BEGIN;
	}
	
	public void setBeginGame() {
		gameStatus = GameStatus.GAME_BEGIN;
	}	
	
	public boolean isLevelBegin() {
		return gameStatus == GameStatus.LEVEL_BEGIN;
	}

	public void setBeginLevel() {
		gameStatus = GameStatus.LEVEL_BEGIN;
	}
	
	@Override
	public boolean isInGame() {
		return gameStatus == GameStatus.IN_GAME;
	}

	@Override
	public void setInGame() {
		gameStatus = GameStatus.IN_GAME;
	}	

	@Override
	public boolean isToConfiguration() {
		return gameStatus == GameStatus.TO_CONF_LOCAL_USR;
	}

	@Override
	public void setConfiguration() {
		gameStatus = GameStatus.TO_CONF_LOCAL_USR;
	}
	
	// KYLIAN C'EST ICI
	public void initNumLevel() {
		setNumLevel(0);
	}
	
	public void doActionAfterTimer() {
		setInGame();
	}
}
