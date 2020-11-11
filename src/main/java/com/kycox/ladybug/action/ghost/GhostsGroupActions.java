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
package com.kycox.ladybug.action.ghost;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.score.GroupIncrementScores;

public class GhostsGroupActions {
	private List<GhostActions> lstGhostActions = new ArrayList<>();

	public void addGhostAction(GhostActions ghostActions) {
		lstGhostActions.add(ghostActions);
	}

	/**
	 * Création des Increments Score en fonction des fantômes mangés
	 */
	public void addIncrementScores(GroupIncrementScores groupIncrementScores) {
		lstGhostActions.stream().filter(GhostActions::isEaten).forEach(ga -> groupIncrementScores
		        .add((Point) ga.getGhost().getPosition().clone(), Integer.toString(Constants.SCORE_EATEN_GHOST)));
	}

	public void addNewLifeToKeyGhost() {
		lstGhostActions.stream().filter(GhostActions::isEatLadybug).filter(ga -> !ga.getGhost().isComputed())
		        .forEach(g -> g.getGhost().manageNewLife());
	}

	/**
	 * a mangé Ladybug ?
	 *
	 * @return
	 */
	public boolean eatLadybug() {
		return lstGhostActions.stream().filter(GhostActions::isEatLadybug).count() > 0;
	}

	/**
	 * Retourne le nombres de fantômes mangés
	 *
	 * @return
	 */
	public int getNbrEatenGhost() {
		return (int) lstGhostActions.stream().filter(GhostActions::isEaten).count();
	}

	// à mettre ailleurs ?
	/**
	 * Si le fantôme a été mangé, ses paramètres changent
	 *
	 * (voir Ghost.setSettingJustAfterBeEaten())
	 */
	public void setGhostSettingAfterLadybugContact(int numLevel) {
		// Mis à jour du statut
		lstGhostActions.stream().filter(GhostActions::isEaten)
		        .forEach(ga -> ga.getGhost().setSettingAfterBeEaten(numLevel));
		lstGhostActions.stream().filter(GhostActions::isEaten).forEach(ga -> ga.getGhost().minusLifesLeft());
	}
}
