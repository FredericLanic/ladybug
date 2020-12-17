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
package com.kycox.ladybug.body.ghost;

import com.kycox.ladybug.constant.ghost.GhostStatusEnum;

/**
 * Spécificité du fantôme Clyde
 *
 */
public class Clyde extends Ghost {
	@Override
	public void setInitSpeed(int numLevel) {
		initSpeedIndex(getSpeedFunction().getRealIndexSpeed(numLevel));
	}

	@Override
	public void setSpeed(int numLevel, int perCent) {
		/*
		 * Idée : faire accélerer ou ralentir Clyde de manière aléatoire....
		 */
		if (!hasChangeBlock()) {
			return;
		}
		if (GhostStatusEnum.SCARED.equals(getStatus()))
			setSpeedIndex(getSpeedFunction().getRealIndexSpeedMinus(numLevel) - 1);
		else
			setSpeedIndex(getStartSpeedIndex());
	}
}
