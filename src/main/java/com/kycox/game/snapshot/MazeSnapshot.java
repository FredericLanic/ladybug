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
package com.kycox.game.snapshot;

import com.kycox.game.tools.Position;

import java.util.List;

/**
 * Le labyrinthe à un battement donné (étape 4.6) : ses dimensions, son numéro
 * de forme et toutes ses cases, ligne par ligne.
 *
 * @param layoutVersion   augmente à chaque niveau préparé ; la vue s'en sert
 *                        pour savoir quand reconstruire le contour des murs
 * @param nbrBlocksByLine nombre de cases par ligne
 * @param nbrLines        nombre de lignes
 * @param cells           les cases, ligne par ligne (la case (x, y) est à
 *                        l'indice y × nbrBlocksByLine + x)
 */
public record MazeSnapshot(int layoutVersion, int nbrBlocksByLine, int nbrLines, List<CellSnapshot> cells) {

	public MazeSnapshot {
		cells = List.copyOf(cells);
	}

	/** La case à cette position (en cases), ou {@code null} hors du labyrinthe. */
	public CellSnapshot cellAt(Position block) {
		if (block.x() < 0 || block.y() < 0 || block.x() >= nbrBlocksByLine || block.y() >= nbrLines) {
			return null;
		}
		return cells.get(block.y() * nbrBlocksByLine + block.x());
	}
}
