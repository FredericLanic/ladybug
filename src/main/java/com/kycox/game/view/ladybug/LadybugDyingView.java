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

import java.awt.Image;
import java.awt.Point;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.kycox.game.body.ladybug.LadybugDying;
import com.kycox.game.constant.Constants;
import com.kycox.game.constant.GameImages;
import com.kycox.game.constant.ladybug.LadybugImages;
import com.kycox.game.view.body.BodyImg;

/**
 * Cinématique pour afficher / entendre lors de la mort de Ladybug
 *
 */
@Named("LadybugDeathView")
public class LadybugDyingView extends LadybugCommun {
	private int			 deltaBips;
	private BodyImg		 empty		 = new BodyImg(GameImages.EMPTY.getImage());
	@Inject
	private LadybugDying ladybugDying;
	private BodyImg		 ladybugFull = new BodyImg(LadybugImages.LADYBUG_UP_FULL.getImage());
	private BodyImg		 ladybugUp1	 = new BodyImg(LadybugImages.LADYBUG_UP_1.getImage());
	private BodyImg		 ladybugUp2	 = new BodyImg(LadybugImages.LADYBUG_UP_2.getImage());
	private BodyImg		 ladybugUp3	 = new BodyImg(LadybugImages.LADYBUG_UP_3.getImage());
	private BodyImg		 ladybugUp4	 = new BodyImg(LadybugImages.LADYBUG_UP_4.getImage());
	private BodyImg		 ladybugUp5	 = new BodyImg(LadybugImages.LADYBUG_UP_5.getImage());
	private BodyImg		 ladybugUp6	 = new BodyImg(LadybugImages.LADYBUG_UP_6.getImage());
	private BodyImg		 ladybugUp7	 = new BodyImg(LadybugImages.LADYBUG_UP_7.getImage());
	private BodyImg		 ladybugUp8	 = new BodyImg(LadybugImages.LADYBUG_UP_8.getImage());
	private BodyImg		 ladybugUp9	 = new BodyImg(LadybugImages.LADYBUG_UP_9.getImage());
	private int			 nbrImages	 = 19;

	@Override
	public Image getImage(Point direction) {
		Image displayImage = super.getImage(direction);
		if (mustNextImage()) {
			deltaBips = 0;
			setNextImage();
		}
		return displayImage;
	}

	@PostConstruct
	public void init() {
		setBodyUpCurrent(ladybugFull);
		deltaBips = 0;
	}

	public void inProgress() {
		deltaBips++;
	}

	public boolean isEnd() {
		return ladybugDying.isEnd();
	}

	public boolean isInProgress() {
		return ladybugDying.isInPogress();
	}

	@PostConstruct
	private void initUpImages() {
		addConvertPointToDegrees(Constants.POINT_UP, 0);
		ladybugFull.setNext(ladybugUp1);
		ladybugUp1.setNext(ladybugUp2);
		ladybugUp2.setNext(ladybugUp3);
		ladybugUp3.setNext(ladybugUp4);
		ladybugUp4.setNext(ladybugUp5);
		ladybugUp5.setNext(ladybugUp6);
		ladybugUp6.setNext(ladybugUp7);
		ladybugUp7.setNext(ladybugUp8);
		ladybugUp8.setNext(ladybugUp9);
		ladybugUp9.setNext(empty);
		empty.setNext(empty);
	}

	private boolean mustNextImage() {
		long nbrBips		= ladybugDying.getMillisecondLenght() / Constants.PACE;
		long nbrBitPerImage	= nbrBips / nbrImages;
		if (deltaBips > nbrBitPerImage) {
			return true;
		}
		return false;
	}
}
