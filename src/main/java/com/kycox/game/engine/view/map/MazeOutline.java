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

import com.kycox.game.engine.maze.Maze;
import com.kycox.game.engine.maze.ScreenBlock;
import com.kycox.game.launcher.GuiComponent;
import com.kycox.game.tools.Position;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Le contour du labyrinthe, tel qu'il est dessiné : une copie des cases du
 * {@link Maze} dont les bordures sont « nettoyées » pour un beau tracé (pas de
 * double trait entre un mur et son voisin, pas de trait sur le bord extérieur).
 *
 * <p>Étape 4.4 (« Extract Class ») : cette copie vivait dans {@code ScreenData}
 * et devait être tenue à jour à la main à chaque gomme ou fruit mangé. Elle ne
 * sert plus qu'à la <b>forme</b> des murs ; le contenu des cases (gommes,
 * fruits, téléportations) est lu directement dans le {@link Maze}. Le contour
 * est reconstruit dès que le labyrinthe change de forme (nouveau niveau). Voir
 * {@code doc/patterns/extract-class.md}.</p>
 */
@GuiComponent
public class MazeOutline {
	private final Map<Position, ScreenBlock> blocks = new HashMap<>();
	@Getter
	private int nbrBlocksByLine;
	@Getter
	private int nbrLines;
	/** Forme du labyrinthe à partir de laquelle le contour a été construit. */
	private int builtFromLayoutVersion = -1;

	/**
	 * Met le contour à jour si le labyrinthe a changé de forme, puis le renvoie.
	 */
	public MazeOutline of(Maze maze) {
		if (maze.getLayoutVersion() != builtFromLayoutVersion) {
			rebuild(maze);
		}
		return this;
	}

	/**
	 * La case dessinée à cette position (en cases, pas en pixels), ou
	 * {@code null} hors du labyrinthe.
	 */
	public ScreenBlock getBlock(Position blockPosition) {
		return blocks.get(blockPosition);
	}

	private void rebuild(Maze maze) {
		blocks.clear();
		maze.getScreenBlocks().forEach(sb -> blocks.put(sb.getCoordinate(), sb.clone()));
		nbrBlocksByLine = maze.getCurrentLevel().getNbrBlocksByLine();
		nbrLines = maze.getCurrentLevel().getNbrLines();
		for (var x = 0; x < nbrBlocksByLine; x++) {
			for (var y = 0; y < nbrLines; y++) {
				var coord = new Position(x, y);
				removeBordersSharedWithAWall(coord);
				removeOuterBorders(coord);
			}
		}
		builtFromLayoutVersion = maze.getLayoutVersion();
	}

	/**
	 * Les bords extérieurs sont dessinés à part : on retire leurs bordures.
	 */
	private void removeOuterBorders(Position coord) {
		var screenBlock = getBlock(coord);
		if (coord.x() == 0) {
			screenBlock.removeBorderLeft();
		} else if (coord.x() == nbrBlocksByLine - 1) {
			screenBlock.removeBorderRight();
		}

		if (coord.y() == 0) {
			screenBlock.removeBorderUp();
		} else if (coord.y() == nbrLines - 1) {
			screenBlock.removeBorderDown();
		}
	}

	/**
	 * Une case de mur garde ses bordures ; la case voisine perd la bordure qui
	 * leur est commune, pour ne tracer qu'un seul trait.
	 */
	private void removeBordersSharedWithAWall(Position point) {
		var screenBlock = getBlock(point);
		ScreenBlock otherScreenBlock;
		if (!screenBlock.isNotAccessible()) {
			return;
		}
		otherScreenBlock = getBlock(new Position(point.x(), point.y() - 1));
		if (otherScreenBlock != null && screenBlock.isBorderUp()) {
			otherScreenBlock.removeBorderDown();
		}
		otherScreenBlock = getBlock(new Position(point.x(), point.y() + 1));
		if (otherScreenBlock != null && screenBlock.isBorderDown()) {
			otherScreenBlock.removeBorderUp();
		}
		otherScreenBlock = getBlock(new Position(point.x() - 1, point.y()));
		if (otherScreenBlock != null && screenBlock.isBorderLeft()) {
			otherScreenBlock.removeBorderRight();
		}
		otherScreenBlock = getBlock(new Position(point.x() + 1, point.y()));
		if (otherScreenBlock != null && screenBlock.isBorderRight()) {
			otherScreenBlock.removeBorderLeft();
		}
	}
}
