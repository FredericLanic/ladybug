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
package com.kycox.game.constant.ladybug;

import com.kycox.game.constant.GameImages;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public enum LadybugImages {
    LADYBUG_UP_1("up1.png"), LADYBUG_UP_2("up2.png"), LADYBUG_UP_3("up3.png"), LADYBUG_UP_4("up4.png"),
	LADYBUG_UP_5("up5.png"), LADYBUG_UP_6("up6.png"), LADYBUG_UP_7("up7.png"), LADYBUG_UP_8("up8.png"),
	LADYBUG_UP_9("up9.png"), LADYBUG_UP_FULL("upFull.png");

	@Getter
	private Image image;

	LadybugImages(String fileName) {
		var pathName = "images/ladybug/color/blue/" + fileName;
		image = new ImageIcon(GameImages.class.getClassLoader().getResource(pathName)).getImage();
	}
}
