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

import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kycox.game.constant.GameStatus;
import com.kycox.game.contract.IDoActionAfterTimer;
import com.kycox.game.contract.IGameStatusForController;
import com.kycox.game.contract.IGameStatusForGameSounds;
import com.kycox.game.contract.IGameStatusForGameView;

import lombok.Getter;
import lombok.Setter;

@Named("CurrentGameStatus")
public class CurrentGameStatus implements IGameStatusForGameView, IGameStatusForGameSounds, IGameStatusForController, IDoActionAfterTimer {
	private static final Log	 logger		   = LogFactory.getLog(CurrentGameStatus.class);
	
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
	
	public void setGamePresentation() {
		gameStatus = GameStatus.GAME_PRESENTATION;
	}
	
	public boolean isGamePresentation() {
		return gameStatus == GameStatus.GAME_PRESENTATION;
	}

	public boolean isLevelStart() {
		return gameStatus == GameStatus.LEVEL_START;
	}
	
	public void setLevelStart() {
		gameStatus = GameStatus.LEVEL_START;
	}
	
	public boolean isGameStart() {
		return gameStatus == GameStatus.GAME_START;
	}
	
	public void setGameStart() {
		gameStatus = GameStatus.GAME_START;
	}	
	
	public boolean isLevelStarting() {
		return gameStatus == GameStatus.LEVEL_STARTING;
	}

	public void setLevelStarting() {
		gameStatus = GameStatus.LEVEL_STARTING;
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
	
	public void doActionAfterTimer(int nbrAction) {
		switch(nbrAction) {
			case 0 : setInGame();
				break;
			default : logger.debug("no number " + nbrAction + " action");
		}
		
	}
}
