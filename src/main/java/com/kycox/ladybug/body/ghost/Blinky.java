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
import com.kycox.ladybug.constant.ghost.GhostsSettingsEnum;
import com.kycox.ladybug.maths.BlinkySpeedIndex;
import com.kycox.ladybug.maths.SpeedFunction;

/**
 * Setting du fantôme Blinky
 *
 */
public class Blinky extends Ghost {
	private BlinkySpeedIndex blinkyIncrementSpeed = null;

	/**
	 * Constructeur
	 */
	public Blinky(int numLevel) {
		super(numLevel);
		setGhostSettings(GhostsSettingsEnum.BLINKY);
	}

	@Override
	public void getInitSpeed(int numLevel) {
		// Vitesse de Ladybug moins 1
		initSpeedIndex(SpeedFunction.getInstance().getRealIndexSpeedMinus(numLevel));
	}

	public void setBlinkyIncrementSpeed(BlinkySpeedIndex blinkyIncrementSpeed) {
		this.blinkyIncrementSpeed = blinkyIncrementSpeed;
	}

	@Override
	public void setSpeed(int numLevel, int perCent) {
		if (!changeBlock()) {
			// setSpeedIndex(getSpeedIndex());
			return;
		}
		if (GhostStatusEnum.isScared().test(this))
			setSpeedIndex(SpeedFunction.getInstance().getRealIndexSpeedMinus(numLevel) - 1);
		else {
			// Calcul de la vitesse de Blinky en fonction du nombre de point restant dans la
			// map
			int incrementSpeedIndex = blinkyIncrementSpeed.getIncrementSpeedIndex();
			setSpeedIndex(getStartIndexSpeed() + incrementSpeedIndex);
		}
	}
}
