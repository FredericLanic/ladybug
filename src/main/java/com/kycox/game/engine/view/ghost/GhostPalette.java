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
package com.kycox.game.engine.view.ghost;

import com.kycox.game.body.ghost.GhostColor;

import java.awt.Color;

/**
 * Couleur d'affichage d'un fantôme (textes et traits du mode debug). Avant,
 * cette correspondance était écrite en dur dans le panneau de debug.
 */
public final class GhostPalette {

	private GhostPalette() {
	}

	public static Color of(GhostColor color) {
		return switch (color) {
			case RED -> Color.RED;
			case PINK -> Color.PINK;
			case ORANGE -> Color.ORANGE;
			case CYAN -> Color.CYAN;
		};
	}
}
