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

import com.kycox.game.constant.ghost.GhostsColorImages;
import com.kycox.game.constant.contract.GhostForView;

import java.awt.*;

public class GhostDefaultView {
	private static final GhostDefaultView ghostDefaultView = new GhostDefaultView();

	public static GhostDefaultView getInstance() {
		return ghostDefaultView;
	}

	/**
	 * Constructeur privé pour assurer le singleton
	 */
	private GhostDefaultView() {
	}

	public Image getImage(GhostForView ghostForView) {
		if (ghostForView.isCamouflage()) {
			return GhostsColorImages.GHOST_CAMOUFLAGE_COLOR.getImage();
		} else {
			return ghostForView.getColor().getImage();
		}
	}
}
