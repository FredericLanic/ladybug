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

import lombok.Setter;

public class SpeedFunction {
	private double a = 0;
	private double b = 0;
	private final double highLimit = GameMainConstants.VALID_SPEEDS.size();
	@Setter
	private double lowLimit;

	public int getRealIndexSpeed(int numLevel) {
		// calcul de l'index de la vitesse
		return (int) (a * numLevel + b);
	}

	public int getRealIndexSpeedMinus(int numLevel) {
		// calcul de l'index de la vitesse
		var id = getRealIndexSpeed(numLevel);
		if (id > 0) {
			id--;
		}
		return id;
	}

	public int getRealIndexSpeedPlus(int numLevel) {
		// calcul de l'index de la vitesse
		var id = getRealIndexSpeed(numLevel);
		if (id < highLimit - 1) {
			id++;
		}
		return id;
	}

	public void init() {
		a = (highLimit - lowLimit) / (GameMainConstants.NBR_LEVELS - 1);
		b = lowLimit - a;
	}
}
