package com.kycox.game.engine.view.map.rendering;

import com.kycox.game.engine.view.map.MazeOutline;

import java.awt.*;

public interface Rendering {
	void displayScreenBlockBorders(Graphics2D g2d, MazeOutline outline, int x, int y);
}
