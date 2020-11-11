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
		lstGhostActions.stream().filter(GhostActions::isEaten).forEach(
		        ga -> groupIncrementScores.add(ga.getPosition(), Integer.toString(Constants.SCORE_EATEN_GHOST)));
	}
}
