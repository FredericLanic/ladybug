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

import org.springframework.stereotype.Component;

import java.awt.*;

@Component("Level1")
public final class Level1 extends Level {
	// Représentation du level initial
	// 0000001 : barre à gauche 1
	// 0000010 : barre en haut 2
	// 0000100 : barre à droite 4
	// 0001000 : barre en bas 8
	// 0010000 : point 16
	// 0100000 : superpower 32
	// 1000000 : survivor ghost 64
	// 10000000 : double traits 128
	public Level1() {
		// nombre de blocks par ligne
		nbrBlocksByLine = 15;
		// nombre de lignes
		nbrLines = 15;
		levelDATA = new int[] { 19, 26, 26, 26, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22, 21, 128, 128, 128, 17, 16,
		        16, 16, 16, 16, 16, 16, 16, 16, 20, 17, 26, 10, 26, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 21, 128,
		        128, 128, 17, 16, 16, 24, 16, 16, 16, 16, 16, 16, 20, 17, 18, 18, 18, 16, 16, 20, 128, 17, 16, 16, 16,
		        16, 16, 20, 17, 16, 16, 16, 16, 16, 20, 128, 17, 16, 16, 16, 16, 24, 20, 25, 16, 16, 16, 24, 24, 28,
		        128, 25, 24, 24, 16, 20, 128, 21, 129, 17, 16, 20, 128, 128, 128, 128, 128, 128, 128, 17, 20, 128, 21,
		        129, 17, 16, 16, 18, 18, 22, 128, 19, 18, 18, 16, 20, 128, 21, 129, 17, 16, 16, 16, 16, 20, 128, 17, 16,
		        16, 16, 20, 128, 21, 129, 17, 16, 16, 16, 16, 20, 128, 17, 16, 16, 16, 20, 128, 21, 129, 17, 16, 16, 16,
		        16, 16, 18, 16, 16, 16, 16, 20, 128, 21, 129, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 128, 21,
		        129, 25, 24, 24, 24, 24, 24, 24, 24, 24, 16, 16, 16, 18, 20, 137, 136, 136, 136, 136, 136, 136, 136,
		        136, 136, 25, 24, 24, 24, 28 };
	}

	@Override
	public Point getGhostRegenerateBlockPoint() {
		return new Point(2, 2);
	}

	@Override
	public Point getInitLadybugBlockPos() {
		return new Point(7, 11);
	}

	@Override
	public int getNbrMegaPoints() {
		return 2;
	}
}
