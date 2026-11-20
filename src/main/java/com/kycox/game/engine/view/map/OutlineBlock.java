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

import com.kycox.game.snapshot.CellSnapshot;
import com.kycox.game.tools.Position;
import lombok.Getter;

/**
 * Une case du contour des murs, côté vue : sa position, « mur ou pas », et ses
 * quatre bordures, que {@link MazeOutline} retire pour un beau tracé.
 *
 * <p>Étape 4.6c : remplace la copie des cases du modèle ({@code ScreenBlock}) ;
 * elle est construite à partir de la photo du labyrinthe ({@link CellSnapshot}).
 * La vue n'utilise plus aucune classe du modèle pour dessiner les murs.</p>
 */
public class OutlineBlock {
	@Getter
	private final Position coordinate;
	private final boolean notAccessible;
	private boolean borderLeft;
	private boolean borderUp;
	private boolean borderRight;
	private boolean borderDown;

	OutlineBlock(CellSnapshot cell) {
		coordinate = cell.coordinate();
		notAccessible = cell.notAccessible();
		borderLeft = cell.borderLeft();
		borderUp = cell.borderUp();
		borderRight = cell.borderRight();
		borderDown = cell.borderDown();
	}

	public boolean isNotAccessible() {
		return notAccessible;
	}

	public boolean isBorderLeft() {
		return borderLeft;
	}

	public boolean isBorderUp() {
		return borderUp;
	}

	public boolean isBorderRight() {
		return borderRight;
	}

	public boolean isBorderDown() {
		return borderDown;
	}

	void removeBorderLeft() {
		borderLeft = false;
	}

	void removeBorderUp() {
		borderUp = false;
	}

	void removeBorderRight() {
		borderRight = false;
	}

	void removeBorderDown() {
		borderDown = false;
	}
}
