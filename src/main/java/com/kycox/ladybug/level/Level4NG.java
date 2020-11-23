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

import static com.kycox.ladybug.constant.LevelConstructEnum.B;
import static com.kycox.ladybug.constant.LevelConstructEnum.N;

import java.awt.Point;

import javax.inject.Named;

import com.kycox.ladybug.constant.LevelConstructEnum;

@Named("Level4NG")
public class Level4NG extends LevelNG {
	public Level4NG() {
		// nombre de blocks par ligne
		nbrBlocksPerLine = 15;
		// nombre de lignes
		nbrLines  = 15;
		levelDATA = new LevelConstructEnum[] {
		        // 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
		        B, B, B, N, B, B, B, B, B, B, B, N, B, B, B,	  // 1
		        B, B, B, N, B, N, N, B, N, N, B, N, B, B, B,	  // 2
		        N, N, B, B, B, B, B, B, B, B, B, B, B, N, N,	  // 3
		        N, B, B, B, N, B, B, B, B, B, N, B, B, B, N,	  // 4
		        N, B, B, N, N, N, N, B, B, N, N, N, N, B, N,	  // 5
		        N, B, B, N, B, N, B, B, B, B, N, B, N, B, N,	  // 6
		        B, B, B, B, B, N, N, B, B, N, N, B, B, B, B,	  // 7
		        B, N, N, N, N, N, N, N, N, N, N, N, N, N, B,	  // 8
		        B, B, B, N, B, B, B, B, B, B, B, N, B, B, B,	  // 9
		        N, B, B, B, N, B, N, B, N, B, N, B, B, B, N,	  // 10
		        B, N, B, B, B, N, B, B, B, N, B, B, B, N, B,	  // 11
		        B, N, B, B, B, B, B, B, B, B, B, B, B, N, B,	  // 12
		        B, B, B, N, N, N, N, B, N, N, N, N, B, B, B,	  // 13
		        B, B, N, B, N, B, N, B, N, B, N, B, N, B, B,	  // 14
		        B, B, B, B, B, B, N, B, N, B, B, B, B, B, B		  // 15
		};
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
}
