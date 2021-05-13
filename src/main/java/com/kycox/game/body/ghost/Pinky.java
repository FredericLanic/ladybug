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
package com.kycox.game.body.ghost;

import com.kycox.game.constant.ghost.GhostStatus;

/**
 * Spécificités du fantôme Pinky
 *
 */
public class Pinky extends Ghost {
	@Override
	public void setInitSpeed(int numLevel) {
		initSpeedIndex(getSpeedFunction().getRealIndexSpeedMinus(numLevel));
	}

	@Override
	public void setSpeed(int numLevel, int perCent) {
		if (!isPerfectOnABlock()) {
			return;
		}
		if (getStatus() == GhostStatus.SCARED)
			setSpeedIndex(getSpeedFunction().getRealIndexSpeedMinus(numLevel) - 1);
		else
			setSpeedIndex(getStartSpeedIndex());
	}
}
