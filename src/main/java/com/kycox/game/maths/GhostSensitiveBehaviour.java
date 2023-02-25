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
package com.kycox.game.maths;

import com.kycox.game.constant.GameMainConstants;
import com.kycox.game.tools.Utils;

/**
 * Probabilité de comportement
 * Fonction y = ax + b où x est le niveau
 * Qui résoud : (1, 20); (nbrMaxNiveau, 90)
 * Retourne vrai si le nombre aléatoire (0-100) est en dessous de y
 *
 */
public class GhostSensitiveBehaviour {
	private static final double HIGH_LIMIT = 90;
	private static final double LOW_LIMIT = 20;
	private static final int SCALE = 100;
	private double y = 0;

	public boolean isActive() {
		return (Utils.generateRandomInt(SCALE) + 1) < y;
	}

	public void setNumLevel(int numLevel) {
		double a;
		double b;
		a = (HIGH_LIMIT - LOW_LIMIT) / (GameMainConstants.NBR_LEVELS - 1);
		b = LOW_LIMIT - a;
		y = (a * numLevel + b);
	}
}
