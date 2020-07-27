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

import com.kycox.ladybug.level.ScreenData;

public class BlinkySpeedIndex {
	/**
	 * aX²+bX+c (c=0)
	 *
	 * Point (0,0) Point (R,1) Point (T,3) (ici T=100%)
	 *
	 * 1 < R < T
	 *
	 */
	private static double centPerCent  = 100.0;
	private double		  a;
	private double		  b;
	private double		  levelPerCent = 80.0;
	private ScreenData	  screenData   = null;

	public BlinkySpeedIndex(int numLevel, ScreenData screenData) {
		this.screenData = screenData;
		// calcul de R en fonction du niveau
		// pour commencer : on met R = 80
		// FIXME : puis on devrait faire R = 80 pour le niveau 1, et R = 20 pour le
		// dernier
		// niveau
		a = (centPerCent - 3 * levelPerCent)
		        / (levelPerCent * levelPerCent * centPerCent - levelPerCent * centPerCent * centPerCent);
		b = (centPerCent * centPerCent - 3 * levelPerCent * levelPerCent)
		        / (levelPerCent * centPerCent * (centPerCent - levelPerCent));
	}

	/**
	 * Return the number of index to increment
	 *
	 * @param x
	 * @return
	 */
	public int getIncrementSpeedIndex() {
		int x = screenData.getPercentageEatenPoint();
		return (int) (a * x * x + b * x);
	}
}
