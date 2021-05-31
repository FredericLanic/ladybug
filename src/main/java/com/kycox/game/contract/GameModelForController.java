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

import java.awt.Point;

public interface GameModelForController extends GameCommon {
	public void forceStopGame();

	public void gameInPause();

	public GameStatusForController getCurrentProgramStatus();

	public LadybugForController getLadybug();

	public boolean isGamePresentation();

	public void setGhostRequest(Point point);

	public void setLadybugRequest(Point point);

	public void startGame();

	public void startStopSoundActive();
}