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
import java.util.List;

import com.kycox.game.level.ScreenBlock;

public interface LevelStructure {
	public Point getGhostRegenerateBlockPoint();

	public Point getInitLadybugBlockPos();

	public List<ScreenBlock> getLstBlocks();

	public int getNbrBlocksByLine();

	public int getNbrLines();

	public int getNbrMegaPoints();
}