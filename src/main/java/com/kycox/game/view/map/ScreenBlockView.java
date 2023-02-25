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
package com.kycox.game.view.map;

import com.kycox.game.constant.GameMainConstants;
import com.kycox.game.constant.game.GameImages;
import com.kycox.game.constant.level.LevelImages;
import com.kycox.game.fruit.Fruits;
import com.kycox.game.level.ScreenData;
import com.kycox.game.tools.Utils;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class ScreenBlockView {
	private static final Color dotColor = new Color(192, 192, 0);
	@Setter
	private Color colorMaze = GameMainConstants.BLUE_LADYBUG;

	private final Fruits fruits;
	private final Color megaPointColor = new Color(255, 128, 0);
	private final Color revivorPointColor = new Color(128, 255, 255);
	private final Color teleportationPointColor = new Color(255, 255, 255);

	public ScreenBlockView(Fruits fruits) {
		this.fruits = fruits;
	}

	public void display(Graphics2D g2d, ScreenData screenData, int x, int y) {
		g2d.setColor(colorMaze);
		// displayBorders(g2d, screenData, x, y);
		displayBordersImages(g2d, screenData, x, y);
		displayTeleportation(g2d, screenData, x, y);
		displayPoints(g2d, screenData, x, y);
		displayFruit(g2d, screenData, x, y);
	}

	private void displayBordersImages(Graphics2D g2d, ScreenData screenData, int x, int y) {
		var screenBlock = screenData.getViewBlock(Utils.convertGraphicPointToBlockPoint(new Point(x, y)));
		if (!screenBlock.isBorderRight() && screenBlock.isBorderLeft() && screenBlock.isBorderUp()  && screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages.NO_RIGHT_LEFT_UP_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isBorderRight() && !screenBlock.isBorderLeft() && screenBlock.isBorderUp()  && screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages.RIGHT_NO_LEFT_UP_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isBorderRight() && screenBlock.isBorderLeft() && screenBlock.isBorderUp()  && screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages.RIGHT_LEFT_UP_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isBorderRight() && screenBlock.isBorderLeft() && screenBlock.isBorderUp()  && !screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages.RIGHT_LEFT_UP_NO_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isBorderRight() && screenBlock.isBorderLeft() && !screenBlock.isBorderUp()  && screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages.RIGHT_LEFT_NO_UP_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isBorderRight() && screenBlock.isBorderLeft() && !screenBlock.isBorderUp()  && !screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages.RIGHT_LEFT_NO_UP_NO_DOWN.getImage(), x, y, null);
		} else if (!screenBlock.isBorderRight() && !screenBlock.isBorderLeft() && screenBlock.isBorderUp()  && screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages.NO_RIGHT_NO_LEFT_UP_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isBorderRight() && !screenBlock.isBorderLeft() && screenBlock.isBorderUp()  && !screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages.RIGHT_NO_LEFT_UP_NO_DOWN.getImage(), x, y, null);
		} else if (!screenBlock.isBorderRight() && screenBlock.isBorderLeft() && screenBlock.isBorderUp()  && !screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages.NO_RIGHT_LEFT_UP_NO_DOWN.getImage(), x, y, null);
		} else if (!screenBlock.isBorderRight() && screenBlock.isBorderLeft() && !screenBlock.isBorderUp()  && screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages.NO_RIGHT_LEFT_NO_UP_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isBorderRight() && !screenBlock.isBorderLeft() && !screenBlock.isBorderUp()  && screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages.RIGHT_NO_LEFT_NO_UP_DOWN.getImage(), x, y, null);
		} else if (!screenBlock.isBorderRight() && !screenBlock.isBorderLeft() && screenBlock.isBorderUp()  && !screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages.NO_RIGHT_NO_LEFT_UP_NO_DOWN.getImage(), x, y, null);
		} else if (!screenBlock.isBorderRight() && !screenBlock.isBorderLeft() && !screenBlock.isBorderUp()  && screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages.NO_RIGHT_NO_LEFT_NO_UP_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isBorderRight() && !screenBlock.isBorderLeft() && !screenBlock.isBorderUp()  && !screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages.RIGHT_NO_LEFT_NO_UP_NO_DOWN.getImage(), x, y, null);
		} else if (!screenBlock.isBorderRight() && screenBlock.isBorderLeft() && !screenBlock.isBorderUp()  && !screenBlock.isBorderDown()) {
			g2d.drawImage(LevelImages.NO_RIGHT_LEFT_NO_UP_NO_DOWN.getImage(), x, y, null);
		} else if (screenBlock.isNotAccessible()) {
			g2d.drawImage(LevelImages.NO_RIGHT_NO_LEFT_NO_UP_NO_DOWN.getImage(), x, y, null);
		}
	}

	private void displayBorders(Graphics2D g2d, ScreenData screenData, int x, int y) {
		var screenBlock = screenData.getViewBlock(Utils.convertGraphicPointToBlockPoint(new Point(x, y)));
		g2d.setStroke(new BasicStroke(2));
		var currentCoord = screenBlock.getCoordinate();
		// affichage de la barre à gauche
		if (screenBlock.isBorderLeft() && !screenBlock.isBorderUp() && !screenBlock.isBorderDown()
		        || currentCoord.x == 0 && currentCoord.y != 0 && currentCoord.y != screenData.getNbrLines() - 1) {
			g2d.drawLine(x, y, x, y + GameMainConstants.BLOCK_SIZE);
		}
		// affichage de la barre en haut
		if (screenBlock.isBorderUp() && !screenBlock.isBorderLeft() && !screenBlock.isBorderRight()
		        || currentCoord.y == 0 && currentCoord.x != 0
		                && currentCoord.x != screenData.getCurrentLevel().getNbrBlocksByLine() - 1) {
			g2d.drawLine(x, y, x + GameMainConstants.BLOCK_SIZE, y);
		}
		// affichage de la barre à droite
		if (screenBlock.isBorderRight() && !screenBlock.isBorderUp() && !screenBlock.isBorderDown()
		        || currentCoord.x == screenData.getCurrentLevel().getNbrBlocksByLine() - 1 && currentCoord.y != 0
		                && currentCoord.y != screenData.getNbrLines() - 1) {
			g2d.drawLine(x + GameMainConstants.BLOCK_SIZE, y, x + GameMainConstants.BLOCK_SIZE, y + GameMainConstants.BLOCK_SIZE);
		}
		// affichage de la barre en bas
		if (screenBlock.isBorderDown() && !screenBlock.isBorderLeft() && !screenBlock.isBorderRight()
		        || currentCoord.y == screenData.getNbrLines() - 1 && currentCoord.x != 0
		                && currentCoord.x != screenData.getCurrentLevel().getNbrBlocksByLine() - 1) {
			g2d.drawLine(x, y + GameMainConstants.BLOCK_SIZE, x + GameMainConstants.BLOCK_SIZE, y + GameMainConstants.BLOCK_SIZE);
		}
		var rayon = GameMainConstants.BLOCK_SIZE;
		// affichage de la courbe gauche vers haut
		if (screenBlock.isBorderLeft() && screenBlock.isBorderUp()) {
			g2d.drawArc(x, y, rayon, rayon, 90, 90);
		}
		// affichage de la courbe gauche vers bas
		if (screenBlock.isBorderLeft() && screenBlock.isBorderDown()) {
			g2d.drawArc(x, y, rayon, rayon, 270, -90);
		}
		// affichage de la courbe haut vers droite
		if (screenBlock.isBorderRight() && screenBlock.isBorderUp()) {
			g2d.drawArc(x, y, rayon, rayon, 0, 90);
		}
		// affichage de la courbe droite vers bas
		if (screenBlock.isBorderRight() && screenBlock.isBorderDown()) {
			g2d.drawArc(x, y, rayon, rayon, 270, 90);
		}
		if (screenBlock.isBorderLeft() && screenBlock.isBorderUp()
		        && currentCoord.x < screenData.getCurrentLevel().getNbrBlocksByLine() - 1
		        && screenData.getViewBlock(new Point(currentCoord.x + 1, currentCoord.y)).isBorderUp()) {
			g2d.drawLine(x + GameMainConstants.BLOCK_SIZE / 2, y, x + GameMainConstants.BLOCK_SIZE, y);
		}
		if (screenBlock.isBorderUp() && currentCoord.x < screenData.getCurrentLevel().getNbrBlocksByLine() - 1
		        && currentCoord.y > 1
		        && screenData.getViewBlock(new Point(currentCoord.x + 1, currentCoord.y - 1)).isBorderLeft()) {
			g2d.drawLine(x + GameMainConstants.BLOCK_SIZE / 2, y, x + GameMainConstants.BLOCK_SIZE, y);
		}
		if (screenBlock.isBorderLeft() && screenBlock.isBorderDown()
		        && currentCoord.x < screenData.getCurrentLevel().getNbrBlocksByLine() - 1
		        && screenData.getViewBlock(new Point(currentCoord.x + 1, currentCoord.y)).isBorderDown()) {
			g2d.drawLine(x + GameMainConstants.BLOCK_SIZE / 2, y + GameMainConstants.BLOCK_SIZE, x + GameMainConstants.BLOCK_SIZE,
			        y + GameMainConstants.BLOCK_SIZE);
		}
		if (screenBlock.isBorderDown() && currentCoord.x < screenData.getCurrentLevel().getNbrBlocksByLine() - 1
		        && currentCoord.y < screenData.getNbrLines() - 1
		        && screenData.getViewBlock(new Point(currentCoord.x + 1, currentCoord.y + 1)).isBorderLeft()) {
			g2d.drawLine(x + GameMainConstants.BLOCK_SIZE / 2, y + GameMainConstants.BLOCK_SIZE, x + GameMainConstants.BLOCK_SIZE,
			        y + GameMainConstants.BLOCK_SIZE);
		}
		if (screenBlock.isBorderRight() && screenBlock.isBorderUp() && currentCoord.x > 0
		        && screenData.getViewBlock(new Point(currentCoord.x - 1, currentCoord.y)).isBorderUp()) {
			g2d.drawLine(x, y, x + GameMainConstants.BLOCK_SIZE / 2, y);
		}
		if (screenBlock.isBorderUp() && currentCoord.x > 0 && currentCoord.y > 1
		        && screenData.getViewBlock(new Point(currentCoord.x - 1, currentCoord.y - 1)).isBorderRight()) {
			g2d.drawLine(x, y, x + GameMainConstants.BLOCK_SIZE / 2, y);
		}
		var sbTmp = screenData.getViewBlock(new Point(currentCoord.x - 1, currentCoord.y));
		var sbTmp1 = screenData.getViewBlock(new Point(currentCoord.x - 1, currentCoord.y + 1));
		if (screenBlock.isBorderRight() && screenBlock.isBorderDown() && currentCoord.x > 0
		        && (sbTmp.isBorderDown() || sbTmp.isBorderRight())) {
			g2d.drawLine(x, y + GameMainConstants.BLOCK_SIZE, x + GameMainConstants.BLOCK_SIZE / 2, y + GameMainConstants.BLOCK_SIZE);
		}
		if (screenBlock.isBorderDown() && currentCoord.x > 0 && currentCoord.y < screenData.getNbrLines() - 1
		        && (sbTmp.isBorderDown() || sbTmp1.isBorderRight())) {
			g2d.drawLine(x, y + GameMainConstants.BLOCK_SIZE, x + GameMainConstants.BLOCK_SIZE / 2, y + GameMainConstants.BLOCK_SIZE);
		}
		// ********************//
		if (screenBlock.isBorderRight() && screenBlock.isBorderUp() && currentCoord.y < screenData.getNbrLines() - 1
		        && screenData.getViewBlock(new Point(currentCoord.x, currentCoord.y + 1)).isBorderRight()) {
			g2d.drawLine(x + GameMainConstants.BLOCK_SIZE, y + GameMainConstants.BLOCK_SIZE / 2, x + GameMainConstants.BLOCK_SIZE,
			        y + GameMainConstants.BLOCK_SIZE);
		}
		if (screenBlock.isBorderRight() && currentCoord.y < screenData.getNbrLines() - 1
		        && currentCoord.x < screenData.getCurrentLevel().getNbrBlocksByLine() - 1
		        && screenData.getViewBlock(new Point(currentCoord.x + 1, currentCoord.y + 1)).isBorderUp()) {
			g2d.drawLine(x + GameMainConstants.BLOCK_SIZE, y + GameMainConstants.BLOCK_SIZE / 2, x + GameMainConstants.BLOCK_SIZE,
			        y + GameMainConstants.BLOCK_SIZE);
		}
		if (screenBlock.isBorderLeft() && screenBlock.isBorderUp() && currentCoord.y < screenData.getNbrLines() - 1
		        && screenData.getViewBlock(new Point(currentCoord.x, currentCoord.y + 1)).isBorderLeft()) {
			g2d.drawLine(x, y + GameMainConstants.BLOCK_SIZE / 2, x, y + GameMainConstants.BLOCK_SIZE);
		}
		if (screenBlock.isBorderLeft() && currentCoord.y < screenData.getNbrLines() - 1 && currentCoord.x > 0
		        && screenData.getViewBlock(new Point(currentCoord.x - 1, currentCoord.y + 1)).isBorderUp()) {
			g2d.drawLine(x, y + GameMainConstants.BLOCK_SIZE / 2, x, y + GameMainConstants.BLOCK_SIZE);
		}
		if (screenBlock.isBorderRight() && screenBlock.isBorderDown() && currentCoord.y > 0
		        && screenData.getViewBlock(new Point(currentCoord.x, currentCoord.y - 1)).isBorderRight()) {
			g2d.drawLine(x + GameMainConstants.BLOCK_SIZE, y, x + GameMainConstants.BLOCK_SIZE, y + GameMainConstants.BLOCK_SIZE / 2);
		}
		if (screenBlock.isBorderRight() && currentCoord.y > 0
		        && currentCoord.x < screenData.getCurrentLevel().getNbrBlocksByLine() - 1
		        && screenData.getViewBlock(new Point(currentCoord.x + 1, currentCoord.y - 1)).isBorderDown()) {
			g2d.drawLine(x + GameMainConstants.BLOCK_SIZE, y, x + GameMainConstants.BLOCK_SIZE, y + GameMainConstants.BLOCK_SIZE / 2);
		}
		if (screenBlock.isBorderLeft() && screenBlock.isBorderDown() && currentCoord.y > 0
		        && screenData.getViewBlock(new Point(currentCoord.x, currentCoord.y - 1)).isBorderLeft()) {
			g2d.drawLine(x, y, x, y + GameMainConstants.BLOCK_SIZE / 2);
		}
		if (screenBlock.isBorderLeft() && currentCoord.y > 0 && currentCoord.x > 0
		        && screenData.getViewBlock(new Point(currentCoord.x - 1, currentCoord.y - 1)).isBorderDown()) {
			g2d.drawLine(x, y, x, y + GameMainConstants.BLOCK_SIZE / 2);
		}
	}

	private void displayPoints(Graphics2D g2d, ScreenData screenData, int x, int y) {
		var screenBlock = screenData.getScreenBlock(Utils.convertGraphicPointToBlockPoint(new Point(x, y)));
		// affichage du méga point

		// affichage du point de survie des fantômes
		if (screenBlock.isGhostReviver()) {
			g2d.setColor(revivorPointColor);
			g2d.fillOval(x + GameMainConstants.BLOCK_SIZE / 2 - 7, y + GameMainConstants.BLOCK_SIZE / 2 - 7 , 14, 14);
		}
		// affichage des points
//		g2d.setColor(dotColor);
//		if (screenBlock.isPoint() && !screenBlock.isMegaPoint()) {
//			g2d.fillRect(x + Constants.BLOCK_SIZE / 2 - 4, y + Constants.BLOCK_SIZE / 2 - 4, 4, 4);
//		}
//		if (screenBlock.isPoint() && !screenBlock.isBorderUp()) {
//			g2d.fillRect(x + Constants.BLOCK_SIZE / 2 - 2, y + Constants.BLOCK_SIZE / 2 - 2 - Constants.BLOCK_SIZE / 3,
//					2, 2);
//		}
//		if (screenBlock.isPoint() && !screenBlock.isBorderDown()) {
//			g2d.fillRect(x + Constants.BLOCK_SIZE / 2 - 2, y + Constants.BLOCK_SIZE / 2 - 2 + Constants.BLOCK_SIZE / 3,
//					2, 2);
//		}
//		if (screenBlock.isPoint() && !screenBlock.isBorderLeft()) {
//			g2d.fillRect(x + Constants.BLOCK_SIZE / 2 - 2 - Constants.BLOCK_SIZE / 3, y + Constants.BLOCK_SIZE / 2 - 2,
//					2, 2);
//		}
//		if (screenBlock.isPoint() && !screenBlock.isBorderRight()) {
//			g2d.fillRect(x + Constants.BLOCK_SIZE / 2 - 2 + Constants.BLOCK_SIZE / 3, y + Constants.BLOCK_SIZE / 2 - 2,
//					2, 2);
//		}

		if (screenBlock.isPoint() && screenBlock.isMegaPoint()) {
			Image coin = LevelImages.MEGA_GOLD.getImage();
			g2d.drawImage(coin, x + (GameMainConstants.BLOCK_SIZE - coin.getWidth(null) ) / 2, y + (GameMainConstants.BLOCK_SIZE - coin.getHeight(null) ) / 2, null);
		} else 	if (screenBlock.isPoint()) {
			Image coin = LevelImages.COIN.getImage();
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
