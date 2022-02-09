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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.kycox.game.body.ladybug.Ladybug;
import com.kycox.game.constant.Constants;
import com.kycox.game.contract.LevelStructure;
import com.kycox.game.tools.Utils;
import com.kycox.game.tools.dijkstra.UnitDijkstra;

import lombok.Getter;
import lombok.Setter;

/**
 * La map du jeu en cours
 *
 */
@Named("ScreenData")
public final class ScreenData {
	@Getter
	private LevelStructure currentLevel;
	private List<ScreenBlock> dataBlocks = new ArrayList<>();
	private int initNbrBlocksWithPoint = 0;
	@Getter
	@Setter
	private boolean litLampMode = false;
	@Inject
	private ManageLevel manageLevel;
	@Getter
	private List<ScreenBlock> viewBlocks = new ArrayList<>();

	public void addNewFruit(int idRefFruit) {
		var currentScreenBlock = dataBlocks.get(getRandomPosNumEatenPoint());
		currentScreenBlock.setIdRefFruit(idRefFruit);
		viewBlocks.stream().filter(sb -> sb.getCoordinate().equals(currentScreenBlock.getCoordinate())).findFirst()
		        .orElseThrow().setIdRefFruit(idRefFruit);
	}

	/**
	 * Convert le ScreenData en une List<UnitDijkstra>
	 *
	 * @return
	 */
	public List<UnitDijkstra> convertToDjistraList() {
		List<UnitDijkstra> lstUnitDijkstra = new ArrayList<>();
		// java8 : faire un stream
		for (ScreenBlock block : dataBlocks) {
			lstUnitDijkstra.add(new UnitDijkstra(block));
		}
		return lstUnitDijkstra;
	}

	/**
	 * Retourne un objet de type ScreenBlock qui représente le block
	 *
	 * @param pointPos : coordonnées BLOCK (x,y) dans la fenêtre
	 * @return
	 */
	public ScreenBlock getDataBlock(Point posPoint) {
		// java8 :
		return dataBlocks.stream().filter(b -> b.getCoordinate().equals(posPoint)).findFirst().orElse(null);
	}

	/**
	 * Retourne la position initiale de ladybug
	 *
	 * @return
	 */
	public Point getInitLadybugPos() {
		return Utils.convertPointToGraphicUnit(currentLevel.getInitLadybugBlockPos());
	}

	/**
	 * Retourne le nombre de points mangé par ladybug présents dans le IData
	 */
	public int getNbrBlocksWithEatenPoint() {
		return (int) dataBlocks.stream().filter(ScreenBlock::isEatenPoint).count();
	}

	/**
	 * Retourne le nombre de points à manger par ladybug présents dans le IData
	 */
	public int getNbrBlocksWithPoint() {
		return (int) dataBlocks.stream().filter(ScreenBlock::isPoint).count();
	}

	/**
	 * Retourne le nombre de lignes de la map du niveau
	 *
	 * @return
	 */
	public int getNbrLines() {
		return currentLevel.getNbrLines();
	}

	/**
	 * Retourne le nombre de méga point à mettre aléatoirement dans la map
	 *
	 * @return
	 */
	// FIXME : mettre ce nombre dans le Level ? ou bien faire un calcul du nombre de
	// méga point en fonction du niveau
	public int getNbrMegaPoint() {
		return 2;
	}

	/**
	 * Return the percentage of eaten point
	 *
	 * @return
	 */
	public int getPercentageEatenPoint() {
		return (initNbrBlocksWithPoint - getNbrBlocksWithPoint()) * 100 / initNbrBlocksWithPoint;
	}

	private int getPosNumEatenPoint(int numPoint) {
		var nbrPoint = 0;
		var pos = 0;
		for (var i = 0; i < dataBlocks.size(); i++) {
			if (dataBlocks.get(i).isEatenPoint()) {
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
		for (var i = 0; i < dataBlocks.size(); i++) {
			if (dataBlocks.get(i).isPoint()) {
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

	/**
	 * Retourne les coordonnées aléatoire GRAPHIQUE d'un block qui contient un point
	 * à manger par ladybug
	 *
	 * @return
	 */
	public Point getRandomPosOnAEatenPoint() {
		var pos = getRandomPosNumEatenPoint();
		var yBlock = pos / currentLevel.getNbrBlocksByLine();
		var xBlock = pos % currentLevel.getNbrBlocksByLine();
		return new Point(xBlock, yBlock);
	}

	/**
	 * Retourne les coordonnées aléatoire GRAPHIQUE d'un block qui contient un point
	 * à manger par ladybug
	 *
	 * @return
	 */
	public Point getRandomPosOnAPoint() {
		var pos = getRandomPosNumPoint();
		var yBlock = pos / currentLevel.getNbrBlocksByLine();
		var xBlock = pos % currentLevel.getNbrBlocksByLine();
		return new Point(xBlock, yBlock);
	}

	/**
	 * Retourne le point ou les fantômes renaissent
	 *
	 * @return
	 */
	public Point getRevivorGhostPos() {
		return Utils.convertPointToGraphicUnit(currentLevel.getGhostRegenerateBlockPoint());
	}

	/**
	 * Retourne la hauteur GRAPHIQUE de la map du jeu (coord y)
	 *
	 * @return
	 */
	public int getScreenHeight() {
		return getNbrLines() * Constants.BLOCK_SIZE;
	}

	/**
	 * Retourne la largeur GRAPHIQUE de la map du jeu (coord x)
	 *
	 * @return
	 */
	public int getScreenWidth() {
		return currentLevel.getNbrBlocksByLine() * Constants.BLOCK_SIZE;
	}

	/**
	 * Retourne un objet de type ScreenBlock qui représente le block
	 *
	 * @param pointPos : coordonnées BLOCK (x,y) dans la fenêtre
	 * @return
	 */
	public ScreenBlock getViewBlock(Point posPoint) {
		// java8 :
		return viewBlocks.stream().filter(b -> b.getCoordinate().equals(posPoint)).findFirst().orElse(null);
	}

	/***
	 * Affecte la map en fonction du numero de niveau
	 *
	 * @param numLevel
	 */
	public void setLevelMap(int numLevel, boolean mustDisplayMegaPoints) {
		// récupération du level associé au numLevel
		currentLevel = manageLevel.getLevel(numLevel);
		// Création des blocks
		dataBlocks = currentLevel.getLstBlocks();
		// vérification des bordure des blocks
		var checkScreenBlockBorders = new CheckScreenBlockBorders(this);
		checkScreenBlockBorders.checkDataBlockBorder();
		dataBlocks.stream().filter(b -> b.getCoordinate().equals(currentLevel.getGhostRegenerateBlockPoint()))
		        .forEach(ScreenBlock::addGhostReviver);
		// ajout des mega points aléatoires
		if (mustDisplayMegaPoints) {
			for (var i = 0; i < currentLevel.getNbrMegaPoints(); i++) {
				var randomPoint = getRandomPosOnAPoint();
				dataBlocks.stream().filter(b -> b.getCoordinate().equals(randomPoint))
				        .forEach(ScreenBlock::addMegaPoint);
			}

			var teleportPoints = currentLevel.getTeleportPoints();
			for (Map.Entry<Point, Point> m : teleportPoints.entrySet()) {
				dataBlocks.stream().filter(b -> b.getCoordinate().equals(m.getKey()))
				        .forEach(sb -> sb.addTeleportation(m.getValue()));
			}

		}
		viewBlocks.clear();
		// on clone la liste
		dataBlocks.stream().forEach(sb -> viewBlocks.add(sb.clone()));
		checkScreenBlockBorders.checkViewBlockBorder();
		initNbrBlocksWithPoint = getNbrBlocksWithPoint();
	}

	/**
	 * Update Point
	 *
	 * FIXME : à mettre ailleurs ou appeler différemment, par exemple sans
	 * l'instance de Ladybug
	 */
	public void updateScreenBlock(Ladybug ladybug) {
		// Suppression du point dans le ScreenBlock de lstDataBlocks
		var currentScreenBlock = ladybug.getLadybugActions().getCurrentScreenBlock();
		if (ladybug.isEatenAMegaPoint() || ladybug.isEatenAPoint()) {
			currentScreenBlock.removePoint();
			currentScreenBlock.addEatenPoint();
			// Suppression du point dans de ScreenBlock de lstViewBlocks
			viewBlocks.stream().filter(sb -> sb.getCoordinate().equals(currentScreenBlock.getCoordinate())).findFirst()
			        .orElseThrow().removePoint();
		}
		if (ladybug.hasEatenAFruit()) {
			currentScreenBlock.resetIdRefFruit();
			viewBlocks.stream().filter(sb -> sb.getCoordinate().equals(currentScreenBlock.getCoordinate())).findFirst()
			        .orElseThrow().resetIdRefFruit();

		}
	}
}
