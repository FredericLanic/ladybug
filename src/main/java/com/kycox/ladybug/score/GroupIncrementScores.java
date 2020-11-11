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

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class GroupIncrementScores {
	/**
	 * Liste des score incréments
	 */
	@Getter
	private List<IncrementScore> lstIncrementScore = new ArrayList<>();

	/**
	 * Ajout d'un score incrément
	 *
	 * @param position
	 * @param value
	 */
	public void add(Point position, String value) {
		lstIncrementScore.add(new IncrementScore(position, value));
	}

	public void addAll(List<IncrementScore> lstIncrementScore) {
		this.lstIncrementScore.addAll(lstIncrementScore);
	}

	/**
	 * Suppression des score incrément qui sont en train de mourir
	 */
	public void removeIfDying() {
		lstIncrementScore.removeIf(IncrementScore::isDying);
	}
}
