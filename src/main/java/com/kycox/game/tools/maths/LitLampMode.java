package com.kycox.game.tools.maths;

import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.tools.GameRandom;

public final class LitLampMode {
	private static double a = 40.0 / (GameMainConstants.NBR_LEVELS - 1.0);
	private static double b = (50.0 * GameMainConstants.NBR_LEVELS - 90.0) / (GameMainConstants.NBR_LEVELS - 1.0);

	public static boolean isLitLampMode(int currentLevel) {
		var min = a * currentLevel + b;
		return GameRandom.nextDouble() * 100 > min;
	}

	private LitLampMode() {
	}
}
