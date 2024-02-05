package com.kycox.game.constant;

import java.awt.Image;
import java.util.Objects;

import javax.swing.ImageIcon;

import lombok.Getter;

@Getter
public enum FruitsImages {
	APPLE("apple.png"), BANANA("banana.png"), CHERRY("cherry.png"), HEART("heart.png"), ORANGE("orange.png"),
	PEAR("pear.png"), STRAWBERRY("strawberry.png");

	private Image image;

	FruitsImages(String fileName) {
		var pathName = "images/fruits/" + fileName;
		image = new ImageIcon(Objects.requireNonNull(FruitsImages.class.getClassLoader().getResource(pathName))).getImage();
	}
}
