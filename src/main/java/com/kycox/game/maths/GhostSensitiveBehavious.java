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
package com.kycox.game.maths;

import java.util.Random;

import com.kycox.game.constant.Constants;

/**
 * Probabilité de comportement
 *
 * Fonction y = ax + b où x est le niveau
 *
 * Qui résoud : (1, 20); (nbrMaxNiveau, 90)
 *
 * Retourne vrai si le nombre aléatoire (0-100) est en dessous de y
 *
 */
public class GhostSensitiveBehavious {
	private final double HIGH_LIMIT		  = 90;
	private final double LOW_LIMIT		  = 20;
	private final Random RANDOM_GENERATOR = new Random();
	private final int	 SCALE			  = 100;
	private double		 y				  = 0;

	/**
	 *
	 * Créé un nombre aléatoire [0-100] et définie si ce nombre est inférieur à une
	 * règle spécifique [limiteBasse, limiteHaute]
	 *
	 * @param numLevel
	 * @return
	 */
	public boolean isActive() {
		return (RANDOM_GENERATOR.nextInt(SCALE) + 1) < y;
	}

	public void setNumLevel(int numLevel) {
		double a = 0;
		double b = 0;
		a = (HIGH_LIMIT - LOW_LIMIT) / (Constants.NIVEAU_MAX - 1);
		b = LOW_LIMIT - a;
		y = (a * numLevel + b);
	}
}
