package com.kycox.game.constant.ladybug;

import java.awt.Image;

import javax.swing.ImageIcon;

import com.kycox.game.constant.GameImages;

import lombok.Getter;

public enum LadybugImages {
    // ladybug
	LADYBUG_UP_1("images/ladybug/color/blue/up1.png"), LADYBUG_UP_2("images/ladybug/color/blue/up2.png"),
	LADYBUG_UP_3("images/ladybug/color/blue/up3.png"), LADYBUG_UP_4("images/ladybug/color/blue/up4.png"),
	LADYBUG_UP_5("images/ladybug/color/blue/up5.png"), LADYBUG_UP_6("images/ladybug/color/blue/up6.png"),
	LADYBUG_UP_7("images/ladybug/color/blue/up7.png"), LADYBUG_UP_8("images/ladybug/color/blue/up8.png"),
	LADYBUG_UP_9("images/ladybug/color/blue/up9.png"), LADYBUG_UP_FULL("images/ladybug/color/blue/upFull.png");

	@Getter
	private Image image = null;

	private LadybugImages(String fileName) {
		image = new ImageIcon(GameImages.class.getClassLoader().getResource(fileName)).getImage();
	}
}
