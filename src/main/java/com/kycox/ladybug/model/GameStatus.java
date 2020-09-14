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
package com.kycox.ladybug.model;

import com.kycox.ladybug.constant.GameStatusEnum;

import lombok.Getter;
import lombok.Setter;

public class GameStatus {
	private GameStatusEnum gameStatusEnum;
	@Getter
	@Setter
	private int			   numLevel;

	public void addNumLevel() {
		numLevel++;
	}

	public boolean isBeginningGame() {
		return gameStatusEnum == GameStatusEnum.BEGINNING_GAME;
	}

	public boolean isBeginningLevel() {
		return gameStatusEnum == GameStatusEnum.BEGINING_LEVEL;
	}

	public boolean isInGame() {
		return gameStatusEnum == GameStatusEnum.IN_GAME;
	}

	public boolean isNoGame() {
		return gameStatusEnum == GameStatusEnum.NO_GAME;
	}

	public boolean isToConfiguration() {
		return gameStatusEnum == GameStatusEnum.TO_CONF_LOCAL_USR;
	}

	public void setBeginingLevel() {
		gameStatusEnum = GameStatusEnum.BEGINING_LEVEL;
	}

	public void setBeginningGame() {
		gameStatusEnum = GameStatusEnum.BEGINNING_GAME;
	}

	public void setConfiguration() {
		gameStatusEnum = GameStatusEnum.TO_CONF_LOCAL_USR;
	}

	public void setInGame() {
		gameStatusEnum = GameStatusEnum.IN_GAME;
	}

	public void setNoGame() {
		gameStatusEnum = GameStatusEnum.NO_GAME;
	}
}
