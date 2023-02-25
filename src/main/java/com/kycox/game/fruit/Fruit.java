package com.kycox.game.fruit;

import com.kycox.game.constant.FruitsImages;

import lombok.Getter;
import lombok.Setter;

class Fruit {
	@Getter
	@Setter
	private int activationPercent;
	@Getter
	private final FruitsImages fruitImage;
	@Getter
	private final int idFruit;
	@Getter
	@Setter
	private Fruit next;
	@Getter
	private final int score;

	public Fruit(int idFruit, FruitsImages fruitImage, int score, int activationPercent) {
		this.idFruit = idFruit;
		this.fruitImage = fruitImage;
		this.score = score;
		this.activationPercent = activationPercent;
	}

}
