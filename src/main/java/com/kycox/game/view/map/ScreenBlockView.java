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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.inject.Inject;
import javax.inject.Named;

import com.kycox.game.constant.Constants;
import com.kycox.game.constant.GameImages;
import com.kycox.game.fruit.Fruits;
import com.kycox.game.level.ScreenBlock;
import com.kycox.game.level.ScreenData;
import com.kycox.game.tools.Utils;

import lombok.Setter;

@Named("ScreenBlockView")
public class ScreenBlockView {
	@Inject
	private Fruits fruits;
	// Couleur d'un point
	private static final Color dotColor	 = new Color(192, 192, 0);
	@Setter
	private Color			   colorMaze = Constants.BLUE_LADYBUG;
	// Couleur du labyrinthe
	private final Color megaPointColor = new Color(255, 128, 0);
	// Couleur du lieu revivor
	private final Color revivorColor = new Color(128, 255, 255);
	// Couleur du labyrinthe
	private final Color teleportationColor = new Color(255, 255, 255);

	public void display(Graphics2D g2d, ScreenData screenData, int x, int y) {
		g2d.setColor(colorMaze);
		displayBorders(g2d, screenData, x, y);
		displayPoints(g2d, screenData, x, y);
		displayTeleportation(g2d, screenData, x, y);
		displayFruit(g2d, screenData, x, y);
	}

	/**
	 * Affichage des bordures
	 *
	 * Note : me faire confiance, je sais ce n'est pas maintenable en l'état :)
	 *
	 * @param g2d
	 * @param screenBlock
	 * @param x
	 * @param y
	 */
	private void displayBorders(Graphics2D g2d, ScreenData screenData, int x, int y) {
		ScreenBlock screenBlock = screenData.getViewBlock(Utils.convertPointToBlockUnit(new Point(x, y)));
		g2d.setStroke(new BasicStroke(2));
		Point currentCoord = screenBlock.getCoordinate();
		// affichage de la barre à gauche
		if (screenBlock.isBorderLeft() && !screenBlock.isBorderUp() && !screenBlock.isBorderDown()
		        || currentCoord.x == 0 && currentCoord.y != 0 && currentCoord.y != screenData.getNbrLines() - 1) {
			g2d.drawLine(x, y, x, y + Constants.BLOCK_SIZE);
		}
		// affichage de la barre en haut
		if (screenBlock.isBorderUp() && !screenBlock.isBorderLeft() && !screenBlock.isBorderRight()
		        || currentCoord.y == 0 && currentCoord.x != 0
		                && currentCoord.x != screenData.getCurrentLevel().getNbrBlocksByLine() - 1) {
			g2d.drawLine(x, y, x + Constants.BLOCK_SIZE, y);
		}
		// affichage de la barre à droite
		if (screenBlock.isBorderRight() && !screenBlock.isBorderUp() && !screenBlock.isBorderDown()
		        || currentCoord.x == screenData.getCurrentLevel().getNbrBlocksByLine() - 1 && currentCoord.y != 0
		                && currentCoord.y != screenData.getNbrLines() - 1) {
			g2d.drawLine(x + Constants.BLOCK_SIZE, y, x + Constants.BLOCK_SIZE, y + Constants.BLOCK_SIZE);
		}
		// affichage de la barre en bas
		if (screenBlock.isBorderDown() && !screenBlock.isBorderLeft() && !screenBlock.isBorderRight()
		        || currentCoord.y == screenData.getNbrLines() - 1 && currentCoord.x != 0
		                && currentCoord.x != screenData.getCurrentLevel().getNbrBlocksByLine() - 1) {
			g2d.drawLine(x, y + Constants.BLOCK_SIZE, x + Constants.BLOCK_SIZE, y + Constants.BLOCK_SIZE);
		}
		int rayon = Constants.BLOCK_SIZE;
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
			g2d.drawLine(x + Constants.BLOCK_SIZE / 2, y, x + Constants.BLOCK_SIZE, y);
		}
		if (screenBlock.isBorderUp() && currentCoord.x < screenData.getCurrentLevel().getNbrBlocksByLine() - 1
		        && currentCoord.y > 1
		        && screenData.getViewBlock(new Point(currentCoord.x + 1, currentCoord.y - 1)).isBorderLeft()) {
			g2d.drawLine(x + Constants.BLOCK_SIZE / 2, y, x + Constants.BLOCK_SIZE, y);
		}
		if (screenBlock.isBorderLeft() && screenBlock.isBorderDown()
		        && currentCoord.x < screenData.getCurrentLevel().getNbrBlocksByLine() - 1
		        && screenData.getViewBlock(new Point(currentCoord.x + 1, currentCoord.y)).isBorderDown()) {
			g2d.drawLine(x + Constants.BLOCK_SIZE / 2, y + Constants.BLOCK_SIZE, x + Constants.BLOCK_SIZE,
			        y + Constants.BLOCK_SIZE);
		}
		if (screenBlock.isBorderDown() && currentCoord.x < screenData.getCurrentLevel().getNbrBlocksByLine() - 1
		        && currentCoord.y < screenData.getNbrLines() - 1
		        && screenData.getViewBlock(new Point(currentCoord.x + 1, currentCoord.y + 1)).isBorderLeft()) {
			g2d.drawLine(x + Constants.BLOCK_SIZE / 2, y + Constants.BLOCK_SIZE, x + Constants.BLOCK_SIZE,
			        y + Constants.BLOCK_SIZE);
		}
		if (screenBlock.isBorderRight() && screenBlock.isBorderUp() && currentCoord.x > 0
		        && screenData.getViewBlock(new Point(currentCoord.x - 1, currentCoord.y)).isBorderUp()) {
			g2d.drawLine(x, y, x + Constants.BLOCK_SIZE / 2, y);
		}
		if (screenBlock.isBorderUp() && currentCoord.x > 0 && currentCoord.y > 1
		        && screenData.getViewBlock(new Point(currentCoord.x - 1, currentCoord.y - 1)).isBorderRight()) {
			g2d.drawLine(x, y, x + Constants.BLOCK_SIZE / 2, y);
		}
		ScreenBlock	sbTmp  = screenData.getViewBlock(new Point(currentCoord.x - 1, currentCoord.y));
		ScreenBlock	sbTmp1 = screenData.getViewBlock(new Point(currentCoord.x - 1, currentCoord.y + 1));
		if (screenBlock.isBorderRight() && screenBlock.isBorderDown() && currentCoord.x > 0
		        && (sbTmp.isBorderDown() || sbTmp.isBorderRight())) {
			g2d.drawLine(x, y + Constants.BLOCK_SIZE, x + Constants.BLOCK_SIZE / 2, y + Constants.BLOCK_SIZE);
		}
		if (screenBlock.isBorderDown() && currentCoord.x > 0 && currentCoord.y < screenData.getNbrLines() - 1
		        && (sbTmp.isBorderDown() || sbTmp1.isBorderRight())) {
			g2d.drawLine(x, y + Constants.BLOCK_SIZE, x + Constants.BLOCK_SIZE / 2, y + Constants.BLOCK_SIZE);
		}
		// ********************//
		if (screenBlock.isBorderRight() && screenBlock.isBorderUp() && currentCoord.y < screenData.getNbrLines() - 1
		        && screenData.getViewBlock(new Point(currentCoord.x, currentCoord.y + 1)).isBorderRight()) {
			g2d.drawLine(x + Constants.BLOCK_SIZE, y + Constants.BLOCK_SIZE / 2, x + Constants.BLOCK_SIZE,
			        y + Constants.BLOCK_SIZE);
		}
		if (screenBlock.isBorderRight() && currentCoord.y < screenData.getNbrLines() - 1
		        && currentCoord.x < screenData.getCurrentLevel().getNbrBlocksByLine() - 1
		        && screenData.getViewBlock(new Point(currentCoord.x + 1, currentCoord.y + 1)).isBorderUp()) {
			g2d.drawLine(x + Constants.BLOCK_SIZE, y + Constants.BLOCK_SIZE / 2, x + Constants.BLOCK_SIZE,
			        y + Constants.BLOCK_SIZE);
		}
		if (screenBlock.isBorderLeft() && screenBlock.isBorderUp() && currentCoord.y < screenData.getNbrLines() - 1
		        && screenData.getViewBlock(new Point(currentCoord.x, currentCoord.y + 1)).isBorderLeft()) {
			g2d.drawLine(x, y + Constants.BLOCK_SIZE / 2, x, y + Constants.BLOCK_SIZE);
		}
		if (screenBlock.isBorderLeft() && currentCoord.y < screenData.getNbrLines() - 1 && currentCoord.x > 0
		        && screenData.getViewBlock(new Point(currentCoord.x - 1, currentCoord.y + 1)).isBorderUp()) {
			g2d.drawLine(x, y + Constants.BLOCK_SIZE / 2, x, y + Constants.BLOCK_SIZE);
		}
		if (screenBlock.isBorderRight() && screenBlock.isBorderDown() && currentCoord.y > 0
		        && screenData.getViewBlock(new Point(currentCoord.x, currentCoord.y - 1)).isBorderRight()) {
			g2d.drawLine(x + Constants.BLOCK_SIZE, y, x + Constants.BLOCK_SIZE, y + Constants.BLOCK_SIZE / 2);
		}
		if (screenBlock.isBorderRight() && currentCoord.y > 0
		        && currentCoord.x < screenData.getCurrentLevel().getNbrBlocksByLine() - 1
		        && screenData.getViewBlock(new Point(currentCoord.x + 1, currentCoord.y - 1)).isBorderDown()) {
			g2d.drawLine(x + Constants.BLOCK_SIZE, y, x + Constants.BLOCK_SIZE, y + Constants.BLOCK_SIZE / 2);
		}
		if (screenBlock.isBorderLeft() && screenBlock.isBorderDown() && currentCoord.y > 0
		        && screenData.getViewBlock(new Point(currentCoord.x, currentCoord.y - 1)).isBorderLeft()) {
			g2d.drawLine(x, y, x, y + Constants.BLOCK_SIZE / 2);
		}
		if (screenBlock.isBorderLeft() && currentCoord.y > 0 && currentCoord.x > 0
		        && screenData.getViewBlock(new Point(currentCoord.x - 1, currentCoord.y - 1)).isBorderDown()) {
			g2d.drawLine(x, y, x, y + Constants.BLOCK_SIZE / 2);
		}
	}

	/**
	 * Display the points into the map
	 *
	 * @param g2d
	 * @param screenBlock
	 * @param x
	 * @param y
	 */
	private void displayPoints(Graphics2D g2d, ScreenData screenData, int x, int y) {
		ScreenBlock screenBlock = screenData.getDataBlock(Utils.convertPointToBlockUnit(new Point(x, y)));
		// affichage du méga point
		if (screenBlock.isPoint() && screenBlock.isMegaPoint()) {
			g2d.setColor(megaPointColor);
			g2d.fillOval(x + Constants.BLOCK_SIZE / 2 - 8, y + Constants.BLOCK_SIZE / 2 - 8, 12, 12);
		}
		// affichage du point de survie des fantômes
		if (screenBlock.isGhostReviver()) {
			g2d.setColor(revivorColor);
			g2d.fillOval(x + Constants.BLOCK_SIZE / 2 - 6, y + Constants.BLOCK_SIZE / 2 - 6, 8, 8);
		}
		// affichage des points
		g2d.setColor(dotColor);
		if (screenBlock.isPoint() && !screenBlock.isMegaPoint()) {
			g2d.fillRect(x + Constants.BLOCK_SIZE / 2 - 4, y + Constants.BLOCK_SIZE / 2 - 4, 4, 4);
		}
		if (screenBlock.isPoint() && !screenBlock.isBorderUp()) {
			g2d.fillRect(x + Constants.BLOCK_SIZE / 2 - 2, y + Constants.BLOCK_SIZE / 2 - 2 - Constants.BLOCK_SIZE / 3,
			        2, 2);
		}
		if (screenBlock.isPoint() && !screenBlock.isBorderDown()) {
			g2d.fillRect(x + Constants.BLOCK_SIZE / 2 - 2, y + Constants.BLOCK_SIZE / 2 - 2 + Constants.BLOCK_SIZE / 3,
			        2, 2);
		}
		if (screenBlock.isPoint() && !screenBlock.isBorderLeft()) {
			g2d.fillRect(x + Constants.BLOCK_SIZE / 2 - 2 - Constants.BLOCK_SIZE / 3, y + Constants.BLOCK_SIZE / 2 - 2,
			        2, 2);
		}
		if (screenBlock.isPoint() && !screenBlock.isBorderRight()) {
			g2d.fillRect(x + Constants.BLOCK_SIZE / 2 - 2 + Constants.BLOCK_SIZE / 3, y + Constants.BLOCK_SIZE / 2 - 2,
			        2, 2);
		}
	}

	private void displayTeleportation(Graphics2D g2d, ScreenData screenData, int x, int y) {
		ScreenBlock screenBlock = screenData.getViewBlock(Utils.convertPointToBlockUnit(new Point(x, y)));
		if (screenBlock.isTeleportation()) {
			g2d.setColor(teleportationColor);
			g2d.fillOval(x + Constants.BLOCK_SIZE / 2 - 4, y + Constants.BLOCK_SIZE / 2 - 4, 10, 10);
			g2d.drawImage(GameImages.TELEPORTATION.getImage(), x, y, null);
		}
	}
	
	private void displayFruit(Graphics2D g2d, ScreenData screenData, int x, int y) {
		ScreenBlock screenBlock = screenData.getViewBlock(Utils.convertPointToBlockUnit(new Point(x, y)));
		if (screenBlock.getIdRefFruit() !=  Constants.NOFRUITID) {
			g2d.drawImage(fruits.getFruitImgById(screenBlock.getIdRefFruit()), x, y, null);
		}
	}
}
