package com.kycox.game.engine.view.map.rendering;

import com.kycox.game.engine.screendata.ScreenData;

import java.awt.*;

public interface Rendering {
	void displayScreenBlockBorders(Graphics2D g2d, ScreenData screenData, int x, int y);
}
