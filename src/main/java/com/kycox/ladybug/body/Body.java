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

/**
 * Classe abstraite pour les éléments qui bouge dans le jeu
 *
 * Le mouvement peut être calculé ou bien en fonction des joueurs
 */
public abstract class Body {
	// Direction du fantôme
	private Point dirPoint = Constants.POINT_ZERO;
	// nombre de vie restant pour ce body
	private int lifesLeft = 0;
	// Position dans le JPanel
	private Point posPoint = Constants.POINT_ZERO;
	// Vitesse : index dans le tableau VALID_SPEEDS
	private int speedIndex = 0;
	// Vitesse initiale : index dans le tableau VALID_SPEEDS
	private int startSpeedIndex = 0;

	/**
	 * Ajout d'une nouvelle vie
	 */
	public void addNewLife() {
		lifesLeft++;
	}

	/**
	 * Le fantôme ou ladybug change de block
	 *
	 * @param pointPos : coordonnées DE L'ECRAN (x,y) dans la fenêtre
	 * @return
	 */
	public boolean changeBlock() {
		Point pointPos = getPosition();
		return pointPos.x % Constants.BLOCK_SIZE == 0 && pointPos.y % Constants.BLOCK_SIZE == 0;
	}

	/**
	 * Retourne la direction
	 *
	 * @return
	 */
	public Point getDirection() {
		return dirPoint;
	}

	public int getLifesLeft() {
		return lifesLeft;
	}

	/**
	 * Retourne la position
	 *
	 * @return
	 */
	public Point getPosition() {
		return posPoint;
	}

	/**
	 * Getters et setters pour la vitesse
	 *
	 * @return
	 */
	public int getSpeed() {
		return Constants.VALID_SPEEDS.get(speedIndex);
	}

	public int getSpeedIndex() {
		return speedIndex;
	}

	/**
	 * Return the start speed
	 *
	 * @return
	 */
	public int getStartIndexSpeed() {
		return startSpeedIndex;
	}

	/**
	 * Initialise la vitesse du fantôme à la construction
	 *
	 * (speed & startSpeed)
	 *
	 * @param speedIndex
	 */
	public void initSpeedIndex(int speedIndex) {
		this.speedIndex		 = speedIndex;
		this.startSpeedIndex = speedIndex;
	}

	public void minusLifesLeft() {
		lifesLeft--;
		System.out.println("lifesLeft:" + lifesLeft);
	}

	/**
	 * Affecte une direction
	 *
	 * @param dirPoint
	 */
	public void setDirection(Point dirPoint) {
		this.dirPoint = dirPoint;
	}

	public void setLeftLifes(int lifesLeft) {
		this.lifesLeft = lifesLeft;
	}

	/**
	 * Affecte une position
	 *
	 * @param posPoint
	 */
	public void setPosition(Point posPoint) {
		this.posPoint = posPoint;
	}

	/**
	 * Affecte la vitesse
	 *
	 * @param speedIndex
	 */
	public void setSpeedIndex(int speedIndex) {
		// FIXME : rajouter un test si speenIndex > VALID_SPEEDS.length
		// ou pas, comme ça j'aurai une exception et je pourrai corriger le problème
		this.speedIndex = speedIndex;
	}
}