package com.kycox.game.constant.level;

import com.kycox.game.tools.GameRandom;

public enum LevelRendering {
	LINES_RENDERING, TILES_RENDERING_492, TILES_RENDERING_584, TILES_RENDERING_REDLINES;

	public static LevelRendering getDefaultLevelRendering() {
		return LINES_RENDERING;
	}

	/** Rendu tiré au hasard, via la source unique {@link GameRandom}. */
	public static LevelRendering getRandomizeLevelRendering() {
		return GameRandom.randomEnum(LevelRendering.class);
	}
}
