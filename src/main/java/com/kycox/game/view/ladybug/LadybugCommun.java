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
package com.kycox.game.view.ladybug;

import com.kycox.game.constant.Constants;
import com.kycox.game.constant.GameImages;
import com.kycox.game.constant.ladybug.LadybugImages;
import com.kycox.game.properties.GameProperties;
import com.kycox.game.tools.ImageUtils;
import com.kycox.game.view.body.BodyImg;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public abstract class LadybugCommun {
	@Getter
	@Setter
	private BodyImg bodyUpCurrent;
	private final Map<Point, Integer> convertPointToDegrees = new HashMap<>();

	@Autowired
	private GameProperties gameProperties;

	protected void addConvertPointToDegrees(Point point, Integer integer) {
		convertPointToDegrees.put(point, integer);
	}

	private Image addSkin(Image image, Point direction) {
		if (gameProperties.hasLadybugSkin()) {
			var imagePluginOX = ImageUtils.rotateImage(GameImages.LADYBUG_PLUGIN_OX_UP.getImage(),
			        convertPointToDegrees.get(direction));
			return ImageUtils.appendImages(image, imagePluginOX);
		}
		return image;
	}

	public Image getImage(Point direction) {
		var ladybugImage = ImageUtils.rotateImage(bodyUpCurrent.getImage(), convertPointToDegrees.get(direction));
		return addSkin(ladybugImage, direction);
	}

	public Image getStaticView() {
		var direction = Constants.POINT_RIGHT;
		var ladybugImage = ImageUtils.rotateImage(LadybugImages.LADYBUG_UP_2.getImage(),
		        convertPointToDegrees.get(direction));
		return addSkin(ladybugImage, direction);
	}

	@PostConstruct
	private void initConvertPointToDegrees() {
		convertPointToDegrees.put(Constants.POINT_UP, 0);
		convertPointToDegrees.put(Constants.POINT_ZERO, 0);
		convertPointToDegrees.put(Constants.POINT_LEFT, 90);
		convertPointToDegrees.put(Constants.POINT_RIGHT, -90);
		convertPointToDegrees.put(Constants.POINT_DOWN, 180);
	}

	protected void setNextImage() {
		bodyUpCurrent = bodyUpCurrent.getNext();
	}
}
