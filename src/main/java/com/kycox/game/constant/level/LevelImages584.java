package com.kycox.game.constant.level;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

@Getter
public enum LevelImages584 {
	NO_RIGHT_LEFT_UP_DOWN("no_right_left_up_down.png"), RIGHT_NO_LEFT_UP_DOWN("right_no_left_up_down.png"),
	RIGHT_LEFT_UP_DOWN("right_left_up_down.png"), RIGHT_LEFT_UP_NO_DOWN("right_left_up_no_down.png"),

	RIGHT_LEFT_NO_UP_DOWN("right_left_no_up_down.png"), RIGHT_LEFT_NO_UP_NO_DOWN("right_left_no_up_no_down.png"),

	NO_RIGHT_NO_LEFT_UP_DOWN("no_right_no_left_up_down.png"), RIGHT_NO_LEFT_UP_NO_DOWN("right_no_left_up_no_down.png"),
	NO_RIGHT_LEFT_UP_NO_DOWN("no_right_left_up_no_down.png"), NO_RIGHT_LEFT_NO_UP_DOWN("no_right_left_no_up_down.png"),

	RIGHT_NO_LEFT_NO_UP_DOWN("right_no_left_no_up_down.png"), NO_RIGHT_NO_LEFT_UP_NO_DOWN("no_right_no_left_up_no_down.png"),

	NO_RIGHT_NO_LEFT_NO_UP_DOWN("no_right_no_left_no_up_down.png"), RIGHT_NO_LEFT_NO_UP_NO_DOWN("right_no_left_no_up_no_down.png"),

	NO_RIGHT_LEFT_NO_UP_NO_DOWN("no_right_left_no_up_no_down.png"), NO_RIGHT_NO_LEFT_NO_UP_NO_DOWN("no_right_no_left_no_up_no_down.png"),

	GOLD("gold.png"), MEGA_GOLD("megaGold.png");

	private final Image image;
	private final ClassLoader classLoader = getClass().getClassLoader();

	LevelImages584(String fileName) {
		var pathName = "images/level/584/" + fileName;
		image = new ImageIcon(Objects.requireNonNull(classLoader.getResource(pathName))).getImage();
	}
}
