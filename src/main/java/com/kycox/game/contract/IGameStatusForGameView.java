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
package com.kycox.game.contract;

public interface IGameStatusForGameView extends IGameCommon {
	public int getNumLevel();

	public boolean isGameEnd();

	public boolean isGameEnding();

	public boolean isGamePresentation();

	public boolean isGameStarting();

	public boolean isLevelEnding();

	public boolean isLevelStarting();

	public boolean isProgramStarting();

	public boolean isToConfiguration();

	// FIXME : hmmm ?
	public void setGamePresentation();
}