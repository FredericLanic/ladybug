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
package com.kycox.game.score;

import javax.inject.Named;

import com.kycox.game.body.ghost.GhostsGroup;
import com.kycox.game.body.ladybug.Ladybug;
import com.kycox.game.constant.Constants;

import lombok.Getter;
import lombok.Setter;

/**
 * Score du jeu
 *
 */
@Named("GameScore")
public class GameScore {
	@Getter
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
		initIncrementScore();
		score = 0;
	}

	public void initIncrementScore() {
		incrementScore = 0;
	}

	/**
	 * Adjust the score number
	 */
	public void setScore(GhostsGroup ghostsGroup, Ladybug ladybug, GroupMessages groupMessages) {
		if (ladybug.isEatenAMegaPoint()) {
			addScore(Constants.SCORE_MEGA_POINT);
			groupMessages.add(ladybug.getPosition(), Integer.toString(Constants.SCORE_MEGA_POINT));
		}
		if (ladybug.isEatenAPoint()) {
			addScore(Constants.SCORE_SIMPLE_POINT);
		}
		if (ladybug.isToBeTeleported()) {
			addScore(Constants.SCORE_TELEPORT_POINT);
			groupMessages.add(ladybug.getPosition(), Integer.toString(Constants.SCORE_TELEPORT_POINT));
		}
		addScore(ghostsGroup.getNbrEatenGhost() * Constants.SCORE_EATEN_GHOST);
		ghostsGroup.getLstGhosts().stream().filter(g -> g.getGhostActions().isEaten())
		        .forEach(g -> groupMessages.add(g.getPosition(), Integer.toString(Constants.SCORE_EATEN_GHOST)));
	}
}
