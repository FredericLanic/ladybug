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
package com.kycox.game.body.ghost;

import com.kycox.game.constant.ghost.GhostStatusEnum;
import com.kycox.game.maths.BlinkySpeedIndex;

import lombok.Setter;

/**
 * Setting du fantôme Blinky
 *
 */
public class Blinky extends Ghost {
	@Setter
	private BlinkySpeedIndex blinkySpeedIndex;

	@Override
	public void setInitSpeed(int numLevel) {
		// Vitesse de Ladybug moins 1
		initSpeedIndex(getSpeedFunction().getRealIndexSpeedMinus(numLevel));
	}

	@Override
	public void setNumLevel(int numLevel) {
		super.setNumLevel(numLevel);
		blinkySpeedIndex.setNumLevel();
	}

	@Override
	public void setSpeed(int numLevel, int perCent) {
		if (!hasChangeBlock())
			return;
		if (GhostStatusEnum.SCARED.equals(getStatus()))
			setSpeedIndex(getSpeedFunction().getRealIndexSpeedMinus(numLevel) - 1);
		else {
			// Calcul de la vitesse de Blinky en fonction du nombre de point restant dans la
			// map
			setSpeedIndex(getStartSpeedIndex() + blinkySpeedIndex.getIncrementSpeedIndex(perCent));
		}
	}
}
