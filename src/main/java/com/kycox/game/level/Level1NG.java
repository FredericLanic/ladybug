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

@Component
public class Level1NG extends LevelNG {
	public Level1NG() {
		// nombre de blocks par ligne
		nbrBlocksPerLine = 15;
		// nombre de lignes
		nbrLines = 15;
		levelDATA = new LevelConstruct[] {
		        // 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
		        B, B, B, B, N, N, B, B, B, N, N, B, B, B, B, // 1
		        B, N, N, B, B, B, B, N, B, B, B, B, N, N, B, // 2
		        B, B, B, B, N, N, B, B, B, N, N, B, B, B, B, // 3
		        B, N, B, B, B, N, B, N, B, N, B, B, N, N, B, // 4
		        B, N, N, N, B, N, B, N, B, N, B, N, N, N, B, // 5
		        B, B, B, B, B, B, B, B, B, B, B, B, B, B, B, // 6
		        N, B, N, B, N, B, N, N, N, B, N, B, N, B, N, // 7
		        B, B, N, B, N, B, N, N, N, B, N, B, N, B, B, // 8
		        N, B, N, B, N, B, N, N, N, B, N, B, N, B, N, // 9
		        B, B, B, B, B, B, B, B, B, B, B, B, B, B, B, // 10
		        B, N, N, N, N, B, N, B, N, B, N, N, N, N, B, // 11
		        B, N, N, N, N, B, B, B, B, B, N, N, N, N, B, // 12
		        B, B, B, B, N, B, N, N, N, B, N, B, B, B, B, // 13
		        B, N, N, B, B, B, B, N, B, B, B, B, N, N, B, // 14
		        B, B, B, B, N, N, B, B, B, N, N, B, B, B, B // 15
		};
	}

	@Override
	public Point getGhostRegenerateBlockPoint() {
		return new Point(7, 2);
	}

	@Override
	public Point getInitLadybugBlockPos() {
		return new Point(7, 11);
	}

	@Override
	public int getNbrMegaPoints() {
		return 2;
	}

	@Override
	public Map<Point, Point> getTeleportPoints() {
		Map<Point, Point> teleportPoint = new HashMap<>();
		teleportPoint.put(new Point(0, 7), new Point(14, 7));
		teleportPoint.put(new Point(14, 7), new Point(0, 7));
		return teleportPoint;
	}
}
