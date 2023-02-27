package com.kycox.game.constant.level;

import java.util.Random;

public enum LevelRendering {
	LINES_RENDERING, TILES_RENDERING_492, TILES_RENDERING_584;

	private static final RandomEnum<LevelRendering> randomEnum = new RandomEnum<>(LevelRendering.class);

	public static LevelRendering getDefaultLevelRendering() {
		return LINES_RENDERING;
	}

	public static LevelRendering getRandomizeLevelRendering() {
		return randomEnum.random();
	}

	private static class RandomEnum<E extends Enum<E>> {
		private static final Random RANDOM = new Random();
		private final E[] values;

		public RandomEnum(Class<E> token) {
			values = token.getEnumConstants();
		}

		public E random() {
			return values[RANDOM.nextInt(values.length)];
		}
	}
}
