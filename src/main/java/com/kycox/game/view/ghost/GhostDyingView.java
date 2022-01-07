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
package com.kycox.game.view.ghost;

import java.awt.Image;

import javax.swing.Timer;

import com.kycox.game.constant.ghost.image.GhostEyesImages;
import com.kycox.game.timer.TimerView;
import com.kycox.game.view.body.BodyImg;

public class GhostDyingView implements TimerView {
	private static GhostDyingView ghostEatenlView = new GhostDyingView();

	public static GhostDyingView getInstance() {
		return ghostEatenlView;
	}

	private BodyImg currentImg;
	private BodyImg downEyes = new BodyImg(GhostEyesImages.GHOST_DOWN_EYES.getImage());
	private BodyImg leftEyes = new BodyImg(GhostEyesImages.GHOST_LEFT_EYES.getImage());
	private BodyImg rightEyes = new BodyImg(GhostEyesImages.GHOST_RIGHT_EYES.getImage());
	private Timer timer;
	private BodyImg upEyes = new BodyImg(GhostEyesImages.GHOST_UP_EYES.getImage());

	/**
	 * Constructeur privé pour assurer le singleton
	 */
	private GhostDyingView() {
		currentImg = leftEyes;
		leftEyes.setNext(upEyes);
		upEyes.setNext(rightEyes);
		rightEyes.setNext(downEyes);
		downEyes.setNext(leftEyes);
		timer = createTimer();
		timer.start();
	}

	@Override
	public void doAction() {
		currentImg = currentImg.getNext();
	}

	public Image getImage() {
		return currentImg.getImage();
	}
}
