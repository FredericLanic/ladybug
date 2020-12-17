package com.kycox.ladybug.constant.pictures;

import java.awt.Image;
import java.awt.Point;

import javax.swing.ImageIcon;

import com.kycox.ladybug.constant.Constants;

public class PirateOneEye {
	
	private static final Image pirateOneEyeDown = new ImageIcon(PicturesEnum.class.getClassLoader().getResource("images/add/PirateOneEye/PirateOneEyeDown.png")).getImage();
	private static final Image pirateOneEyeUp = new ImageIcon(PicturesEnum.class.getClassLoader().getResource("images/add/PirateOneEye/PirateOneEyeUp.png")).getImage();
	private static final Image pirateOneEyeLeft = new ImageIcon(PicturesEnum.class.getClassLoader().getResource("images/add/PirateOneEye/PirateOneEyeLeft.png")).getImage();
	private static final Image pirateOneEyeRight = new ImageIcon(PicturesEnum.class.getClassLoader().getResource("images/add/PirateOneEye/PirateOneEyeRight.png")).getImage();
	
	public static Image getPirateOneEye(Point viewDirection) {
		if (Constants.POINT_LEFT.equals(viewDirection))
			return pirateOneEyeLeft;
		if (Constants.POINT_RIGHT.equals(viewDirection))
			return pirateOneEyeRight;
		if (Constants.POINT_DOWN.equals(viewDirection))
			return pirateOneEyeDown;
		return pirateOneEyeUp;
	}
}
