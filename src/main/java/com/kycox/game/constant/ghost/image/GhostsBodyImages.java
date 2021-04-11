/**
  Copyright (C) 2019  Frédéric Lanic frederic.lanic@outlook.fr

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
package com.kycox.game.constant.ghost.image;

import java.awt.Image;

import lombok.Getter;
import lombok.Setter;

public enum GhostsBodyImages {
	BLUE(GhostsColorImages.GHOST_COLOR_BLUE, true), ORANGE(GhostsColorImages.GHOST_COLOR_ORANGE, true),
	PINK(GhostsColorImages.GHOST_COLOR_PINK, true), RED(GhostsColorImages.GHOST_COLOR_RED, true);

	@Getter
	private GhostsColorImages	ghostColorBody;
	@Getter
	@Setter // ici je triche 
	private boolean				isComputed;

	private GhostsBodyImages(GhostsColorImages colorImage, boolean isComputed) {
		this.ghostColorBody	= colorImage;
		this.isComputed		= isComputed;
	}

	public Image getImage() {
		return ghostColorBody.getImage();
	}
}
