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

import com.kycox.game.constant.level.LevelConstruct;
import org.springframework.stereotype.Component;

import java.awt.*;

import static com.kycox.game.constant.level.LevelConstruct.B;
import static com.kycox.game.constant.level.LevelConstruct.N;

@Component
public class SecondGenerationLevel6 extends SecondGenerationLevel {
	public SecondGenerationLevel6() {
		// nombre de blocks par ligne
		nbrBlocksPerLine = 15;
		// nombre de lignes
		nbrLines = 15;
		levelDATA = new LevelConstruct[] {
		        // 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15
		        B, B, B, B, B, B, B, B, B, B, B, B, B, B, B, // 1
		        B, N, N, N, B, N, N, N, N, N, B, N, N, N, B, // 2
		        B, N, B, B, B, B, B, B, B, B, B, B, B, N, B, // 3
		        B, N, B, N, N, B, N, N, N, B, N, N, B, N, B, // 4
		        B, B, B, N, B, B, B, B, B, B, B, N, B, B, B, // 5
		        B, N, B, B, B, N, N, B, N, N, B, B, B, N, B, // 6
		        B, N, B, N, B, N, B, B, B, N, B, N, B, N, B, // 7
		        B, N, B, N, B, N, N, B, N, N, B, N, B, N, B, // 8
		        B, N, B, N, B, N, B, B, B, N, B, N, B, N, B, // 9
		        B, N, B, B, B, N, N, B, N, N, B, B, B, N, B, // 10
		        B, B, B, N, B, B, B, B, B, B, B, N, B, B, B, // 11
		        B, N, B, N, N, B, N, N, N, B, N, N, B, N, B, // 12
		        B, N, B, B, B, B, B, B, B, B, B, B, B, N, B, // 13
		        B, N, N, N, B, N, N, B, N, N, B, N, N, N, B, // 14
		        B, B, B, B, B, B, B, B, B, B, B, B, B, B, B // 15
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
