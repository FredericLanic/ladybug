/**
  Copyright (C) 2021  Frédéric Lanic frederic.lanic@outlook.fr

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.kycox.game.engine.view.map;

import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.constant.game.GameImages;
import com.kycox.game.constant.level.LevelImages492;
import com.kycox.game.dynamic.fruit.Fruits;
import com.kycox.game.engine.screendata.ScreenData;
import com.kycox.game.tools.Utils;
import com.kycox.game.engine.view.map.rendering.DisplayRendering;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class ScreenBlockView {

	@Setter
	private Color colorMaze = GameMainConstants.BLUE_LADYBUG;

	private final Fruits fruits;

	private final DisplayRendering displayRendering;
	private final Color revivorPointColor = new Color(128, 255, 255);
	private final Color teleportationPointColor = new Color(255, 255, 255);

	public ScreenBlockView(Fruits fruits, DisplayRendering displayRendering) {
		this.fruits = fruits;
		this.displayRendering = displayRendering;
	}

	public void display(Graphics2D g2d, ScreenData screenData, int x, int y) {
		g2d.setColor(colorMaze);
		displayRendering.display(g2d, screenData, x, y);
		displayTeleportation(g2d, screenData, x, y);
		displayPoints(g2d, screenData, x, y);
		displayFruit(g2d, screenData, x, y);
	}

	private void displayPoints(Graphics2D g2d, ScreenData screenData, int x, int y) {
		var screenBlock = screenData.getScreenBlock(Utils.convertGraphicPointToBlockPoint(new Point(x, y)));

		// affichage du point de survie des fantômes
		if (screenBlock.isGhostReviver()) {
			g2d.setColor(revivorPointColor);
			g2d.fillOval(x + GameMainConstants.BLOCK_SIZE / 2 - 7, y + GameMainConstants.BLOCK_SIZE / 2 - 7 , 14, 14);
		}

		// affichage du mégapoint
		if (screenBlock.isPoint() && screenBlock.isMegaPoint()) {
			Image coin = LevelImages492.MEGA_GOLD.getImage();
			g2d.drawImage(coin, x + (GameMainConstants.BLOCK_SIZE - coin.getWidth(null) ) / 2, y + (GameMainConstants.BLOCK_SIZE - coin.getHeight(null) ) / 2, null);
		} else if (screenBlock.isPoint()) {
			Image coin = LevelImages492.GOLD.getImage();
			g2d.drawImage(coin, x + (GameMainConstants.BLOCK_SIZE - coin.getWidth(null) ) / 2, y + (GameMainConstants.BLOCK_SIZE - coin.getHeight(null) ) / 2, null);
		}
	}

	private void displayTeleportation(Graphics2D g2d, ScreenData screenData, int x, int y) {
		var screenBlock = screenData.getViewBlock(Utils.convertGraphicPointToBlockPoint(new Point(x, y)));
		if (screenBlock.isTeleportation()) {
			g2d.setColor(teleportationPointColor);
			g2d.fillOval(x + GameMainConstants.BLOCK_SIZE / 2 - 4, y + GameMainConstants.BLOCK_SIZE / 2 - 4, 10, 10);
			g2d.drawImage(GameImages.TELEPORTATION.getImage(), x, y, null);
		}
	}

	private void displayFruit(Graphics2D g2d, ScreenData screenData, int x, int y) {
		var screenBlock = screenData.getViewBlock(Utils.convertGraphicPointToBlockPoint(new Point(x, y)));
		if (screenBlock.getIdRefFruit() != GameMainConstants.NOFRUITID) {
			g2d.drawImage(fruits.getFruitImgById(screenBlock.getIdRefFruit()), x, y, null);
		}
	}
}
