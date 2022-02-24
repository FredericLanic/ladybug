package com.kycox.game.fruit;

import java.awt.Image;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import com.kycox.game.constant.fruit.image.FruitsImages;

import lombok.Getter;

@Named("Fruits")
public class Fruits {

	private static int idBouchon = 0;
	private Fruit apple;
	private Fruit banana;
	private Fruit cherry;
	@Getter
	private Fruit currentFruit;
	private Fruit fruitLimit;
	private Fruit heart;
	private Fruit orange;
	private Fruit pear;
	private Fruit strawberry;

	public Fruits() {
		cherry = new Fruit(1, FruitsImages.CHERRY, 5, 50);
		heart = new Fruit(2, FruitsImages.HEART, 10, 60);
		orange = new Fruit(3, FruitsImages.ORANGE, 15, 70);
		strawberry = new Fruit(4, FruitsImages.STRAWBERRY, 20, 77);
		apple = new Fruit(5, FruitsImages.APPLE, 25, 85);
		pear = new Fruit(6, FruitsImages.PEAR, 30, 92);
		banana = new Fruit(7, FruitsImages.BANANA, 35, 95);
		fruitLimit = new Fruit(idBouchon, FruitsImages.CHERRY, 0, 110);

		cherry.setNext(heart);
		heart.setNext(orange);
		orange.setNext(strawberry);
		strawberry.setNext(apple);
		apple.setNext(pear);
		pear.setNext(banana);
		banana.setNext(fruitLimit);
	}

	public int getActivationPercent() {
		return currentFruit.getActivationPercent();
	}

	public int getCurrentIdFruit() {
		return currentFruit.getIdFruit();
	}

	private Fruit getFruitById(Fruit fruit, int idRefFruit) {
		if (fruit.getIdFruit() != idRefFruit && fruit.getIdFruit() != ID_BOUCHON) {
			return getFruitById(fruit.getNext(), idRefFruit);
		}
		return fruit;
	}

	public Image getFruitImgById(int idRefFruit) {
		return getFruitById(cherry, idRefFruit).getFruitImage().getImage();
	}

	public int getScoreFruitById(int idRefFruit) {
		return getFruitById(cherry, idRefFruit).getScore();
	}

	@PostConstruct
	public void init() {
		currentFruit = cherry;
	}

	public void next() {
		currentFruit = currentFruit.getNext();
	}

}
