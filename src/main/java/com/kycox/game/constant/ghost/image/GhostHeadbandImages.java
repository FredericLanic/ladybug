package com.kycox.game.constant.ghost.image;

import java.awt.Image;

import javax.swing.ImageIcon;

import lombok.Getter;

public enum GhostHeadbandImages {
	HEADBAND_DOWN("images/ghosts/headband/HeadbandDown.png"),
	HEADBAND_LEFT("images/ghosts/headband/HeadbandLeft.png"),
	HEADBAND_RIGHT("images/ghosts/headband/HeadbandRight.png"),
	HEADBAND_UP("images/ghosts/headband/HeadbandUp.png");

	@Getter
	private Image image;

	private GhostHeadbandImages(String fileName) {
		image = new ImageIcon(GhostHeadbandImages.class.getClassLoader().getResource(fileName)).getImage();
	}
}
