package com.kycox.game.constant.fruit.image;

import java.awt.Image;

import javax.swing.ImageIcon;

import lombok.Getter;

public enum FruitsImages {
	CHERRY("cherry.png"),
	HEART("heart.png"),
	ORANGE("orange.png"),
	STRAWBERRY("strawberry.png"),
	APPLE("apple.png"),
	PEAR("pear.png"),
	BANANA("banana.png");
	
	@Getter
	private Image image;

	private FruitsImages(String fileName) {
		String pathName = "images/fruits/" + fileName;
		image = new ImageIcon(FruitsImages.class.getClassLoader().getResource(pathName)).getImage();
	}
}
