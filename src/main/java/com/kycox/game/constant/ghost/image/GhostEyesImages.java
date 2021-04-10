package com.kycox.game.constant.ghost.image;

import java.awt.Image;

import javax.swing.ImageIcon;

import lombok.Getter;

public enum GhostEyesImages {
	GHOST_DOWN_EYES("DownEyes.png"), GHOST_LEFT_EYES("LeftEyes.png"),
	GHOST_RIGHT_EYES("RightEyes.png"), GHOST_UP_EYES("UpEyes.png");

	@Getter
	private Image image = null;

	// constructor other than ghost
	private GhostEyesImages(String fileName) {
		String pathName = "images/ghosts/eyes/" + fileName;
		image = new ImageIcon(GhostEyesImages.class.getClassLoader().getResource(pathName)).getImage();
	}
}
