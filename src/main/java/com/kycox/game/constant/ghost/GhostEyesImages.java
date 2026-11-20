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

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

@Getter
public enum GhostEyesImages {
	GHOST_DOWN_EYES("DownEyes.png"), GHOST_LEFT_EYES("LeftEyes.png"), GHOST_RIGHT_EYES("RightEyes.png"),
	GHOST_UP_EYES("UpEyes.png");

	private final Image image;
	private final ClassLoader classLoader = getClass().getClassLoader();

	GhostEyesImages(String fileName) {
		var pathName = "images/ghosts/eyes/" + fileName;
		image = new ImageIcon(Objects.requireNonNull(classLoader.getResource(pathName))).getImage();
	}
}
