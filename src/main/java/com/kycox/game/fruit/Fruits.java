package com.kycox.game.fruit;

import java.awt.Image;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import com.kycox.game.constant.fruit.image.FruitsImages;

import lombok.Getter;

@Named("Fruits")
public class Fruits {

	private static int ID_BOUCHON = 0;
	private Fruit apple = new Fruit(5, FruitsImages.APPLE, 9, 10);
	private Fruit banana = new Fruit(7, FruitsImages.BANANA, 11, 12);
	private Fruit cherry = new Fruit(1, FruitsImages.CHERRY, 5, 5);
	@Getter
	private Fruit currentFruit;
	private Fruit fruitLimit = new Fruit(ID_BOUCHON, FruitsImages.CHERRY, 0, 110);
	private Fruit heart = new Fruit(2, FruitsImages.HEART, 6, 7);
	private Fruit orange = new Fruit(3, FruitsImages.ORANGE, 7, 8);
	private Fruit pear = new Fruit(6, FruitsImages.PEAR, 10, 11);
	private Fruit strawberry = new Fruit(4, FruitsImages.STRAWBERRY, 8, 9);

	public Fruits() {
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
