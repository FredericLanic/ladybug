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

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.swing.Timer;

import com.kycox.game.constant.ladybug.LadybugImages;
import com.kycox.game.timer.TimerView;
import com.kycox.game.view.body.BodyImg;

@Named("LadybugView")
public class LadybugView extends LadybugCommun implements TimerView {
	private BodyImg	ladybugFull01 = new BodyImg(LadybugImages.LADYBUG_UP_FULL.getImage());
	private BodyImg	ladybugFull02 = new BodyImg(LadybugImages.LADYBUG_UP_FULL.getImage());
	private BodyImg	ladybugUp1	  = new BodyImg(LadybugImages.LADYBUG_UP_1.getImage());
	private BodyImg	ladybugUp2	  = new BodyImg(LadybugImages.LADYBUG_UP_2.getImage());
	private BodyImg	ladybugUp3	  = new BodyImg(LadybugImages.LADYBUG_UP_1.getImage());
	private Timer	timer;

	@Override
	public void doAction() {
		setNextImage();
	}

	@PostConstruct
	private void init() {
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
		setBodyUpCurrent(ladybugFull01);
	}
}
