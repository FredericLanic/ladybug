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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.tools.ImageUtils;

/**
 * Enumération des images à afficher dans le jeu
 *
 * @author kycox
 *
 */
public enum PicturesEnum {
	// blue ghost
	GHOST_BLUE_DOWN_EYES("GhostBlueDown.png", Constants.POINT_DOWN),
	GHOST_BLUE_LEFT_EYES("GhostBlueLeft.png", Constants.POINT_LEFT),
	GHOST_BLUE_RIGHT_EYES("GhostBlueRight.png", Constants.POINT_RIGHT),
	GHOST_BLUE_UP_EYES("GhostBlueUp.png", Constants.POINT_UP),
	// orange ghost
	GHOST_ORANGE_DOWN_EYES("GhostOrangeDown.png", Constants.POINT_DOWN),
	GHOST_ORANGE_LEFT_EYES("GhostOrangeLeft.png", Constants.POINT_LEFT), 
	GHOST_ORANGE_RIGHT_EYES("GhostOrangeRight.png", Constants.POINT_RIGHT),
	GHOST_ORANGE_UP_EYES("GhostOrangeUp.png", Constants.POINT_UP),
	// pink ghost
	GHOST_PINK_DOWN_EYES("GhostPinkDown.png", Constants.POINT_DOWN),
	GHOST_PINK_LEFT_EYES("GhostPinkLeft.png", Constants.POINT_LEFT), 
	GHOST_PINK_RIGHT_EYES("GhostPinkRight.png", Constants.POINT_RIGHT),
	GHOST_PINK_UP_EYES("GhostPinkUp.png", Constants.POINT_UP),
	// red ghost
	GHOST_RED_DOWN_EYES("GhostRedDown.png", Constants.POINT_DOWN),
	GHOST_RED_LEFT_EYES("GhostRedLeft.png", Constants.POINT_LEFT), 
	GHOST_RED_RIGHT_EYES("GhostRedRight.png", Constants.POINT_RIGHT),
	GHOST_RED_UP_EYES("GhostRedUp.png", Constants.POINT_UP),
	// scared ghost
	GHOST_SCARED_LEFT_EYES("GhostScared1.png"),
	GHOST_SCARED_RIGHT_EYES("GhostScared2.png"),
	// ladybug
	LADYBUG_DOWN_1("skin/ladybug-oxiane/Ladybug1down.gif"), 
	LADYBUG_DOWN_2("skin/ladybug-oxiane/Ladybug2down.gif"),
	LADYBUG_DOWN_3("skin/ladybug-oxiane/Ladybug3down.gif"), 
	LADYBUG_DOWN_4("skin/ladybug-oxiane/Ladybug4down.gif"), 
	LADYBUG_LEFT_1("skin/ladybug-oxiane/Ladybug1left.gif"),
	LADYBUG_LEFT_2("skin/ladybug-oxiane/Ladybug2left.gif"), 
	LADYBUG_LEFT_3("skin/ladybug-oxiane/Ladybug3left.gif"), 
	LADYBUG_LEFT_4("skin/ladybug-oxiane/Ladybug4left.gif"),
	LADYBUG_RIGHT_1("skin/ladybug-oxiane/Ladybug1right.gif"), 
	LADYBUG_RIGHT_2("skin/ladybug-oxiane/Ladybug2right.gif"), 
	LADYBUG_RIGHT_3("skin/ladybug-oxiane/Ladybug3right.gif"),
	LADYBUG_RIGHT_4("skin/ladybug-oxiane/Ladybug4right.gif"), 
	LADYBUG_UP_1("skin/ladybug-oxiane/Ladybug1up.gif"), 
	LADYBUG_UP_10("skin/ladybug-oxiane/Ladybug10up.gif"),
	LADYBUG_UP_11("skin/ladybug-oxiane/Ladybug11up.gif"), 
	LADYBUG_UP_2("skin/ladybug-oxiane/Ladybug2up.gif"), 
	LADYBUG_UP_3("skin/ladybug-oxiane/Ladybug3up.gif"),
	LADYBUG_UP_4("skin/ladybug-oxiane/Ladybug4up.gif"), 
	LADYBUG_UP_5("skin/ladybug-oxiane/Ladybug5up.gif"), 
	LADYBUG_UP_6("skin/ladybug-oxiane/Ladybug6up.gif"),
	LADYBUG_UP_7("skin/ladybug-oxiane/Ladybug7up.gif"), 
	LADYBUG_UP_8("skin/ladybug-oxiane/Ladybug8up.gif"), 
	LADYBUG_UP_9("skin/ladybug-oxiane/Ladybug9up.gif"),
	// dying ghost
	ONLY_EYES_LEFT_EYES("Eyes2.gif"), 
	ONLY_EYES_RIGHT_EYES("Eyes1.gif"),
	// teleportation
	TELEPORTATION("Teleportation.png");

	private Image image = null;
	
	// constructor other than ghost 
	private PicturesEnum(String fileName) {
		this(fileName, Constants.POINT_ZERO);
	}
	
	// constructor only for ghost
	private PicturesEnum(String fileName, Point viewDirection) {
		image = new ImageIcon(PicturesEnum.class.getClassLoader().getResource("images/" + fileName)).getImage();
	
		if (!viewDirection.equals(Constants.POINT_ZERO))			
			image = ImageUtils.appendImages(image, PirateOneEye.getPirateOneEye(viewDirection));
	}	
	
	public Image getImg() {
		return image;
	}
}
