package com.kycox.game.tools;

import java.awt.Toolkit;

import javax.inject.Named;

import com.kycox.game.constant.Constants;

@Named("Screen")
public class Screen {
	final Toolkit defaultToolKit = Toolkit.getDefaultToolkit();

	public double getBorderHeight() {
		return (getScreenHeight() - 15 * Constants.BLOCK_SIZE) / 2;
	}

	public double getBorderWidth() {
		return (getScreenWidth() - 15 * Constants.BLOCK_SIZE) / 2;
	}

	public double getEdgeGameSide() {
		return 15 * Constants.BLOCK_SIZE;
	}

	public double getScreenHeight() {
		return defaultToolKit.getScreenSize().getHeight();
	}

	public double getScreenWidth() {
		return defaultToolKit.getScreenSize().getWidth();
	}
}
