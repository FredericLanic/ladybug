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

public enum GhostEyesImages {
	GHOST_DOWN_EYES("DownEyes.png"), GHOST_LEFT_EYES("LeftEyes.png"), GHOST_RIGHT_EYES("RightEyes.png"),
	GHOST_UP_EYES("UpEyes.png");

	@Getter
	private Image image;

	// constructor other than ghost
	private GhostEyesImages(String fileName) {
		String pathName = "images/ghosts/eyes/" + fileName;
		image = new ImageIcon(GhostEyesImages.class.getClassLoader().getResource(pathName)).getImage();
	}
}
