package com.kycox.game.constant.pictures;

import java.awt.Image;
import java.awt.Point;

import com.kycox.game.constant.Constants;
import com.kycox.game.constant.ghost.image.GhostHeadbandImages;

public class PirateHeadband {
	public static Image getPirateHeadband(Point viewDirection) {
		if (Constants.POINT_LEFT.equals(viewDirection))
			return GhostHeadbandImages.HEADBAND_LEFT.getImage();
		if (Constants.POINT_RIGHT.equals(viewDirection))
			return GhostHeadbandImages.HEADBAND_RIGHT.getImage();
		if (Constants.POINT_DOWN.equals(viewDirection))
			return GhostHeadbandImages.HEADBAND_DOWN.getImage();
		return GhostHeadbandImages.HEADBAND_UP.getImage();
	}

	private PirateHeadband() {
	}
}
