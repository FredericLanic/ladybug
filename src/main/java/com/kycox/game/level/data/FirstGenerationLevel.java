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
package com.kycox.game.level.data;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kycox.game.contract.LevelStructure;

import com.kycox.game.level.ScreenBlock;
import lombok.Getter;

public abstract class FirstGenerationLevel implements LevelStructure {
	// Map
	protected int[] levelDATA;
	// Nombre de blocks par ligne
	@Getter
	protected int nbrBlocksByLine;
	// Nombre de lignes
	@Getter
	protected int nbrLines;

	@Override
	public List<ScreenBlock> getLstBlocks() {
		List<ScreenBlock> lstBlocks = new ArrayList<>();
		for (var i = 0; i < levelDATA.length; i++) {
			var x = i % nbrBlocksByLine;
			var y = i / nbrBlocksByLine;
			var block = new ScreenBlock(levelDATA[i]);
			block.setCoordinate(new Point(x, y));
			lstBlocks.add(block);
		}
		return lstBlocks;
	}

	@Override
	public Map<Point, Point> getTeleportPoints() {
		return new HashMap<>();
	}
}
