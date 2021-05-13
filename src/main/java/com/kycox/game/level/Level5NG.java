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

@Named("Level5NG")
public class Level5NG extends LevelNG {
	public Level5NG() {
		// nombre de blocks par ligne
		nbrBlocksPerLine = 15;
		// nombre de lignes
		nbrLines = 15;
		levelDATA = new LevelConstruct[] {
		        // 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15
		        B, B, B, B, N, N, B, N, B, N, N, B, B, B, B, // 1
		        B, B, N, B, B, B, B, B, B, B, B, B, N, B, B, // 2
		        B, N, N, N, B, N, B, N, B, N, B, N, N, N, B, // 3
		        B, B, B, N, B, B, B, N, B, B, B, N, B, B, B, // 4
		        N, B, B, B, B, N, B, B, B, N, B, B, B, B, N, // 5
		        B, B, N, N, B, N, B, N, B, N, B, N, N, B, B, // 6
		        B, B, B, B, B, B, B, N, B, B, B, B, B, B, B, // 7
		        N, N, B, B, N, N, B, B, B, N, N, B, B, N, N, // 8
		        B, B, B, B, B, B, B, N, B, B, B, B, B, B, B, // 9
		        B, B, N, N, B, N, B, N, B, N, B, N, N, B, B, // 10
		        N, B, B, B, B, N, B, B, B, N, B, B, B, B, N, // 11
		        B, B, B, N, B, B, B, N, B, B, B, N, B, B, B, // 12
		        B, B, N, N, B, N, B, N, B, N, B, N, N, B, B, // 13
		        B, N, N, B, B, B, B, B, B, B, B, B, N, N, B, // 14
		        B, B, B, B, N, N, B, N, B, N, N, B, B, B, B // 15
		};
	}

	@Override
	public Point getGhostRegenerateBlockPoint() {
		return new Point(7, 4);
	}

	@Override
	public Point getInitLadybugBlockPos() {
		return new Point(7, 13);
	}

	@Override
	public int getNbrMegaPoints() {
		return 2;
	}
}
