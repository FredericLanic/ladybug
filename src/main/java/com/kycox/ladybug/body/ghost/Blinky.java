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
import com.kycox.ladybug.maths.BlinkySpeedIndex;
import com.kycox.ladybug.maths.GhostSensitiveBehavious;
import com.kycox.ladybug.maths.SpeedFunction;

import lombok.Setter;

/**
 * Setting du fantôme Blinky
 *
 */
public class Blinky extends Ghost {
	@Setter
	private BlinkySpeedIndex incrementSpeed = null;

	/**
	 * Constructeur
	 */
	public Blinky() {
		super();
	}

	@Override
	public void getInitSpeed(int numLevel) {
		// Vitesse de Ladybug moins 1
		initSpeedIndex(SpeedFunction.getInstance().getRealIndexSpeedMinus(numLevel));
	}

	@Override
	public void setNumLevel(int numLevel) {
		// initialise le comportement du fantôme en fonction du niveau
		setSensitiveBehavious(new GhostSensitiveBehavious(numLevel));
		// Affectation de l'objet de gestion de sa vitesse incrémentale
		BlinkySpeedIndex blinkyIncrementSpeed = new BlinkySpeedIndex(numLevel);
		setIncrementSpeed(blinkyIncrementSpeed);
	}

	@Override
	public void setSpeed(int numLevel, int perCent) {
		if (!changeBlock())
			return;
		if (GhostStatusEnum.isScared().test(this))
			setSpeedIndex(SpeedFunction.getInstance().getRealIndexSpeedMinus(numLevel) - 1);
		else {
			// Calcul de la vitesse de Blinky en fonction du nombre de point restant dans la
			// map
			setSpeedIndex(getStartSpeedIndex() + incrementSpeed.getIncrementSpeedIndex(perCent));
		}
	}
}
