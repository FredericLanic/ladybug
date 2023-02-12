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

import java.awt.*;

//@Component
public class Level3 extends Level {
	public Level3() {
		// nombre de blocks par ligne
		nbrBlocksByLine = 15;
		// nombre de lignes
		nbrLines = 15;
		levelDATA = new int[] { 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 6, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		        4, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 1, 0, 0, 0, 0, 0, 0, 16, 0, 0, 0, 0, 0, 0, 4, 1, 0, 0,
		        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 1, 0, 0, 0, 0, 0, 0, 0,
		        0, 0, 0, 0, 0, 0, 4, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		        0, 4, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 1, 4,
		        139, 138, 142, 1, 0, 0, 0, 0, 0, 0, 0, 0, 4, 1, 8, 2, 10, 10, 8, 0, 0, 0, 0, 0, 0, 0, 0, 4, 5, 143, 5,
		        139, 138, 142, 0, 0, 0, 0, 0, 0, 0, 0, 4, 9, 10, 8, 10, 10, 10, 8, 8, 8, 8, 8, 8, 8, 8, 12 };
	}

	@Override
	public Point getGhostRegenerateBlockPoint() {
		return new Point(2, 3);
	}

	@Override
	public Point getInitLadybugBlockPos() {
		return new Point(2, 3);
	}

	@Override
	public int getNbrMegaPoints() {
		return 2;
	}
}
