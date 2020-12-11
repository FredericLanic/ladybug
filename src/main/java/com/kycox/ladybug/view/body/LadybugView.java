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
package com.kycox.ladybug.view.body;

import java.awt.Image;
import java.awt.Point;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.swing.Timer;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.constant.PicturesEnum;
import com.kycox.ladybug.timer.TimerView;

@Named("LadybugView")
public class LadybugView implements TimerView {
	// Suite de body image
	private BodyImg	bodyDownCurrent;
	private BodyImg	bodyLeftCurrent;
	private BodyImg	bodyRightCurrent;
	private BodyImg	bodyUpCurrent;
	// liste d'images ladybug regarde en bas
	private BodyImg	ladybugDown1 = new BodyImg(PicturesEnum.LADYBUG_DOWN_1.getImg());
	private BodyImg	ladybugDown2 = new BodyImg(PicturesEnum.LADYBUG_DOWN_2.getImg());
	private BodyImg	ladybugDown3 = new BodyImg(PicturesEnum.LADYBUG_DOWN_3.getImg());
	private BodyImg	ladybugDown4 = new BodyImg(PicturesEnum.LADYBUG_DOWN_4.getImg());
	private BodyImg	ladybugDown5 = new BodyImg(PicturesEnum.LADYBUG_DOWN_3.getImg());
	private BodyImg	ladybugDown6 = new BodyImg(PicturesEnum.LADYBUG_DOWN_2.getImg());
	// liste d'images ladybug regarde à gauche
	private BodyImg	ladybugLeft1 = new BodyImg(PicturesEnum.LADYBUG_LEFT_1.getImg());
	private BodyImg	ladybugLeft2 = new BodyImg(PicturesEnum.LADYBUG_LEFT_2.getImg());
	private BodyImg	ladybugLeft3 = new BodyImg(PicturesEnum.LADYBUG_LEFT_3.getImg());
	private BodyImg	ladybugLeft4 = new BodyImg(PicturesEnum.LADYBUG_LEFT_4.getImg());
	private BodyImg	ladybugLeft5 = new BodyImg(PicturesEnum.LADYBUG_LEFT_3.getImg());
	private BodyImg	ladybugLeft6 = new BodyImg(PicturesEnum.LADYBUG_LEFT_2.getImg());
	// liste d'images ladybug regarde à doite
	private BodyImg	ladybugRight1 = new BodyImg(PicturesEnum.LADYBUG_RIGHT_1.getImg());
	private BodyImg	ladybugRight2 = new BodyImg(PicturesEnum.LADYBUG_RIGHT_2.getImg());
	private BodyImg	ladybugRight3 = new BodyImg(PicturesEnum.LADYBUG_RIGHT_3.getImg());
	private BodyImg	ladybugRight4 = new BodyImg(PicturesEnum.LADYBUG_RIGHT_4.getImg());
	private BodyImg	ladybugRight5 = new BodyImg(PicturesEnum.LADYBUG_RIGHT_3.getImg());
	private BodyImg	ladybugRight6 = new BodyImg(PicturesEnum.LADYBUG_RIGHT_2.getImg());
	// liste d'images ladybug regarde en haut
	private BodyImg	ladybugUp1 = new BodyImg(PicturesEnum.LADYBUG_UP_1.getImg());
	private BodyImg	ladybugUp2 = new BodyImg(PicturesEnum.LADYBUG_UP_2.getImg());
	private BodyImg	ladybugUp3 = new BodyImg(PicturesEnum.LADYBUG_UP_3.getImg());
	private BodyImg	ladybugUp4 = new BodyImg(PicturesEnum.LADYBUG_UP_4.getImg());
	private BodyImg	ladybugUp5 = new BodyImg(PicturesEnum.LADYBUG_UP_3.getImg());
	private BodyImg	ladybugUp6 = new BodyImg(PicturesEnum.LADYBUG_UP_2.getImg());
	/** Timer de l'affichage */
	private Timer	timer;
	// View par défaut à la construction
	private Point viewDirectionPoint = Constants.POINT_ZERO;

	/**
	 * Change la vue de ladybug
	 */
	@Override
	public void doAction() {
		if (viewDirectionPoint == Constants.POINT_UP)
			bodyUpCurrent = bodyUpCurrent.getNext();
		if (viewDirectionPoint == Constants.POINT_RIGHT)
			bodyRightCurrent = bodyRightCurrent.getNext();
		if (viewDirectionPoint == Constants.POINT_LEFT)
			bodyLeftCurrent = bodyLeftCurrent.getNext();
		if (viewDirectionPoint == Constants.POINT_DOWN)
			bodyDownCurrent = bodyDownCurrent.getNext();
	}

	/**
	 * Retourne l'image de ladybug
	 *
	 * @param viewDirectionPoint
	 * @return
	 */
	public Image getNextImage(Point viewDirectionPoint) {
		this.viewDirectionPoint = viewDirectionPoint;
		if (viewDirectionPoint == Constants.POINT_UP)
			return bodyUpCurrent.getImg();
		if (viewDirectionPoint == Constants.POINT_RIGHT)
			return bodyRightCurrent.getImg();
		if (viewDirectionPoint == Constants.POINT_LEFT)
			return bodyLeftCurrent.getImg();
		return bodyDownCurrent.getImg();
	}

	@PostConstruct
	private void init() {
		initDownImages();
		initLeftImages();
		initRightImages();
		initUpImages();
		timer = createTimer();
		timer.start();
	}

	private void initDownImages() {
		ladybugDown1.setNext(ladybugDown2);
		ladybugDown2.setNext(ladybugDown3);
		ladybugDown3.setNext(ladybugDown4);
		ladybugDown4.setNext(ladybugDown5);
		ladybugDown5.setNext(ladybugDown6);
		ladybugDown5.setNext(ladybugDown1);
		bodyDownCurrent = ladybugDown1;
	}

	private void initLeftImages() {
		ladybugLeft1.setNext(ladybugLeft2);
		ladybugLeft2.setNext(ladybugLeft3);
		ladybugLeft3.setNext(ladybugLeft4);
		ladybugLeft4.setNext(ladybugLeft5);
		ladybugLeft5.setNext(ladybugLeft6);
		ladybugLeft6.setNext(ladybugLeft1);
		bodyLeftCurrent = ladybugLeft1;
	}

	private void initRightImages() {
		ladybugRight1.setNext(ladybugRight2);
		ladybugRight2.setNext(ladybugRight3);
		ladybugRight3.setNext(ladybugRight4);
		ladybugRight4.setNext(ladybugRight5);
		ladybugRight5.setNext(ladybugRight6);
		ladybugRight6.setNext(ladybugRight1);
		bodyRightCurrent = ladybugRight1;
	}

	private void initUpImages() {
		ladybugUp1.setNext(ladybugUp2);
		ladybugUp2.setNext(ladybugUp3);
		ladybugUp3.setNext(ladybugUp4);
		ladybugUp4.setNext(ladybugUp5);
		ladybugUp5.setNext(ladybugUp6);
		ladybugUp6.setNext(ladybugUp1);
		bodyUpCurrent = ladybugUp1;
	}
}
