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
package com.kycox.game.constant.ghost.image;

import java.awt.Image;

import javax.swing.ImageIcon;

import lombok.Getter;

// Is a subpart of GhostsBodyImage; please, don't use it in your program
public enum GhostsColorImages {
	GHOST_COLOR_BLUE("blue.png"), GHOST_COLOR_GREY("grey.png"),
	GHOST_COLOR_ORANGE("orange.png"), GHOST_COLOR_PINK("pink.png"),
	GHOST_COLOR_RED("red.png");

	@Getter
	private Image image;

	private GhostsColorImages(String fileName) {
		String pathName = "images/ghosts/color/" + fileName;
		
		image = new ImageIcon(GhostsColorImages.class.getClassLoader().getResource(pathName)).getImage();
	}
}
