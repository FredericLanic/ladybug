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
import java.awt.Point;

import javax.inject.Inject;
import javax.inject.Named;

import com.kycox.game.body.ghost.Ghost;
import com.kycox.game.constant.Constants;
import com.kycox.game.constant.ghost.GhostStatus;
import com.kycox.game.constant.ghost.image.GhostEyesImages;
import com.kycox.game.constant.ghost.image.GhostHatImages;
import com.kycox.game.constant.ghost.image.GhostHeadbandImages;
import com.kycox.game.properties.GameProperties;
import com.kycox.game.tools.ImageUtils;

@Named("GhostView")
public class GhostView {
	@Inject
	private GameProperties gameProperties;

	private Image addEyes(Point direction) {
		if (direction.equals(Constants.POINT_LEFT)) {
			return GhostEyesImages.GHOST_LEFT_EYES.getImage();
		}
		if (direction.equals(Constants.POINT_DOWN)) {
			return GhostEyesImages.GHOST_DOWN_EYES.getImage();
		}
		if (direction.equals(Constants.POINT_UP)) {
			return GhostEyesImages.GHOST_UP_EYES.getImage();
		}
		return GhostEyesImages.GHOST_RIGHT_EYES.getImage();
	}

	private Image addHeadband(Point direction) {
		if (direction.equals(Constants.POINT_LEFT)) {
			return GhostHeadbandImages.HEADBAND_LEFT.getImage();
		}
		if (direction.equals(Constants.POINT_DOWN)) {
			return GhostHeadbandImages.HEADBAND_DOWN.getImage();
		}
		if (direction.equals(Constants.POINT_UP)) {
			return GhostHeadbandImages.HEADBAND_UP.getImage();
		}
		return GhostHeadbandImages.HEADBAND_RIGHT.getImage();
	}

	public Image getImage(Ghost ghost) {
		Image ghostImg;
		// ajout le corps
		switch (ghost.getStatus()) {
			case FLASH -> ghostImg = GhostFlashView.getInstance().getImage(ghost);
			case SCARED -> ghostImg = GhostScaredView.getInstance().getImage(ghost);
			case DYING -> ghostImg = GhostDyingView.getInstance().getImage();
			default -> ghostImg = GhostDefautlView.getInstance().getImage(ghost);
		}
		// ajout les yeux en fonction de la direction
		if (ghost.getStatus() != GhostStatus.DYING) {
			ghostImg = ImageUtils.appendImages(ghostImg, addEyes(ghost.getDirection()));
		}
		// ajout du bandeau
		if (ghost.getStatus() != GhostStatus.DYING && gameProperties.hasGhostHeadBand()) {
			ghostImg = ImageUtils.appendImages(ghostImg, addHeadband(ghost.getDirection()));
		}
		if (gameProperties.hasHatSkin()) {
			ghostImg = ImageUtils.appendImages(ghostImg, GhostHatImages.GHOST_HAT.getImage());
		}
		return ghostImg;
	}
}
