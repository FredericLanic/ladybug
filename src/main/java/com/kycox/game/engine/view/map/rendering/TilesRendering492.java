package com.kycox.game.engine.view.map.rendering;

import com.kycox.game.tools.Position;
import com.kycox.game.constant.level.LevelImages492;
import com.kycox.game.engine.maze.ScreenBlock;
import com.kycox.game.engine.view.map.MazeOutline;
import com.kycox.game.tools.Utils;
import com.kycox.game.launcher.GuiComponent;

import java.awt.*;

@GuiComponent
public class TilesRendering492 implements Rendering {

	@Override
	public void displayScreenBlockBorders(Graphics2D g2d, MazeOutline outline, int x, int y) {
		var screenBlock = outline.getBlock(Utils.convertGraphicPointToBlockPoint(new Position(x, y)));
		displayScreenBlock(g2d, screenBlock, x, y);
	}

	private void displayScreenBlock(Graphics2D g2d, ScreenBlock screenBlock, int x, int y) {
		if (!screenBlock.isBorderRight() && screenBlock.isBorderLeft() && screenBlock.isBorderUp()  && screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages492.NO_RIGHT_LEFT_UP_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isBorderRight() && !screenBlock.isBorderLeft() && screenBlock.isBorderUp()  && screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages492.RIGHT_NO_LEFT_UP_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isBorderRight() && screenBlock.isBorderLeft() && screenBlock.isBorderUp()  && screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages492.RIGHT_LEFT_UP_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isBorderRight() && screenBlock.isBorderLeft() && screenBlock.isBorderUp()  && !screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages492.RIGHT_LEFT_UP_NO_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isBorderRight() && screenBlock.isBorderLeft() && !screenBlock.isBorderUp()  && screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages492.RIGHT_LEFT_NO_UP_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isBorderRight() && screenBlock.isBorderLeft() && !screenBlock.isBorderUp()  && !screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages492.RIGHT_LEFT_NO_UP_NO_DOWN.getImage(), x, y, null);
		} else if (!screenBlock.isBorderRight() && !screenBlock.isBorderLeft() && screenBlock.isBorderUp()  && screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages492.NO_RIGHT_NO_LEFT_UP_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isBorderRight() && !screenBlock.isBorderLeft() && screenBlock.isBorderUp()  && !screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages492.RIGHT_NO_LEFT_UP_NO_DOWN.getImage(), x, y, null);
		} else if (!screenBlock.isBorderRight() && screenBlock.isBorderLeft() && screenBlock.isBorderUp()  && !screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages492.NO_RIGHT_LEFT_UP_NO_DOWN.getImage(), x, y, null);
		} else if (!screenBlock.isBorderRight() && screenBlock.isBorderLeft() && !screenBlock.isBorderUp()  && screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages492.NO_RIGHT_LEFT_NO_UP_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isBorderRight() && !screenBlock.isBorderLeft() && !screenBlock.isBorderUp()  && screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages492.RIGHT_NO_LEFT_NO_UP_DOWN.getImage(), x, y, null);
		} else if (!screenBlock.isBorderRight() && !screenBlock.isBorderLeft() && screenBlock.isBorderUp()  && !screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages492.NO_RIGHT_NO_LEFT_UP_NO_DOWN.getImage(), x, y, null);
		} else if (!screenBlock.isBorderRight() && !screenBlock.isBorderLeft() && !screenBlock.isBorderUp()  && screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages492.NO_RIGHT_NO_LEFT_NO_UP_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isBorderRight() && !screenBlock.isBorderLeft() && !screenBlock.isBorderUp()  && !screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages492.RIGHT_NO_LEFT_NO_UP_NO_DOWN.getImage(), x, y, null);
		} else if (!screenBlock.isBorderRight() && screenBlock.isBorderLeft() && !screenBlock.isBorderUp()  && !screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages492.NO_RIGHT_LEFT_NO_UP_NO_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isNotAccessible()) {
			g2d.drawImage(LevelImages492.NO_RIGHT_NO_LEFT_NO_UP_NO_DOWN.getImage(), x, y, null);
		}
	}
}
