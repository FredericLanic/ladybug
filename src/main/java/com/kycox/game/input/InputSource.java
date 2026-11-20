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

/**
 * D'où vient un ordre de déplacement (étape 5.1). Sert à savoir quel appareil
 * pilote quel personnage : le journal du jeu le note à chaque changement.
 */
public enum InputSource {
	KEYBOARD_ARROWS("the keyboard (arrows)"),
	KEYBOARD_ZQSD("the keyboard (ZQSD)"),
	XBOX_PAD_1("the Xbox pad 1"),
	XBOX_PAD_2("the Xbox pad 2");

	private final String description;

	InputSource(String description) {
		this.description = description;
	}

	/** Texte pour le journal du jeu. */
	public String description() {
		return description;
	}
}
