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
package com.kycox.ladybug.score;

import com.kycox.ladybug.action.ghost.GhostsGroupActions;
import com.kycox.ladybug.action.ladybug.LadybugActions;
import com.kycox.ladybug.constant.Constants;

import lombok.Getter;
import lombok.Setter;

/**
 * Score du jeu
 *
 */
public class GameScore {
	@Getter
	@Setter
	private int	incrementScore;
	@Getter
	@Setter
	private int	oldScore = -1;
	@Getter
	private int	score;

	/**
	 * Ajout du score et du score incrémental
	 *
	 * @param score
	 */
	public void addScore(int score) {
		this.score			+= score;
		this.incrementScore	+= score;
	}

	/**
	 * Initialise le score et le score incrémental
	 */
	public void init() {
		incrementScore = 0;
		score		   = 0;
	}

	/**
	 * Adjust the score number
	 */
	public void setScore(GhostsGroupActions allGhostsActions, LadybugActions ladybugActions) {
		if (ladybugActions.isEatenAMegaPoint())
			addScore(Constants.SCORE_MEGA_POINT);
		if (ladybugActions.isEatenAPoint())
			addScore(Constants.SCORE_SIMPLE_POINT);
		addScore(allGhostsActions.getNbrEatenGhost() * Constants.SCORE_EATEN_GHOST);
	}
}
