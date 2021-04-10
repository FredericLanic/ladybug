package com.kycox.game.constant.ghost.image;

import java.awt.Image;

import javax.swing.ImageIcon;

import lombok.Getter;

public enum GhostHeadbandImages {
	HEADBAND_DOWN("HeadbandDown.png"),
	HEADBAND_LEFT("HeadbandLeft.png"),
	HEADBAND_RIGHT("HeadbandRight.png"),
	HEADBAND_UP("HeadbandUp.png");

	@Getter
	private Image image;

	private GhostHeadbandImages(String fileName) {
		String pathName = "images/ghosts/headband/" + fileName;
		image = new ImageIcon(GhostHeadbandImages.class.getClassLoader().getResource(pathName)).getImage();
	}
}
