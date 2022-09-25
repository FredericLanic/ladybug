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
package com.kycox.game.level;

import com.kycox.game.constant.LevelConstruct;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static com.kycox.game.constant.LevelConstruct.B;
import static com.kycox.game.constant.LevelConstruct.N;

@Component("Level4NG")
public class Level4NG extends LevelNG {
	public Level4NG() {
		// nombre de blocks par ligne
		nbrBlocksPerLine = 15;
		// nombre de lignes
		nbrLines = 15;
		levelDATA = new LevelConstruct[] {
		        // 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
		        N, B, B, N, B, B, B, B, B, B, B, N, B, B, N, //
		        B, B, B, N, B, N, N, B, N, N, B, N, B, B, B, //
		        N, N, B, B, B, B, B, B, B, B, B, B, B, N, N, //
		        N, B, B, B, N, B, B, N, B, B, N, B, B, B, N, //
		        N, B, N, N, N, N, B, B, B, N, N, N, N, B, N, //
		        N, B, N, B, N, B, B, B, B, B, N, B, N, B, N, //
		        B, B, B, B, N, N, B, B, B, N, N, B, B, B, B, //
		        B, N, N, N, N, N, N, N, N, N, N, N, N, N, B, //
		        B, B, B, N, B, B, B, B, B, B, B, N, B, B, B, //
		        N, N, B, N, N, B, N, B, N, B, N, N, B, N, N, //
		        B, N, B, B, N, N, N, B, N, N, N, B, B, N, B, //
		        B, B, B, B, B, B, B, B, B, B, B, B, B, B, B, //
		        N, B, N, N, N, B, B, B, B, B, N, N, N, B, N, //
		        B, B, N, B, N, N, N, B, N, N, N, B, N, B, B, //
		        N, B, B, B, B, B, N, B, N, B, B, B, B, B, N };
	}

	@Override
	public Point getGhostRegenerateBlockPoint() {
		return new Point(7, 5);
	}

	@Override
	public Point getInitLadybugBlockPos() {
		return new Point(7, 14);
	}

	@Override
	public int getNbrMegaPoints() {
		return 2;
	}

	@Override
	public Map<Point, Point> getTeleportPoints() {
		Map<Point, Point> teleportPoint = new HashMap<>();
		teleportPoint.put(new Point(14, 1), new Point(0, 1));
		teleportPoint.put(new Point(0, 1), new Point(14, 1));
		return teleportPoint;
	}
}
