/**
  Copyright (C) 2019  Frédéric Lanic frederic.lanic@outlook.fr

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

import lombok.Getter;
import lombok.Setter;

public class CurrentGameStatus {
	private GameStatus gameStatus;
	@Getter
	@Setter
	private int			   numLevel;

	public boolean isGameBegin() {
		return gameStatus == GameStatus.GAME_BEGIN;
	}

	public boolean isInGame() {
		return gameStatus == GameStatus.IN_GAME;
	}

	public boolean isLevelBegin() {
		return gameStatus == GameStatus.LEVEL_BEGIN;
	}

	public boolean isNoGame() {
		return gameStatus == GameStatus.NO_GAME;
	}

	public boolean isToConfiguration() {
		return gameStatus == GameStatus.TO_CONF_LOCAL_USR;
	}

	public void setBeginingLevel() {
		gameStatus = GameStatus.LEVEL_BEGIN;
	}

	public void setBeginningGame() {
		gameStatus = GameStatus.GAME_BEGIN;
	}

	public void setConfiguration() {
		gameStatus = GameStatus.TO_CONF_LOCAL_USR;
	}

	public void setInGame() {
		gameStatus = GameStatus.IN_GAME;
	}

	public void setNoGame() {
		gameStatus = GameStatus.NO_GAME;
	}
}
