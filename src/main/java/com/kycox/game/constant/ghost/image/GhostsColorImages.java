package com.kycox.game.constant.ghost.image;

import java.awt.Image;

import javax.swing.ImageIcon;

import lombok.Getter;

// Is a subpart of GhostsBodyImage; please, don't use it in your program
public enum GhostsColorImages {
	GHOST_COLOR_BLUE("blue.png"), GHOST_COLOR_GREY("grey.png"),
	GHOST_COLOR_ORANGE("orange.png"), GHOST_COLOR_PINK("pink.png"),
	GHOST_COLOR_RED("red.png");

	@Getter
	private Image image;

	private GhostsColorImages(String fileName) {
		String pathName = "images/ghosts/color/" + fileName;
		
		image = new ImageIcon(GhostsColorImages.class.getClassLoader().getResource(pathName)).getImage();
	}
}
