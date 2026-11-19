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
package com.kycox.game.engine.score;

import java.awt.Point;

import com.kycox.game.tools.timer.TickCountdown;

import lombok.Getter;

/**
 * Message affiché un court instant dans le labyrinthe (points gagnés, etc.).
 *
 * <p>Étape 3.5.1d : le message compte lui-même ses battements d'affichage
 * ({@link TickCountdown}, 1 s = 25 battements), avancé par la boucle du jeu via
 * {@link GroupMessages#tick()}, au lieu de lancer sa propre minuterie dans un
 * autre fil d'exécution.
 */
public class Message {
	/** Durée d'affichage d'un message, en millisecondes. */
	static final long DISPLAY_MILLISECONDS = 1000;

	@Getter
	private boolean dying;
	@Getter
	private final MessageType messageType;
	/** Position dans la fenêtre où le score est affiché */
	@Getter
	private final Point position;
	/** Battements d'affichage restants */
	private final TickCountdown displayCountdown = new TickCountdown();
	/** Valeur à afficher */
	@Getter
	private final String value;

	public Message(Point position, String value, MessageType messageType) {
		this.position = position;
		this.value = value;
		this.messageType = messageType;
		dying = false;
		displayCountdown.start(DISPLAY_MILLISECONDS);
	}

	/** Avance d'un battement ; le message meurt à la fin de son affichage. */
	public void tick() {
		if (displayCountdown.tick()) {
			dying = true;
		}
	}
}
