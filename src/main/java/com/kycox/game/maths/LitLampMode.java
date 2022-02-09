package com.kycox.game.maths;

import com.kycox.game.constant.Constants;

public final class LitLampMode {
	private static double a = 40.0 / (Constants.NBR_LEVELS - 1.0);
	private static double b = (50.0 * Constants.NBR_LEVELS - 90.0) / (Constants.NBR_LEVELS - 1.0);

	public static boolean isLitLampMode(int currentLevel) {
		var min = a * currentLevel + b;
		return Math.random() * 100 > min;
	}

	private LitLampMode() {
	}
}
