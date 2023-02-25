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

import javax.swing.ImageIcon;

import lombok.Getter;

/**
 * Is a subpart of GhostsBodyImage; please, don't use it in your program directly
  */
public enum GhostsColorImages {
	GHOST_CYAN_COLOR("cyan.png"), GHOST_GREY_COLOR("grey.png"), GHOST_ORANGE_COLOR("orange.png"),
	GHOST_PINK_COLOR("pink.png"), GHOST_RED_COLOR("red.png"), GHOST_CAMOUFLAGE_COLOR("camouflage.png"), GHOST_GRAY_CAMOUFLAGE_COLOR("greyCamouflage.png");

	@Getter
	private final Image image;

	GhostsColorImages(String fileName) {
		var pathName = "images/ghosts/color/" + fileName;
		image = new ImageIcon(GhostsColorImages.class.getClassLoader().getResource(pathName)).getImage();
	}
}
