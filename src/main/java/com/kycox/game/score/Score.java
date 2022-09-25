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

import com.kycox.game.body.ghost.GhostsGroup;
import com.kycox.game.body.ladybug.Ladybug;
import com.kycox.game.constant.Constants;
import com.kycox.game.fruit.Fruits;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Score du jeu
 *
 */
@Component
public class Score {
	@Inject
	private Fruits fruits;
	@Getter
	private int incrementScore;
	@Getter
	@Setter
	private int oldScore = -1;
	@Getter
	private int score;

	/**
	 * Ajout du score et du score incrémental
	 *
	 * @param score
	 */
	public void addScore(int score) {
		this.score += score;
		incrementScore += score;
	}

	public int getNbrPointsForNewLife() {
		return Constants.NBR_POINTS_FOR_NEW_LIFE;
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
	 * FIXME : on doit sortir cette méthode du score.
	 */
	private void setMessages(GhostsGroup ghostsGroup, Ladybug ladybug, GroupMessages groupMessages) {
		// Ladybug digère et elle laisse tout derrière elle :/
//		if (ladybug.isEatenAPoint() && !ladybug.isEatenAMegaPoint() && !ladybug.isToBeTeleported()
//		        && ghostsGroup.getNbrEatenGhosts() == 0) {
//			groupMessages.add(ladybug.getPosition(), "💩 💩", MessageType.STRING);
//		}
		if (ladybug.isEatenAMegaPoint()) {
			groupMessages.add(ladybug.getPosition(), Integer.toString(Constants.SCORE_MEGA_POINT), MessageType.POINT);
		}
		if (ladybug.isToBeTeleported()) {
			groupMessages.add(ladybug.getPosition(), Integer.toString(Constants.SCORE_TELEPORT_POINT),
			        MessageType.POINT);
		}
		if (ladybug.hasEatenAFruit()) {
			groupMessages.add(ladybug.getPosition(),
			        Integer.toString(fruits.getScoreFruitById(ladybug.getEatenAIdRefFruit())), MessageType.POINT);
		}
		addScore(ghostsGroup.getNbrEatenGhosts() * Constants.SCORE_EATEN_GHOST);
		ghostsGroup.getGhosts().stream().filter(g -> g.getGhostActions().isEatenByLadybug())
		        .forEach(g -> groupMessages.add(g.getPosition(), Integer.toString(Constants.SCORE_EATEN_GHOST),
		                MessageType.POINT));
	}

	private void setScore(GhostsGroup ghostsGroup, Ladybug ladybug) {
		if (ladybug.isEatenAPoint()) {
			addScore(Constants.SCORE_SIMPLE_POINT);
		}
		if (ladybug.isEatenAMegaPoint()) {
			addScore(Constants.SCORE_MEGA_POINT);
		}
		if (ladybug.isToBeTeleported()) {
			addScore(Constants.SCORE_TELEPORT_POINT);
		}
		if (ladybug.hasEatenAFruit()) {
			addScore(fruits.getScoreFruitById(ladybug.getEatenAIdRefFruit()));
		}

		addScore(ghostsGroup.getNbrEatenGhosts() * Constants.SCORE_EATEN_GHOST);
	}

	/**
	 * Adjust the score number
	 */
	public void setScoreAndMessages(GhostsGroup ghostsGroup, Ladybug ladybug, GroupMessages groupMessages) {
		setScore(ghostsGroup, ladybug);
		setMessages(ghostsGroup, ladybug, groupMessages);
	}
}
