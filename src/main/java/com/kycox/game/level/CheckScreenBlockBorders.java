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
package com.kycox.game.level;

import java.awt.Point;

public class CheckScreenBlockBorders {
	private final ScreenData screenData;

	public CheckScreenBlockBorders(ScreenData screenData) {
		this.screenData = screenData;
	}

	/**
	 * Vérifie les bordures des ScreenBlocks utilisés pour la MAP
	 */
	public void checkDataBlockBorder() {
		for (var x = 0; x < screenData.getCurrentLevel().getNbrBlocksByLine(); x++) {
			for (var y = 0; y < screenData.getCurrentLevel().getNbrLines(); y++) {
				checkDataBlockBorder(new Point(x, y));
			}
		}
	}

	// FIXME : un peu de refacto ?
	// TODO : mettre un optional
	private void checkDataBlockBorder(Point point) {
		var screenBlock = screenData.getScreenBlock(point);
		ScreenBlock otherScreenBlock;
		if (screenBlock == null) {
			return;
		}
		// vérification des bordures principales !!!
		if (point.x == 0 && !screenBlock.isBorderLeft()) {
			screenBlock.addBorderLeft();
		}
		if (point.x == (screenData.getCurrentLevel().getNbrBlocksByLine() - 1) && !screenBlock.isBorderRight()) {
			screenBlock.addBorderRight();
		}
		if (point.y == 0 && !screenBlock.isBorderUp()) {
			screenBlock.addBorderUp();
		}
		if (point.y == (screenData.getCurrentLevel().getNbrLines() - 1) && !screenBlock.isBorderDown()) {
			screenBlock.addBorderDown();
		}
		// vérification de la bordure en haut
		otherScreenBlock = screenData.getScreenBlock(new Point(point.x, point.y - 1));
		if (screenBlock.isBorderUp() && point.y > 0 && otherScreenBlock != null && !otherScreenBlock.isBorderDown()) {
			otherScreenBlock.addBorderDown();
		}
		// vérification de la bordure en bas
		otherScreenBlock = screenData.getScreenBlock(new Point(point.x, point.y + 1));
		if (screenBlock.isBorderDown() && point.y < screenData.getCurrentLevel().getNbrLines() - 1
		        && otherScreenBlock != null && !otherScreenBlock.isBorderUp()) {
			otherScreenBlock.addBorderUp();
		}
		// vérification de la bordure à droite
		otherScreenBlock = screenData.getScreenBlock(new Point(point.x + 1, point.y));
		if (screenBlock.isBorderRight() && point.x < screenData.getCurrentLevel().getNbrBlocksByLine() - 1
		        && otherScreenBlock != null && !otherScreenBlock.isBorderLeft()) {
			otherScreenBlock.addBorderLeft();
		}
		// vérification de la bordure à gauche
		otherScreenBlock = screenData.getScreenBlock(new Point(point.x - 1, point.y));
		if (screenBlock.isBorderLeft() && point.x > 0 && otherScreenBlock != null
		        && !otherScreenBlock.isBorderRight()) {
			otherScreenBlock.addBorderRight();
		}
	}

	/**
	 * Vérifie les bordures des ScreenBlocks utilisés pour la Vue
	 */
	public void checkViewBlockBorder() {
		for (var x = 0; x < screenData.getCurrentLevel().getNbrBlocksByLine(); x++) {
			for (var y = 0; y < screenData.getCurrentLevel().getNbrLines(); y++) {
				checkViewBlockBorder(new Point(x, y));
			}
		}
	}

	private void checkViewBlockBorder(Point point) {
		var screenBlock = screenData.getViewBlock(point);
		ScreenBlock otherScreenBlock;
		if (!screenBlock.isNotAccessible()) {
			return;
		}
		otherScreenBlock = screenData.getViewBlock(new Point(point.x, point.y - 1));
		if (otherScreenBlock != null && screenBlock.isBorderUp()) {
			otherScreenBlock.removeBorderDown();
		}
		otherScreenBlock = screenData.getViewBlock(new Point(point.x, point.y + 1));
		if (otherScreenBlock != null && screenBlock.isBorderDown()) {
			otherScreenBlock.removeBorderUp();
		}
		otherScreenBlock = screenData.getViewBlock(new Point(point.x - 1, point.y));
		if (otherScreenBlock != null && screenBlock.isBorderLeft()) {
			otherScreenBlock.removeBorderRight();
		}
		otherScreenBlock = screenData.getViewBlock(new Point(point.x + 1, point.y));
		if (otherScreenBlock != null && screenBlock.isBorderRight()) {
			otherScreenBlock.removeBorderLeft();
		}
	}
}
