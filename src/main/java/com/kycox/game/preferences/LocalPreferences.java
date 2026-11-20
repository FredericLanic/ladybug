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
package com.kycox.game.preferences;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Les réglages propres à ce poste, qui ne changent rien à la partie : son, aides
 * à l'écran, mode debug, présence d'une manette.
 *
 * <p>Étape 4.5 : ces réglages vivaient dans le modèle du jeu ({@code GameModel}).
 * Les contrôleurs les écrivent et les vues (et les sons) les lisent directement,
 * sans passer par le modèle. En réseau (étape 9), chaque joueur aura les siens,
 * alors que le modèle sera partagé. Le chapeau, le bandeau et le skin restent
 * dans {@code GameProperties}, lus depuis {@code ladybug.properties}.</p>
 */
@Component
public class LocalPreferences {
	private static final Logger logger = LoggerFactory.getLogger(LocalPreferences.class);

	@Getter
	@Setter
	private boolean showHelpForKeys = false;
	@Getter
	@Setter
	private boolean showHelpForXboxes = false;
	@Getter
	private boolean soundActive = true;
	@Getter
	private boolean debugMode = false;
	@Getter
	@Setter
	private boolean atLeastOneXboxOneConnected = false;

	/**
	 * Touche F2 (ou croix haut de la manette) : coupe ou remet le son.
	 *
	 * @return le nouvel état du son, pour l'annoncer à l'écran
	 */
	public boolean toggleSound() {
		logger.info("startStopSoundActive : " + soundActive);
		soundActive = !soundActive;
		return soundActive;
	}

	/** Touche Entrée : affiche ou cache les informations de debug. */
	public void toggleDebugMode() {
		debugMode = !debugMode;
	}
}
