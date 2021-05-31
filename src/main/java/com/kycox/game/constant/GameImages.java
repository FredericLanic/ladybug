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
package com.kycox.game.constant;

import java.awt.Image;

import javax.swing.ImageIcon;

import lombok.Getter;

/**
 * Enumération des images à afficher dans le jeu
 *
 * @author kycox
 *
 */
public enum GameImages {
	EMPTY("images/empty.png"),
    // ladybug plugins
	LADYBUG_PLUGIN_OX_UP("images/ladybug/skin/OXiane/OXup.gif"),
    // teleportation
	TELEPORTATION("images/Teleportation.png");

	@Getter
	private Image image;

	private GameImages(String fileName) {
		image = new ImageIcon(GameImages.class.getClassLoader().getResource(fileName)).getImage();
	}
}
