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
package com.kycox.game.constant.level.data;

import static com.kycox.game.constant.level.LevelConstruct.B;
import static com.kycox.game.constant.level.LevelConstruct.N;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kycox.game.constant.level.LevelConstruct;
import com.kycox.game.constant.contract.LevelStructure;
import com.kycox.game.engine.screendata.ScreenBlock;

public abstract class SecondGenerationLevel implements LevelStructure {
	private static final int DOWN = 8;
	private static final int LEFT = 1;
	static final int NOT_ACCESSIBLE_POINT = 128;
	private static final int POINT = 16;
	private static final int RIGHT = 4;
	private static final int UP = 2;
	// Map
	protected LevelConstruct[] levelDATA;
	// Nombre de blocks par ligne
	protected int nbrBlocksPerLine;
	// Nombre de lignes
	protected int nbrLines;

	private int dataPosition(int x, int y) {
		return y * nbrBlocksPerLine + x;
	}

	@Override
	public List<ScreenBlock> getLstBlocks() {
		List<ScreenBlock> lstBlocks = new ArrayList<>();
		for (var i = 0; i < levelDATA.length; i++) {
			var x = i % nbrBlocksPerLine;
			var y = i / nbrBlocksPerLine;
			var block = new ScreenBlock(screenBlockValue(x, y));
			block.setCoordinate(new Point(x, y));
			lstBlocks.add(block);
		}
		return lstBlocks;
	}

	@Override
	public int getNbrBlocksByLine() {
		return nbrBlocksPerLine;
	}

	@Override
	public int getNbrLines() {
		return nbrLines;
	}

	@Override
	public Map<Point, Point> getTeleportPoints() {
		return new HashMap<>();
	}

	/**
	 * FIXME : Refacto !! TODO : mettre des optional
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	private int screenBlockValue(int x, int y) {
		var pointValue = 0;
		var block = levelDATA[dataPosition(x, y)];
		LevelConstruct blockUp = null;
		LevelConstruct blockDown = null;
		LevelConstruct blockLeft = null;
		LevelConstruct blockRight = null;
		// Si y = 0 on est en haut de la grille
		if (y > 0) {
			blockUp = levelDATA[dataPosition(x, y - 1)];
		} else {
			pointValue += UP;
		}
		// Si y = nb ligne => on est sur le bas de la grille
		if (y < nbrLines - 1) {
			blockDown = levelDATA[dataPosition(x, y + 1)];
		} else {
			pointValue += DOWN;
		}
		// Si x = 0 => on est sur le bord gauche de la grille
		if (x > 0) {
			blockLeft = levelDATA[dataPosition(x - 1, y)];
		} else {
			pointValue += LEFT;
		}
		// Si x = nb block par ligne => on est sur le bord droit de la grille
		if (x < nbrBlocksPerLine - 1) {
			blockRight = levelDATA[dataPosition(x + 1, y)];
		} else {
			pointValue += RIGHT;
		}
		if (block == N) {
			pointValue += NOT_ACCESSIBLE_POINT;
			if (blockUp != null && blockUp != N) {
				pointValue += UP;
			}
			if (blockDown != null && blockDown != N) {
				pointValue += DOWN;
			}
			if (blockLeft != null && blockLeft != N) {
				pointValue += LEFT;
			}
			if (blockRight != null && blockRight != N) {
				pointValue += RIGHT;
			}
		}
		if (block == B) {
			pointValue += POINT;
			if (blockUp != null && blockUp == N) {
				pointValue += UP;
			}
			if (blockDown != null && blockDown == N) {
				pointValue += DOWN;
			}
			if (blockLeft != null && blockLeft == N) {
				pointValue += LEFT;
			}
			if (blockRight != null && blockRight == N) {
				pointValue += RIGHT;
			}
		}
		return pointValue;
	}
}
