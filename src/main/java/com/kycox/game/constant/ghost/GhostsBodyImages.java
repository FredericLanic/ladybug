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
package com.kycox.game.constant.ghost;

import java.awt.Image;

import com.kycox.game.body.ghost.GhostColor;

import lombok.Getter;

@Getter
public enum GhostsBodyImages {
	CYAN(GhostsColorImages.GHOST_CYAN_COLOR), ORANGE(GhostsColorImages.GHOST_ORANGE_COLOR),
	PINK(GhostsColorImages.GHOST_PINK_COLOR), RED(GhostsColorImages.GHOST_RED_COLOR);

	private final GhostsColorImages ghostColorBody;

	GhostsBodyImages(GhostsColorImages colorImage) {
		this.ghostColorBody = colorImage;
	}

	/**
	 * Image du corps pour une couleur du modèle (étape 4.2). Le drapeau « joué par
	 * un humain », rangé ici jusqu'alors (« ici je triche »), est maintenant porté
	 * par le fantôme lui-même.
	 */
	public static GhostsBodyImages of(GhostColor color) {
		return valueOf(color.name());
	}

	public Image getImage() {
		return ghostColorBody.getImage();
	}
}
