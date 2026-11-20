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
package com.kycox.game.engine.view.ghost;

import java.awt.Image;

import javax.swing.Timer;

import com.kycox.game.constant.ghost.GhostEyesImages;
import com.kycox.game.tools.timer.TimerView;
import com.kycox.game.engine.view.body.BodyImg;

public class GhostDyingView implements TimerView {
	private static final GhostDyingView ghostEatenlView = new GhostDyingView();

	public static GhostDyingView getInstance() {
		return ghostEatenlView;
	}

	private BodyImg currentImg;
    private Timer timer;

    private GhostDyingView() {
        BodyImg leftEyes = new BodyImg(GhostEyesImages.GHOST_LEFT_EYES.getImage());
        currentImg = leftEyes;
        BodyImg upEyes = new BodyImg(GhostEyesImages.GHOST_UP_EYES.getImage());
        leftEyes.setNext(upEyes);
        BodyImg rightEyes = new BodyImg(GhostEyesImages.GHOST_RIGHT_EYES.getImage());
        upEyes.setNext(rightEyes);
        BodyImg downEyes = new BodyImg(GhostEyesImages.GHOST_DOWN_EYES.getImage());
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
