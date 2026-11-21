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

import com.kycox.game.tools.Position;
import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.constant.game.GameImages;
import com.kycox.game.constant.level.LevelImages492;
import com.kycox.game.constant.fruits.Fruits;
import com.kycox.game.constant.level.LevelRendering;
import com.kycox.game.snapshot.CellSnapshot;
import com.kycox.game.snapshot.MazeSnapshot;
import com.kycox.game.tools.Utils;
import com.kycox.game.engine.view.map.rendering.DisplayRendering;
import com.kycox.game.launcher.GuiComponent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

@GuiComponent
public class ScreenBlockView {

	private final Fruits fruits;

	private final DisplayRendering displayRendering;
	private final Color revivorPointColor = new Color(128, 255, 255);
	private final Color teleportationPointColor = new Color(255, 255, 255);

	/** Marge autour de la case : les traits des murs débordent d'un ou deux pixels. */
	private static final int MARGIN = 2;
	/** Luminosité d'un pixel noir une fois doré (évite un or trop sombre). */
	private static final double GOLD_FLOOR = 0.35;
	/** Part de la luminosité d'origine reportée sur l'or (garde la texture). */
	private static final double GOLD_CONTRAST = 0.9;
	/**
	 * Cases dorées déjà calculées (D25), par position. Valables pour une forme de
	 * labyrinthe et un style de dessin : recalculées quand l'un des deux change.
	 */
	private final Map<Position, BufferedImage> goldenCells = new HashMap<>();
	private int goldenLayoutVersion = -1;
	private LevelRendering goldenRendering;

	public ScreenBlockView(Fruits fruits, DisplayRendering displayRendering) {
		this.fruits = fruits;
		this.displayRendering = displayRendering;
	}

	/**
	 * Dessine une case du labyrinthe.
	 *
	 * @param goldenWalls vrai pendant les phases allumées de l'effet « vie gagnée »
	 *                    (D25) : les murs sont teintés d'or, quel que soit le style
	 *                    (traits ou images) ; les couloirs restent noirs.
	 */
	public void display(Graphics2D g2d, MazeSnapshot maze, LevelRendering levelRendering, int x, int y,
	        boolean goldenWalls) {
		// une seule lecture de la case ; hors du labyrinthe (cellAt renvoie null),
		// rien à dessiner : ni murs ni décorations (alerte SonarQube Cloud)
		var cell = maze.cellAt(Utils.convertGraphicPointToBlockPoint(new Position(x, y)));
		if (cell == null) {
			return;
		}
		if (goldenWalls) {
			displayGoldenWalls(g2d, maze, levelRendering, x, y);
		} else {
			g2d.setColor(MazeColors.BLUE_LADYBUG);
			displayRendering.display(g2d, maze, levelRendering, x, y);
		}
		displayTeleportation(g2d, cell, x, y);
		displayPoints(g2d, cell, x, y);
		displayFruit(g2d, cell, x, y);
	}

	/**
	 * Dessine la version dorée des murs de la case. Elle est calculée une seule
	 * fois par case, puis gardée tant que le labyrinthe et le style ne changent
	 * pas.
	 */
	private void displayGoldenWalls(Graphics2D g2d, MazeSnapshot maze, LevelRendering levelRendering, int x, int y) {
		if (maze.layoutVersion() != goldenLayoutVersion || levelRendering != goldenRendering) {
			goldenCells.clear();
			goldenLayoutVersion = maze.layoutVersion();
			goldenRendering = levelRendering;
		}
		var golden = goldenCells.computeIfAbsent(Utils.convertGraphicPointToBlockPoint(new Position(x, y)),
		        block -> goldenCell(maze, levelRendering, x, y));
		g2d.drawImage(golden, x - MARGIN, y - MARGIN, null);
	}

	/**
	 * Dessine les murs de la case dans une image transparente, puis passe chaque
	 * pixel dessiné à l'or en gardant sa luminosité : une pierre reste une pierre,
	 * mais dorée. Les couloirs, où rien n'est dessiné, restent transparents.
	 */
	private BufferedImage goldenCell(MazeSnapshot maze, LevelRendering levelRendering, int x, int y) {
		var image = new BufferedImage(GameMainConstants.BLOCK_SIZE + 2 * MARGIN,
		        GameMainConstants.BLOCK_SIZE + 2 * MARGIN, BufferedImage.TYPE_INT_ARGB);
		var g = image.createGraphics();
		g.translate(MARGIN - x, MARGIN - y);
		g.setColor(MazeColors.BLUE_LADYBUG);
		displayRendering.display(g, maze, levelRendering, x, y);
		g.dispose();
		for (var py = 0; py < image.getHeight(); py++) {
			for (var px = 0; px < image.getWidth(); px++) {
				image.setRGB(px, py, toGold(image.getRGB(px, py)));
			}
		}
		return image;
	}

	/** Un pixel ARGB passé à l'or : même transparence, même luminosité relative. */
	static int toGold(int argb) {
		var alpha = argb >>> 24;
		if (alpha == 0) {
			return argb;
		}
		var red = (argb >> 16) & 0xFF;
		var green = (argb >> 8) & 0xFF;
		var blue = argb & 0xFF;
		var luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255;
		var factor = GOLD_FLOOR + GOLD_CONTRAST * luminance;
		var gold = MazeColors.NEW_LIFE_GOLD;
		return alpha << 24 | channel(gold.getRed(), factor) << 16 | channel(gold.getGreen(), factor) << 8
		        | channel(gold.getBlue(), factor);
	}

	private static int channel(int goldValue, double factor) {
		return (int) Math.min(255, Math.round(goldValue * factor));
	}

	private void displayPoints(Graphics2D g2d, CellSnapshot screenBlock, int x, int y) {
		// affichage du point de survie des fantômes
		if (screenBlock.ghostReviver()) {
			g2d.setColor(revivorPointColor);
			g2d.fillOval(x + GameMainConstants.BLOCK_SIZE / 2 - 7, y + GameMainConstants.BLOCK_SIZE / 2 - 7 , 14, 14);
		}

		// affichage du mégapoint
		if (screenBlock.point() && screenBlock.megaPoint()) {
			Image coin = LevelImages492.MEGA_GOLD.getImage();
			g2d.drawImage(coin, x + (GameMainConstants.BLOCK_SIZE - coin.getWidth(null) ) / 2, y + (GameMainConstants.BLOCK_SIZE - coin.getHeight(null) ) / 2, null);
		} else if (screenBlock.point()) {
			Image coin = LevelImages492.GOLD.getImage();
			g2d.drawImage(coin, x + (GameMainConstants.BLOCK_SIZE - coin.getWidth(null) ) / 2, y + (GameMainConstants.BLOCK_SIZE - coin.getHeight(null) ) / 2, null);
		}
	}

	private void displayTeleportation(Graphics2D g2d, CellSnapshot screenBlock, int x, int y) {
		if (screenBlock.teleportation()) {
			g2d.setColor(teleportationPointColor);
			g2d.fillOval(x + GameMainConstants.BLOCK_SIZE / 2 - 4, y + GameMainConstants.BLOCK_SIZE / 2 - 4, 10, 10);
			g2d.drawImage(GameImages.TELEPORTATION.getImage(), x, y, null);
		}
	}

	private void displayFruit(Graphics2D g2d, CellSnapshot screenBlock, int x, int y) {
		if (screenBlock.idRefFruit() != GameMainConstants.NOFRUITID) {
			g2d.drawImage(fruits.getFruitImgById(screenBlock.idRefFruit()), x, y, null);
		}
	}
}
