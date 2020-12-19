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
package com.kycox.game.view.ladybug;

import java.awt.Image;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.swing.Timer;

import com.kycox.game.constant.Constants;
import com.kycox.game.constant.pictures.PicturesEnum;
import com.kycox.game.timer.TimerView;
import com.kycox.game.tools.ImageUtils;
import com.kycox.game.view.body.BodyImg;

@Named("LadybugView")
public class LadybugView implements TimerView {
	private BodyImg				bodyUpCurrent;
	private Map<Point, Integer>	convertPointToDegrees = new HashMap<>();
	private BodyImg				ladybugFull01		  = new BodyImg(PicturesEnum.LADYBUG_UP_FULL.getImage());
	private BodyImg				ladybugFull02		  = new BodyImg(PicturesEnum.LADYBUG_UP_FULL.getImage());
	private BodyImg				ladybugUp1			  = new BodyImg(PicturesEnum.LADYBUG_UP_1.getImage());
	private BodyImg				ladybugUp2			  = new BodyImg(PicturesEnum.LADYBUG_UP_2.getImage());
	private BodyImg				ladybugUp3			  = new BodyImg(PicturesEnum.LADYBUG_UP_1.getImage());
	/** Timer de l'affichage */
	private Timer				timer;

	/**
	 * Change la vue de ladybug
	 */
	@Override
	public void doAction() {
		bodyUpCurrent = bodyUpCurrent.getNext();
	}

	public Image getNextImage(Point viewDirectionPoint) {
		// image de base ladybug
		Image ladybugImage = ImageUtils.rotateImage(bodyUpCurrent.getImage(),
		        convertPointToDegrees.get(viewDirectionPoint));
		return addPlugins(ladybugImage, viewDirectionPoint);
	}

	private Image addPlugins(Image image, Point viewDirectionPoint) {
		Image imagePluginOX = ImageUtils.rotateImage(PicturesEnum.LADYBUG_PLUGIN_OX_UP.getImage(),
		        convertPointToDegrees.get(viewDirectionPoint));
		return ImageUtils.appendImages(image, imagePluginOX);
	}

	@PostConstruct
	private void init() {
		convertPointToDegrees.put(Constants.POINT_UP, 0);
		convertPointToDegrees.put(Constants.POINT_ZERO, 0);
		convertPointToDegrees.put(Constants.POINT_LEFT, 90);
		convertPointToDegrees.put(Constants.POINT_RIGHT, -90);
		convertPointToDegrees.put(Constants.POINT_DOWN, 180);
		initUpImages();
		timer = createTimer();
		timer.start();
	}

	private void initUpImages() {
		ladybugFull01.setNext(ladybugUp1);
		ladybugUp1.setNext(ladybugUp2);
		ladybugUp2.setNext(ladybugUp3);
		ladybugUp3.setNext(ladybugFull02);
		ladybugFull02.setNext(ladybugFull01);
		bodyUpCurrent = ladybugFull01;
	}
}
