package com.kycox.game.constant.level;

import com.kycox.game.constant.FruitsImages;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public enum LevelImagesRedLines {
	NO_RIGHT_LEFT_UP_DOWN("no_right_left_up_down.png"), RIGHT_NO_LEFT_UP_DOWN("right_no_left_up_down.png"),
	RIGHT_LEFT_UP_DOWN("right_left_up_down.png"), RIGHT_LEFT_UP_NO_DOWN("right_left_up_no_down.png"),

	RIGHT_LEFT_NO_UP_DOWN("right_left_no_up_down.png"), RIGHT_LEFT_NO_UP_NO_DOWN("right_left_no_up_no_down.png"),

	NO_RIGHT_NO_LEFT_UP_DOWN("no_right_no_left_up_down.png"), RIGHT_NO_LEFT_UP_NO_DOWN("right_no_left_up_no_down.png"),
	NO_RIGHT_LEFT_UP_NO_DOWN("no_right_left_up_no_down.png"), NO_RIGHT_LEFT_NO_UP_DOWN("no_right_left_no_up_down.png"),

	RIGHT_NO_LEFT_NO_UP_DOWN("right_no_left_no_up_down.png"), NO_RIGHT_NO_LEFT_UP_NO_DOWN("no_right_no_left_up_no_down.png"),

	NO_RIGHT_NO_LEFT_NO_UP_DOWN("no_right_no_left_no_up_down.png"), RIGHT_NO_LEFT_NO_UP_NO_DOWN("right_no_left_no_up_no_down.png"),

	NO_RIGHT_LEFT_NO_UP_NO_DOWN("no_right_left_no_up_no_down.png"), NO_RIGHT_NO_LEFT_NO_UP_NO_DOWN("no_right_no_left_no_up_no_down.png"),

	GOLD("gold.png"), MEGA_GOLD("megaGold.png");

	@Getter
	private Image image;

	LevelImagesRedLines(String fileName) {
		var pathName = "images/level/redLines/" + fileName;
		image = new ImageIcon(FruitsImages.class.getClassLoader().getResource(pathName)).getImage();
	}
}
