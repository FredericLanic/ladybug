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

import java.awt.Point;

import com.kycox.game.timer.IncrementScoreTimer;

import lombok.Getter;
import lombok.Setter;

/**
 * Contenu des valeurs afficées dans la map, lorsque ladybug fait des exploits
 *
 * @author kycox
 *
 */
public class Message {
	@Getter
	@Setter
	private boolean				dying;
	/** Position dans la fenêtre où le score est affiché */
	@Getter
	private Point				position;
	/** Timer d'affichage du score dans la fenêtre */
	private IncrementScoreTimer	scoreTimer;
	/** Valeur à afficher */
	@Getter
	private String				value;

	/**
	 * Constructeur
	 *
	 * @param position : position d'affichage du score
	 * @param value    : valeur à afficher
	 */
	public Message(Point position, String value) {
		this.position = position;
		this.value	  = value;
		this.setDying(false);
		scoreTimer = new IncrementScoreTimer(this);
		scoreTimer.launch(1000);
	}
}
