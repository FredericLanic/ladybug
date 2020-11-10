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
package com.kycox.ladybug.body;

import java.awt.Point;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.level.ScreenBlock;

import lombok.Setter;

public abstract class UserBody extends Body {
	// Les direction de ladyBug (requête suite aux touches et la vue)
	@Setter
	protected Point userRequest = Constants.POINT_ZERO;

	/**
	 * Retourne vrai si l'élément peut bouger en fonction des cases à côté
	 *
	 * @param direction
	 * @param screenBlock
	 * @return
	 */
	protected boolean canMove(Point direction, ScreenBlock screenBlock) {
		return !(direction.equals(Constants.POINT_LEFT) && screenBlock.isBorderLeft() // ladybug est coincé à
		                                                                        // gauche
		        || direction.equals(Constants.POINT_RIGHT) && screenBlock.isBorderRight() // ladybug est coincé en
		                                                                            // haut
		        || direction.equals(Constants.POINT_UP) && screenBlock.isBorderUp() // ladybug est coincé à droite
		        || direction.equals(Constants.POINT_DOWN) && screenBlock.isBorderDown() // ladybug est coincé en
		                                                                          // bas
		);
	}

	/**
	 * Déplacement de l'élément
	 *
	 * @param screenBlock
	 */
	protected void move(ScreenBlock screenBlock) {
		if (hasChangeBlock()) {
			if ((userRequest.x != 0 || userRequest.y != 0) && canMove(userRequest, screenBlock)) {
				setDirection(userRequest);
			}
			/**
			 * Quand le body est coincé : au début ou quand il butte sur un mur.
			 */
			if (!canMove(getDirection(), screenBlock)) {
				setDirection(Constants.POINT_ZERO);
			}
		}
	}
}
