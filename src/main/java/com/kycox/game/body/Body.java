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
package com.kycox.game.body;

import java.awt.Point;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kycox.game.constant.Constants;
import com.kycox.game.maths.SpeedFunction;

import lombok.Getter;
import lombok.Setter;

/**
 * Classe abstraite pour les éléments qui bouge dans le jeu
 *
 * Le mouvement peut être calculé ou bien en fonction des joueurs
 */
public abstract class Body {
	private static final Log logger = LogFactory.getLog(Body.class);
	@Getter
	@Setter
	private Point direction = Constants.POINT_ZERO;
	@Getter
	@Setter
	private int leftLifes = 0;
	@Getter
	@Setter
	private boolean newLife = false;
	// Position dans le JPanel
	@Getter
	@Setter
	private Point position = Constants.POINT_ZERO;
	@Setter
	@Getter
	private SpeedFunction speedFunction;
	@Getter
	@Setter
	private int speedIndex = 0;
	@Getter
	private int startSpeedIndex = 0;

	/**
	 * Getters et setters pour la vitesse
	 *
	 * @return
	 */
	public int getSpeed() {
		return Constants.VALID_SPEEDS
		        .get(speedIndex < Constants.VALID_SPEEDS.size() ? speedIndex : Constants.VALID_SPEEDS.size() - 1);
	}

	/**
	 * Initialise la vitesse du fantôme à la construction
	 *
	 * (speed & startSpeed)
	 *
	 * @param speedIndex
	 */
	public void initSpeedIndex(int speedIndex) {
		this.speedIndex = speedIndex;
		this.startSpeedIndex = speedIndex;
	}

	protected abstract boolean isAllowedToDoActions();

	/**
	 * Le fantôme ou ladybug change de block
	 *
	 * @param pointPos : coordonnées DE L'ECRAN (x,y) dans la fenêtre
	 * @return
	 */
	public boolean isPerfectOnABlock() {
		Point pointPos = getPosition();
		return pointPos.x % Constants.BLOCK_SIZE == 0 && pointPos.y % Constants.BLOCK_SIZE == 0;
	}

	/**
	 * Ajout d'une nouvelle vie
	 */
	public void manageNewLife() {
		if (isNewLife()) {
			setNewLife(false);
			leftLifes++;
		}
	}

	public void minusLifesLeft() {
		leftLifes--;
	}
}
