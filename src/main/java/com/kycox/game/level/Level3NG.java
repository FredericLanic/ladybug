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

import static com.kycox.game.constant.LevelConstruct.B;
import static com.kycox.game.constant.LevelConstruct.N;

import java.awt.Point;

import javax.inject.Named;

import com.kycox.game.constant.LevelConstruct;

@Named("Level3NG")
public class Level3NG extends LevelNG {
	public Level3NG() {
		// nombre de blocks par ligne
		nbrBlocksPerLine = 15;
		// nombre de lignes
		nbrLines = 15;
		levelDATA = new LevelConstruct[] {
		        // 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
		        B, B, B, B, N, N, B, B, B, N, N, B, B, B, B, // 1
		        B, N, N, B, B, B, B, N, B, B, B, B, N, N, B, // 2
		        B, B, B, B, B, B, N, N, N, B, B, B, B, B, B, // 3
		        B, B, N, N, B, B, B, B, B, B, B, N, N, B, B, // 4
		        N, B, N, B, B, B, B, N, B, B, B, B, N, B, N, // 5
		        B, B, B, B, N, N, B, B, B, N, N, B, B, B, B, // 6
		        B, B, N, B, B, N, N, B, N, N, B, B, N, B, B, // 7
		        B, N, N, N, B, B, B, B, B, B, B, N, N, N, B, // 8
		        B, B, N, B, B, N, N, B, N, N, B, B, N, B, B, // 9
		        B, B, B, B, N, N, B, B, B, N, N, B, B, B, B, // 10
		        N, B, N, B, B, B, B, N, B, B, B, B, N, B, N, // 11
		        B, B, N, N, B, B, B, B, B, B, B, N, N, B, B, // 12
		        B, B, B, B, B, B, N, N, N, B, B, B, B, B, B, // 13
		        B, N, N, B, B, B, B, N, B, B, B, B, N, N, B, // 14
		        B, B, B, B, N, N, B, B, B, N, N, B, B, B, B // 15
		};
	}

	@Override
	public Point getGhostRegenerateBlockPoint() {
		return new Point(7, 7);
	}

	@Override
	public Point getInitLadybugBlockPos() {
		return new Point(7, 14);
	}

	@Override
	public int getNbrMegaPoints() {
		return 2;
	}
}
