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
package com.kycox.game.constant;

public enum GameStatus {
	GAME_END, GAME_ENDING, GAME_PRESENTATION, GAME_START, IN_GAME, LEVEL_END, LEVEL_ENDING, LEVEL_START, LEVEL_STARTING,
	NO_GAME, PROGRAM_START, PROGRAM_STARTING, TO_CONF_LOCAL_USR;

	private GameStatus() {
	}
}