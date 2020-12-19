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
package com.kycox.game.constant.pictures;

import java.awt.Image;

import javax.swing.ImageIcon;

import lombok.Getter;

/**
 * Enumération des images à afficher dans le jeu
 *
 * @author kycox
 *
 */
public enum PicturesEnum {
	EMPTY("images/empty.png"),
    // ghost color
	GHOST_COLOR_BLUE("images/ghosts/color/blue.png"), GHOST_COLOR_GREY("images/ghosts/color/grey.png"),
	GHOST_COLOR_ORANGE("images/ghosts/color/orange.png"), GHOST_COLOR_PINK("images/ghosts/color/pink.png"),
	GHOST_COLOR_RED("images/ghosts/color/red.png"),
    // eyes ghost
	GHOST_EYE_DOWN("images/ghosts/eyes/DownEyes.png"), GHOST_EYE_LEFT("images/ghosts/eyes/LeftEyes.png"),
	GHOST_EYE_RIGHT("images/ghosts/eyes/RightEyes.png"), GHOST_EYE_UP("images/ghosts/eyes/UpEyes.png"),
    // ghost plugin headband
	GHOST_HEADBAND_DOWN("images/ghosts/headband/HeadbandDown.png"),
	GHOST_HEADBAND_LEFT("images/ghosts/headband/HeadbandLeft.png"),
	GHOST_HEADBAND_RIGHT("images/ghosts/headband/HeadbandRight.png"),
	GHOST_HEADBAND_UP("images/ghosts/headband/HeadbandUp.png"),
    // ladybug plugins
	LADYBUG_PLUGIN_OX_UP("images/ladybug/skin/OXiane/OXup.gif"),
    // ladybug
	LADYBUG_UP_1("images/ladybug/color/blue/up1.png"), LADYBUG_UP_2("images/ladybug/color/blue/up2.png"),
	LADYBUG_UP_3("images/ladybug/color/blue/up3.png"), LADYBUG_UP_4("images/ladybug/color/blue/up4.png"),
	LADYBUG_UP_5("images/ladybug/color/blue/up5.png"), LADYBUG_UP_6("images/ladybug/color/blue/up6.png"),
	LADYBUG_UP_7("images/ladybug/color/blue/up7.png"), LADYBUG_UP_8("images/ladybug/color/blue/up8.png"),
	LADYBUG_UP_9("images/ladybug/color/blue/up9.png"), LADYBUG_UP_FULL("images/ladybug/color/blue/upFull.png"),
    // teleportation
	TELEPORTATION("images/Teleportation.png");

	@Getter
	private Image image = null;

	// constructor other than ghost
	private PicturesEnum(String fileName) {
		image = new ImageIcon(PicturesEnum.class.getClassLoader().getResource(fileName)).getImage();
	}
}
