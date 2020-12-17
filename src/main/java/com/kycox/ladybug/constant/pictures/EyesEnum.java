package com.kycox.ladybug.constant.pictures;

import java.awt.Image;

import javax.swing.ImageIcon;

import lombok.Getter;

public enum EyesEnum {
	GHOST_DOWN_EYES("ghosts/eyes/DownEyes.png"), GHOST_LEFT_EYES("ghosts/eyes/LeftEyes.png"),
	GHOST_RIGHT_EYES("ghosts/eyes/RightEyes.png"), GHOST_UP_EYES("ghosts/eyes/UpEyes.png");

	@Getter
	private Image image = null;

	// constructor other than ghost
	private EyesEnum(String fileName) {
		image = new ImageIcon(EyesEnum.class.getClassLoader().getResource("images/" + fileName)).getImage();
	}
}
