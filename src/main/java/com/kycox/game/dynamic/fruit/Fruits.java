package com.kycox.game.dynamic.fruit;

import com.kycox.game.constant.fruits.FruitsImages;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class Fruits {

	private final int idBouchon = 0;
	private final Fruit cherry = new Fruit(1, FruitsImages.CHERRY, 5, 50);
	private final Fruit orange = new Fruit(3, FruitsImages.ORANGE, 15, 70);
	private final Fruit strawberry = new Fruit(4, FruitsImages.STRAWBERRY, 20, 77);
	private final Fruit apple = new Fruit(5, FruitsImages.APPLE, 25, 85);
	private final Fruit pear = new Fruit(6, FruitsImages.PEAR, 30, 92);
	private final Fruit banana = new Fruit(7, FruitsImages.BANANA, 35, 95);
	@Getter
	private Fruit currentFruit;
	private final Fruit fruitLimit = new Fruit(idBouchon, FruitsImages.CHERRY, 0, 110);

	public Fruits() {
		Fruit heart = new Fruit(2, FruitsImages.HEART, 10, 60);
		cherry.setNext(heart);
		heart.setNext(orange);
		orange.setNext(strawberry);
		strawberry.setNext(apple);
		apple.setNext(pear);
		pear.setNext(banana);
		banana.setNext(fruitLimit);

		init();
	}

	public int getActivationPercent() {
		return currentFruit.getActivationPercent();
	}

	public int getCurrentIdFruit() {
		return currentFruit.getIdFruit();
	}

	private Fruit getFruitById(Fruit fruit, int idRefFruit) {
		if (fruit.getIdFruit() != idRefFruit && fruit.getIdFruit() != idBouchon) {
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

	public void init() {
		currentFruit = cherry;
	}

	public void next() {
		currentFruit = currentFruit.getNext();
	}

}
