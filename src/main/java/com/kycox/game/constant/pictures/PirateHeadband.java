package com.kycox.game.constant.pictures;

import java.awt.Image;
import java.awt.Point;

import com.kycox.game.constant.Constants;

public class PirateHeadband {
	public static Image getPirateHeadband(Point viewDirection) {
		if (Constants.POINT_LEFT.equals(viewDirection))
			return PicturesEnum.GHOST_HEADBAND_LEFT.getImage();
		if (Constants.POINT_RIGHT.equals(viewDirection))
			return PicturesEnum.GHOST_HEADBAND_RIGHT.getImage();
		if (Constants.POINT_DOWN.equals(viewDirection))
			return PicturesEnum.GHOST_HEADBAND_DOWN.getImage();
		return PicturesEnum.GHOST_HEADBAND_UP.getImage();
	}

	private PirateHeadband() {
	}
}
