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
package com.kycox.game.engine.view.map;

import java.awt.Color;

/**
 * Couleurs des murs du labyrinthe. Étape 4.5 : déplacées depuis
 * {@code GameMainConstants}, pour que les constantes partagées avec le modèle
 * n'importent plus rien d'AWT.
 */
public final class MazeColors {
	/** Bleu habituel des murs. */
	public static final Color BLUE_LADYBUG = new Color(33, 33, 255);
	/**
	 * Or de l'effet « vie gagnée » (D25) : les murs clignotent dans cette teinte,
	 * et le texte « +1 VIE » est écrit dans cette couleur.
	 */
	public static final Color NEW_LIFE_GOLD = new Color(255, 196, 36);

	private MazeColors() {
	}
}
