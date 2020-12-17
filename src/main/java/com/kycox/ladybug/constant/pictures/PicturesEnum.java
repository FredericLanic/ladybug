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
package com.kycox.ladybug.constant.pictures;

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
    // color
	GHOST_COLOR_BLUE("ghosts/color/blue.png"), GHOST_COLOR_GREY("ghosts/color/grey.png"),
	GHOST_COLOR_ORANGE("ghosts/color/orange.png"), GHOST_COLOR_PINK("ghosts/color/pink.png"),
	GHOST_COLOR_RED("ghosts/color/red.png"),
    // eyes ghost
	GHOST_EYE_DOWN("ghosts/eyes/DownEyes.png"), GHOST_EYE_LEFT("ghosts/eyes/LeftEyes.png"),
	GHOST_EYE_RIGHT("ghosts/eyes/RightEyes.png"), GHOST_EYE_UP("ghosts/eyes/UpEyes.png"),
    // ladybug
	LADYBUG_DOWN_1("ladybug/color/blue/Ladybug1down.gif"), LADYBUG_DOWN_2("ladybug/color/blue/Ladybug2down.gif"),
	LADYBUG_DOWN_3("ladybug/color/blue/Ladybug3down.gif"), LADYBUG_DOWN_4("ladybug/color/blue/Ladybug4down.gif"),
	LADYBUG_LEFT_1("ladybug/color/blue/Ladybug1left.gif"), LADYBUG_LEFT_2("ladybug/color/blue/Ladybug2left.gif"),
	LADYBUG_LEFT_3("ladybug/color/blue/Ladybug3left.gif"), LADYBUG_LEFT_4("ladybug/color/blue/Ladybug4left.gif"),
	LADYBUG_RIGHT_1("ladybug/color/blue/Ladybug1right.gif"), LADYBUG_RIGHT_2("ladybug/color/blue/Ladybug2right.gif"),
	LADYBUG_RIGHT_3("ladybug/color/blue/Ladybug3right.gif"), LADYBUG_RIGHT_4("ladybug/color/blue/Ladybug4right.gif"),
	LADYBUG_UP_1("ladybug/color/blue/Ladybug1up.gif"), LADYBUG_UP_10("ladybug/color/blue/Ladybug10up.gif"),
	LADYBUG_UP_11("ladybug/color/blue/Ladybug11up.gif"), LADYBUG_UP_2("ladybug/color/blue/Ladybug2up.gif"),
	LADYBUG_UP_3("ladybug/color/blue/Ladybug3up.gif"), LADYBUG_UP_4("ladybug/color/blue/Ladybug4up.gif"),
	LADYBUG_UP_5("ladybug/color/blue/Ladybug5up.gif"), LADYBUG_UP_6("ladybug/color/blue/Ladybug6up.gif"),
	LADYBUG_UP_7("ladybug/color/blue/Ladybug7up.gif"), LADYBUG_UP_8("ladybug/color/blue/Ladybug8up.gif"),
	LADYBUG_UP_9("ladybug/color/blue/Ladybug9up.gif"),
    // teleportation
	TELEPORTATION("Teleportation.png");

	@Getter
	private Image image = null;

	// constructor other than ghost
	private PicturesEnum(String fileName) {
		image = new ImageIcon(PicturesEnum.class.getClassLoader().getResource("images/" + fileName)).getImage();
	}
}
