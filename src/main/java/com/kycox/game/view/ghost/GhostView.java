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

import com.kycox.game.constant.Constants;
import com.kycox.game.constant.ghost.GhostStatus;
import com.kycox.game.constant.ghost.image.GhostEyesImages;
import com.kycox.game.constant.ghost.image.GhostHatImages;
import com.kycox.game.constant.ghost.image.GhostHeadbandImages;
import com.kycox.game.contract.GhostForView;
import com.kycox.game.properties.GameProperties;
import com.kycox.game.tools.ImageUtils;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class GhostView {
	private final GameProperties gameProperties;

	public GhostView(GameProperties gameProperties) {
		this.gameProperties = gameProperties;
	}

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

	public Image getImage(GhostForView ghostForView) {
		Image ghostImg;
		// ajout le corps
		switch (ghostForView.getStatus()) {
			case FLASH -> ghostImg = GhostFlashView.getInstance().getImage(ghostForView);
			case SCARED -> ghostImg = GhostScaredView.getInstance().getImage(ghostForView);
			case DYING -> ghostImg = GhostDyingView.getInstance().getImage();
			default -> ghostImg = GhostDefautlView.getInstance().getImage(ghostForView);
		}
		// ajout les yeux en fonction de la direction
		if (ghostForView.getStatus() != GhostStatus.DYING) {
			ghostImg = ImageUtils.appendImages(ghostImg, addEyes(ghostForView.getDirection()));
		}
		// ajout du bandeau
		if (ghostForView.getStatus() != GhostStatus.DYING && gameProperties.hasGhostHeadBand()) {
			ghostImg = ImageUtils.appendImages(ghostImg, addHeadband(ghostForView.getDirection()));
		}
		if (gameProperties.hasHatSkin()) {
			ghostImg = ImageUtils.appendImages(ghostImg, GhostHatImages.GHOST_HAT.getImage());
		}
		return ghostImg;
	}
}
