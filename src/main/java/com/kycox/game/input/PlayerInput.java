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
package com.kycox.game.input;

import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.tools.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * La « boîte aux lettres » d'un joueur : la dernière direction qu'il a demandée
 * pour son personnage. Elle reste valable jusqu'à la demande suivante (on peut
 * appuyer sur une flèche avant d'arriver au carrefour).
 *
 * <p>Étape 5.1 : chaque personnage piloté par un humain lit ses ordres ici.
 * Clavier et manette y écrivent directement, sans passer par le modèle ; en
 * réseau (étape 9), l'hôte y écrira ce que le client lui envoie. Voir
 * {@code doc/patterns/player-input.md}.</p>
 */
public class PlayerInput {
	private static final Logger logger = LoggerFactory.getLogger(PlayerInput.class);

	/** Nom du personnage piloté, pour le journal. */
	private final Supplier<String> owner;
	private Position requested = GameMainConstants.POINT_ZERO;
	/** Dernier appareil qui a donné un ordre, s'il est connu. */
	private InputSource lastSource;

	public PlayerInput(Supplier<String> owner) {
		this.owner = owner;
	}

	/** Une nouvelle direction demandée, sans appareil connu (bot, scénario de test). */
	public void request(Position direction) {
		requested = direction;
	}

	/**
	 * Une nouvelle direction demandée par cet appareil. Quand l'appareil change,
	 * le journal du jeu le note (« Ladybug is now driven by the keyboard »).
	 */
	public void request(Position direction, InputSource source) {
		if (source != lastSource) {
			logger.info("{} is now driven by {}", owner.get(), source.description());
			lastSource = source;
		}
		request(direction);
	}

	/** Le dernier appareil qui a donné un ordre, s'il y en a eu un. */
	public Optional<InputSource> lastSource() {
		return Optional.ofNullable(lastSource);
	}

	/** La dernière direction demandée, ou {@code POINT_ZERO} si aucune. */
	public Position requested() {
		return requested;
	}

	/** Vrai si une direction a été demandée. */
	public boolean hasRequest() {
		return requested.x() != 0 || requested.y() != 0;
	}

	/** Oublie la demande (début de niveau). */
	public void reset() {
		requested = GameMainConstants.POINT_ZERO;
	}
}
