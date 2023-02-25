package com.kycox.game.view.map.rendering;

import com.kycox.game.level.ScreenData;

import java.awt.*;

public interface Rendering {
	void displayScreenBlockBorders(Graphics2D g2d, ScreenData screenData, int x, int y);
}
