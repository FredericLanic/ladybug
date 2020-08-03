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
package com.kycox.ladybug.level;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public abstract class Level implements ILevel {
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
		for (int i = 0; i < levelDATA.length; i++) {
			int			x	  = i % nbrBlocksByLine;
			int			y	  = i / nbrBlocksByLine;
			ScreenBlock	block = new ScreenBlock(levelDATA[i]);
			block.setCoordinate(new Point(x, y));
			lstBlocks.add(block);
		}
		return lstBlocks;
	}

	@Override
	public int[] getMapLevel() {
		return levelDATA.clone();
	}
}
