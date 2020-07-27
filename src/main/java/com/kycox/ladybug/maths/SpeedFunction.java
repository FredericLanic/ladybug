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
package com.kycox.ladybug.maths;

import com.kycox.ladybug.constant.Constants;

public class SpeedFunction {
	private static final SpeedFunction ladybugSpeed = new SpeedFunction();

	public static SpeedFunction getInstance() {
		return ladybugSpeed;
	}

	private double a = 0;
	private double b = 0;
	// échelle de 1 à 10
	private final double LOW_LIMIT	 = 2;
	private final double HIGHT_LIMIT = Constants.VALID_SPEEDS.size();

	private SpeedFunction() {
		a = (HIGHT_LIMIT - LOW_LIMIT) / (Constants.NIVEAU_MAX - 1);
		b = LOW_LIMIT - a;
	}

	/**
	 * Retourne la vitesse calculée de ladybug en fonction du niveau
	 *
	 * @param numLevel
	 * @return
	 */
	public int getRealIndexSpeed(int numLevel) {
		// calcul de l'index de la vitesse
		return (int) (a * numLevel + b);
	}

	/**
	 * Retourne la vitesse calculée -1 de ladybug en fonction du niveau
	 *
	 * @param numLevel
	 * @return
	 */
	public int getRealIndexSpeedMinus(int numLevel) {
		// calcul de l'index de la vitesse
		int id = getRealIndexSpeed(numLevel);
		if (id > 0)
			id--;
		return id;
	}

	/**
	 * Retourne la vitesse calculée +1 de ladybug en fonction du niveau
	 *
	 * @param numLevel
	 * @return
	 */
	public int getRealIndexSpeedPlus(int numLevel) {
		// calcul de l'index de la vitesse
		int id = getRealIndexSpeed(numLevel);
		if (id < HIGHT_LIMIT - 1)
			id++;
		return id;
	}

	/**
	 * Retourne la vitesse de ladybug spéficique
	 *
	 */
	public int getSpecificIndexSpeed(int numLevel) {
		int id = (int) (a * numLevel + b);
		if (id < HIGHT_LIMIT - 1 && numLevel <= Constants.NIVEAU_MAX / 2)
			id++;
		return id;
	}
}