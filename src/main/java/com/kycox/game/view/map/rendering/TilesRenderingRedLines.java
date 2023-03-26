package com.kycox.game.view.map.rendering;

import com.kycox.game.constant.level.LevelImagesRedLines;
import com.kycox.game.level.ScreenBlock;
import com.kycox.game.level.ScreenData;
import com.kycox.game.tools.Utils;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class TilesRenderingRedLines implements Rendering {

	@Override
	public void displayScreenBlockBorders(Graphics2D g2d, ScreenData screenData, int x, int y) {
		var screenBlock = screenData.getViewBlock(Utils.convertGraphicPointToBlockPoint(new Point(x, y)));
		displayScreenBlock(g2d, screenBlock, x, y);
	}

	private void displayScreenBlock(Graphics2D g2d, ScreenBlock screenBlock, int x, int y) {
		if (!screenBlock.isBorderRight() && screenBlock.isBorderLeft() && screenBlock.isBorderUp()  && screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImagesRedLines.NO_RIGHT_LEFT_UP_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isBorderRight() && !screenBlock.isBorderLeft() && screenBlock.isBorderUp()  && screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImagesRedLines.RIGHT_NO_LEFT_UP_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isBorderRight() && screenBlock.isBorderLeft() && screenBlock.isBorderUp()  && screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImagesRedLines.RIGHT_LEFT_UP_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isBorderRight() && screenBlock.isBorderLeft() && screenBlock.isBorderUp()  && !screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImagesRedLines.RIGHT_LEFT_UP_NO_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isBorderRight() && screenBlock.isBorderLeft() && !screenBlock.isBorderUp()  && screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImagesRedLines.RIGHT_LEFT_NO_UP_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isBorderRight() && screenBlock.isBorderLeft() && !screenBlock.isBorderUp()  && !screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImagesRedLines.RIGHT_LEFT_NO_UP_NO_DOWN.getImage(), x, y, null);
		} else if (!screenBlock.isBorderRight() && !screenBlock.isBorderLeft() && screenBlock.isBorderUp()  && screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImagesRedLines.NO_RIGHT_NO_LEFT_UP_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isBorderRight() && !screenBlock.isBorderLeft() && screenBlock.isBorderUp()  && !screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImagesRedLines.RIGHT_NO_LEFT_UP_NO_DOWN.getImage(), x, y, null);
		} else if (!screenBlock.isBorderRight() && screenBlock.isBorderLeft() && screenBlock.isBorderUp()  && !screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImagesRedLines.NO_RIGHT_LEFT_UP_NO_DOWN.getImage(), x, y, null);
		} else if (!screenBlock.isBorderRight() && screenBlock.isBorderLeft() && !screenBlock.isBorderUp()  && screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImagesRedLines.NO_RIGHT_LEFT_NO_UP_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isBorderRight() && !screenBlock.isBorderLeft() && !screenBlock.isBorderUp()  && screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImagesRedLines.RIGHT_NO_LEFT_NO_UP_DOWN.getImage(), x, y, null);
		} else if (!screenBlock.isBorderRight() && !screenBlock.isBorderLeft() && screenBlock.isBorderUp()  && !screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImagesRedLines.NO_RIGHT_NO_LEFT_UP_NO_DOWN.getImage(), x, y, null);
		} else if (!screenBlock.isBorderRight() && !screenBlock.isBorderLeft() && !screenBlock.isBorderUp()  && screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImagesRedLines.NO_RIGHT_NO_LEFT_NO_UP_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isBorderRight() && !screenBlock.isBorderLeft() && !screenBlock.isBorderUp()  && !screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImagesRedLines.RIGHT_NO_LEFT_NO_UP_NO_DOWN.getImage(), x, y, null);
		} else if (!screenBlock.isBorderRight() && screenBlock.isBorderLeft() && !screenBlock.isBorderUp()  && !screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImagesRedLines.NO_RIGHT_LEFT_NO_UP_NO_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isNotAccessible()) {
			g2d.drawImage(LevelImagesRedLines.NO_RIGHT_NO_LEFT_NO_UP_NO_DOWN.getImage(), x, y, null);
		}
	}
}
