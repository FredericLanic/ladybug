package com.kycox.game.constant;

import java.awt.Image;
import java.util.Objects;

import javax.swing.ImageIcon;

import com.kycox.game.constant.ghost.GhostHeadbandImages;
import lombok.Getter;

@Getter
public enum FruitsImages {
	APPLE("apple.png"), BANANA("banana.png"), CHERRY("cherry.png"), HEART("heart.png"), ORANGE("orange.png"),
	PEAR("pear.png"), STRAWBERRY("strawberry.png");

	private final Image image;
	private final ClassLoader classLoader = getClass().getClassLoader();

	FruitsImages(String fileName) {
		var pathName = "images/fruits/" + fileName;
		image = new ImageIcon(Objects.requireNonNull(classLoader.getResource(pathName))).getImage();
	}
}
