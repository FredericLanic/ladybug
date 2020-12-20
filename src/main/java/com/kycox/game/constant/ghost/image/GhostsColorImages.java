package com.kycox.game.constant.ghost.image;

import java.awt.Image;

import javax.swing.ImageIcon;

import lombok.Getter;

// Is a subpart of GhostsBodyImage; please, don't use it in your program
public enum GhostsColorImages {
	GHOST_COLOR_BLUE("images/ghosts/color/blue.png"), GHOST_COLOR_GREY("images/ghosts/color/grey.png"),
	GHOST_COLOR_ORANGE("images/ghosts/color/orange.png"), GHOST_COLOR_PINK("images/ghosts/color/pink.png"),
	GHOST_COLOR_RED("images/ghosts/color/red.png");

	@Getter
	private Image image;

	private GhostsColorImages(String fileName) {
		image = new ImageIcon(GhostsColorImages.class.getClassLoader().getResource(fileName)).getImage();
	}
}
