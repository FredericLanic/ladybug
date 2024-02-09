package com.kycox.game.constant.fruits;

import lombok.Getter;
import lombok.Setter;

@Getter
class Fruit {
	@Setter
	private int activationPercent;
	private final FruitsImages fruitImage;
	private final int idFruit;
	@Setter
	private Fruit next;
	private final int score;

	public Fruit(int idFruit, FruitsImages fruitImage, int score, int activationPercent) {
		this.idFruit = idFruit;
		this.fruitImage = fruitImage;
		this.score = score;
		this.activationPercent = activationPercent;
	}

}
