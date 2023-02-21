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

import com.kycox.game.body.ladybug.Ladybug;
import com.kycox.game.constant.Constants;
import com.kycox.game.contract.LevelStructure;
import com.kycox.game.tools.Utils;
import com.kycox.game.tools.dijkstra.UnitDijkstra;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * La map du jeu en cours
 *
 */
@Component
public final class ScreenData {
	@Getter
	private LevelStructure currentLevel;
	private List<ScreenBlock> dataScreenBlocks = new ArrayList<>();
	private int initNbrBlocksWithPoint = 0;
	@Getter
	@Setter
	private boolean litLampMode = false;
	private final ManageLevel manageLevel;
	@Getter
	private final List<ScreenBlock> viewScreenBlocks = new ArrayList<>();

	public ScreenData(ManageLevel manageLevel) {
		this.manageLevel = manageLevel;
	}

	public void addNewFruit(int idRefFruit) {
		var currentScreenBlock = dataScreenBlocks.get(getRandomPosNumEatenPoint());
		currentScreenBlock.setIdRefFruit(idRefFruit);
		viewScreenBlocks.stream().filter(sb -> sb.getCoordinate().equals(currentScreenBlock.getCoordinate())).findFirst()
		        .orElseThrow().setIdRefFruit(idRefFruit);
	}

	public List<UnitDijkstra> convertToDjistraList() {
		List<UnitDijkstra> lstUnitDijkstra = new ArrayList<>();
		// java8 : faire un stream
		for (ScreenBlock block : dataScreenBlocks) {
			lstUnitDijkstra.add(new UnitDijkstra(block));
		}
		return lstUnitDijkstra;
	}

	public Point getInitLadybugPos() {
		return Utils.convertBlockPointToGraphicPoint(currentLevel.getInitLadybugBlockPos());
	}

	public int getNbrBlocksWithEatenPoint() {
		return (int) dataScreenBlocks.stream().filter(ScreenBlock::isEatenPoint).count();
	}

	public int getNbrBlocksWithPoint() {
		return (int) dataScreenBlocks.stream().filter(ScreenBlock::isPoint).count();
	}

	public int getNbrLines() {
		return currentLevel.getNbrLines();
	}

	public int getPercentageEatenPoint() {
		return (initNbrBlocksWithPoint - getNbrBlocksWithPoint()) * 100 / initNbrBlocksWithPoint;
	}

	private int getPosNumEatenPoint(int numPoint) {
		var nbrPoint = 0;
		var pos = 0;
		for (var i = 0; i < dataScreenBlocks.size(); i++) {
			if (dataScreenBlocks.get(i).isEatenPoint()) {
				nbrPoint++;
				if (nbrPoint == numPoint) {
					pos = i;
				}
			}
		}
		return pos;
	}

	private int getPosNumPoint(int numPoint) {
		var nbrPoint = 0;
		var pos = 0;
		for (var i = 0; i < dataScreenBlocks.size(); i++) {
			if (dataScreenBlocks.get(i).isPoint()) {
				nbrPoint++;
				if (nbrPoint == numPoint) {
					pos = i;
				}
			}
		}
		return pos;
	}

	private int getRandomPosNumEatenPoint() {
		var nbrPoints = getNbrBlocksWithEatenPoint();
		var randomPoint = Utils.generateRandomInt(nbrPoints) + 1;
		return getPosNumEatenPoint(randomPoint);
	}

	private int getRandomPosNumPoint() {
		var nbrPoints = getNbrBlocksWithPoint();
		var randomPoint = Utils.generateRandomInt(nbrPoints) + 1;
		return getPosNumPoint(randomPoint);
	}

	public Point getRandomPosOnAPoint() {
		var pos = getRandomPosNumPoint();
		var yBlock = pos / currentLevel.getNbrBlocksByLine();
		var xBlock = pos % currentLevel.getNbrBlocksByLine();
		return new Point(xBlock, yBlock);
	}

	public Point getRevivorGhostPos() {
		return Utils.convertBlockPointToGraphicPoint(currentLevel.getGhostRegenerateBlockPoint());
	}

	public ScreenBlock getScreenBlock(Point posPoint) {
		return dataScreenBlocks.stream().filter(b -> b.getCoordinate().equals(posPoint)).findFirst().orElse(null);
	}

	public int getScreenHeight() {
		return getNbrLines() * Constants.BLOCK_SIZE;
	}

	public int getScreenWidth() {
		return currentLevel.getNbrBlocksByLine() * Constants.BLOCK_SIZE;
	}

	public ScreenBlock getViewBlock(Point posPoint) {
		// java8 :
		return viewScreenBlocks.stream().filter(b -> b.getCoordinate().equals(posPoint)).findFirst().orElse(null);
	}

	public void setLevelMap(int numLevel, boolean mustDisplayMegaPoints) {
		// récupération du level associé au numLevel
		currentLevel = manageLevel.getLevel(numLevel);
		// Création des blocks
		dataScreenBlocks = currentLevel.getLstBlocks();
		// vérification des bordures des blocks
		var checkScreenBlockBorders = new CheckScreenBlockBorders(this);
		checkScreenBlockBorders.checkDataBlockBorder();
		dataScreenBlocks.stream().filter(b -> b.getCoordinate().equals(currentLevel.getGhostRegenerateBlockPoint()))
		        .forEach(ScreenBlock::addGhostReviver);
		// ajout des mega points aléatoires
		if (mustDisplayMegaPoints) {
			for (var i = 0; i < currentLevel.getNbrMegaPoints(); i++) {
				var randomPoint = getRandomPosOnAPoint();
				dataScreenBlocks.stream().filter(b -> b.getCoordinate().equals(randomPoint))
				        .forEach(ScreenBlock::addMegaPoint);
			}

			var teleportPoints = currentLevel.getTeleportPoints();
			for (Map.Entry<Point, Point> m : teleportPoints.entrySet()) {
				dataScreenBlocks.stream().filter(b -> b.getCoordinate().equals(m.getKey()))
				        .forEach(sb -> sb.addTeleportation(m.getValue()));
			}

		}
		viewScreenBlocks.clear();
		// on clone la liste
		setViewScreenBlocks();

		checkScreenBlockBorders.checkViewBlockBorder();
		initNbrBlocksWithPoint = getNbrBlocksWithPoint();
	}

	private void setViewScreenBlocks() {
		dataScreenBlocks.stream().forEach(sb -> viewScreenBlocks.add(sb.clone()));
	}

	public void updateScreenBlock(Ladybug ladybug) {
		// Suppression du point dans le ScreenBlock de lstDataBlocks
		var currentScreenBlock = ladybug.getLadybugActions().getCurrentScreenBlock();
		if (ladybug.isEatenAMegaPoint() || ladybug.isEatenAPoint()) {
			currentScreenBlock.removePoint();
			currentScreenBlock.addEatenPoint();
			// Suppression du point dans de ScreenBlock de lstViewBlocks
			viewScreenBlocks.stream().filter(sb -> sb.getCoordinate().equals(currentScreenBlock.getCoordinate())).findFirst()
			        .orElseThrow().removePoint();
		}
		if (ladybug.hasEatenAFruit()) {
			currentScreenBlock.resetIdRefFruit();
			viewScreenBlocks.stream().filter(sb -> sb.getCoordinate().equals(currentScreenBlock.getCoordinate())).findFirst()
			        .orElseThrow().resetIdRefFruit();

		}
	}
}
