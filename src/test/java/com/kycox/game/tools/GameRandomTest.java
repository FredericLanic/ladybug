package com.kycox.game.tools;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.kycox.game.constant.level.LevelRendering;

/**
 * Tests de la source unique de hasard (étape 3.5.1a) : même graine, mêmes tirages.
 */
class GameRandomTest {

	private static List<Object> draws() {
		var result = new ArrayList<Object>();
		for (var i = 0; i < 50; i++) {
			result.add(GameRandom.nextInt(1000));
			result.add(GameRandom.nextDouble());
			result.add(GameRandom.randomEnum(LevelRendering.class));
		}
		return result;
	}

	@Test
	void sameSeedGivesSameDraws() {
		GameRandom.reseed(42);
		var first = draws();
		GameRandom.reseed(42);
		var second = draws();

		assertThat(second).isEqualTo(first);
		assertThat(GameRandom.getSeed()).isEqualTo(42);
	}

	@Test
	void differentSeedsGiveDifferentDraws() {
		GameRandom.reseed(1);
		var first = draws();
		GameRandom.reseed(2);
		var second = draws();

		assertThat(second).isNotEqualTo(first);
	}

	@Test
	void gameDrawsGoThroughTheSameSource() {
		// Utils, LevelRendering et GameRandom partagent la même suite de tirages
		GameRandom.reseed(7);
		var expected = List.of(GameRandom.nextInt(10), GameRandom.nextInt(4));
		GameRandom.reseed(7);
		var actual = List.of(Utils.generateRandomInt(10),
		        LevelRendering.getRandomizeLevelRendering().ordinal());

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void randomEnumReachesEveryValue() {
		GameRandom.reseed(3);
		var seen = EnumSet.noneOf(LevelRendering.class);
		for (var i = 0; i < 200; i++) {
			seen.add(GameRandom.randomEnum(LevelRendering.class));
		}

		assertThat(seen).containsExactlyInAnyOrder(LevelRendering.values());
	}
}
