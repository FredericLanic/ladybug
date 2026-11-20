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
package com.kycox.game.engine.maze.utils;

import com.kycox.game.tools.Position;
import com.kycox.game.engine.maze.ScreenBlock;
import com.kycox.game.engine.maze.Maze;

/**
 * Complète les bordures des cases du labyrinthe pour les règles du jeu : bords
 * extérieurs, et symétrie entre deux cases voisines.
 *
 * <p>Depuis 4.4, le nettoyage des bordures pour le dessin des murs est fait par
 * {@code MazeOutline}, côté vue.</p>
 */
public class CheckScreenBlockBorders {
	private final Maze maze;

	public CheckScreenBlockBorders(Maze maze) {
		this.maze = maze;
	}

	/**
	 * Vérifie les bordures des ScreenBlocks utilisés pour la MAP
	 */
	public void checkDataBlockBorder() {
		for (var x = 0; x < maze.getCurrentLevel().getNbrBlocksByLine(); x++) {
			for (var y = 0; y < maze.getCurrentLevel().getNbrLines(); y++) {
				checkDataBlockBorder(new Position(x, y));
			}
		}
	}

	// FIXME : un peu de refacto ?
	// TODO : mettre un optional
	private void checkDataBlockBorder(Position point) {
		var screenBlock = maze.getScreenBlock(point);
		ScreenBlock otherScreenBlock;
		if (screenBlock == null) {
			return;
		}
		// vérification des bordures principales !!!
		if (point.x() == 0 && !screenBlock.isBorderLeft()) {
			screenBlock.addBorderLeft();
		}
		if (point.x() == (maze.getCurrentLevel().getNbrBlocksByLine() - 1) && !screenBlock.isBorderRight()) {
			screenBlock.addBorderRight();
		}
		if (point.y() == 0 && !screenBlock.isBorderUp()) {
			screenBlock.addBorderUp();
		}
		if (point.y() == (maze.getCurrentLevel().getNbrLines() - 1) && !screenBlock.isBorderDown()) {
			screenBlock.addBorderDown();
		}
		// vérification de la bordure en haut
		otherScreenBlock = maze.getScreenBlock(new Position(point.x(), point.y() - 1));
		if (screenBlock.isBorderUp() && point.y() > 0 && otherScreenBlock != null && !otherScreenBlock.isBorderDown()) {
			otherScreenBlock.addBorderDown();
		}
		// vérification de la bordure en bas
		otherScreenBlock = maze.getScreenBlock(new Position(point.x(), point.y() + 1));
		if (screenBlock.isBorderDown() && point.y() < maze.getCurrentLevel().getNbrLines() - 1
		        && otherScreenBlock != null && !otherScreenBlock.isBorderUp()) {
			otherScreenBlock.addBorderUp();
		}
		// vérification de la bordure à droite
		otherScreenBlock = maze.getScreenBlock(new Position(point.x() + 1, point.y()));
		if (screenBlock.isBorderRight() && point.x() < maze.getCurrentLevel().getNbrBlocksByLine() - 1
		        && otherScreenBlock != null && !otherScreenBlock.isBorderLeft()) {
			otherScreenBlock.addBorderLeft();
		}
		// vérification de la bordure à gauche
		otherScreenBlock = maze.getScreenBlock(new Position(point.x() - 1, point.y()));
		if (screenBlock.isBorderLeft() && point.x() > 0 && otherScreenBlock != null
		        && !otherScreenBlock.isBorderRight()) {
			otherScreenBlock.addBorderRight();
		}
	}
}
