package com.kycox.game.constant.ghost.image;

import java.awt.Image;

import javax.swing.ImageIcon;

import lombok.Getter;

public enum GhostPluginsImages {
	HEADBAND_DOWN("images/ghosts/headband/HeadbandDown.png"),
	HEADBAND_LEFT("images/ghosts/headband/HeadbandLeft.png"),
	HEADBAND_RIGHT("images/ghosts/headband/HeadbandRight.png"),
	HEADBAND_UP("images/ghosts/headband/HeadbandUp.png");

	@Getter
	private Image image;

	private GhostPluginsImages(String fileName) {
		image = new ImageIcon(GhostPluginsImages.class.getClassLoader().getResource(fileName)).getImage();
	}
}
