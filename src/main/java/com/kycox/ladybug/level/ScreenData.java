/**
  Copyright (C) 2019  Frédéric Lanic frederic.lanic@outlook.fr

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
package com.kycox.ladybug.level;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Named;

import com.kycox.ladybug.body.ladybug.Ladybug;
import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.tools.Utils;
import com.kycox.ladybug.tools.dijkstra.UnitDijkstra;

import lombok.Getter;

/**
 * La map du jeu en cours
 *
 */
@Named("ScreenData")
public final class ScreenData {
	@Getter
	private ILevel			  currentLevel;
	@Inject
	private Levels			  gameLevels;
	private int				  initNbrBlocksWithPoint = 0;
	private List<ScreenBlock> lstDataBlocks			 = new ArrayList<>();
	@Getter
	private List<ScreenBlock> lstViewBlocks;

	/**
	 * Convert le ScreenData en une List<UnitDijkstra>
	 *
	 * @return
	 */
	public List<UnitDijkstra> convertToDjistraList() {
		List<UnitDijkstra> lstUnitDijkstra = new ArrayList<>();
		// java8 : faire un stream
		for (ScreenBlock block : lstDataBlocks) {
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
		return lstDataBlocks.stream().filter(b -> b.getCoordinate().equals(posPoint)).findFirst().orElse(null);
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
	 * Retourne le nombre de points à manger par ladybug présents dans le IData
	 */
	public int getNbrBlocksWithPoint() {
		return (int) lstDataBlocks.stream().filter(ScreenBlock::isPoint).count();
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

	/**
	 * Retourne les coordonnées aléatoire GRAPHIQUE d'un block qui contient un point
	 * à manger par ladybug
	 *
	 * @return
	 */
	public Point getRandomPosOnAPoint() {
		int	pos	   = getRandomPosNumPoint();
		int	yBlock = pos / currentLevel.getNbrBlocksByLine();
		int	xBlock = pos % currentLevel.getNbrBlocksByLine();
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
		return lstViewBlocks.stream().filter(b -> b.getCoordinate().equals(posPoint)).findFirst().orElse(null);
	}

	/***
	 * Affecte la map en fonction du numero de niveau
	 *
	 * @param numLevel
	 */
	public void setLevelMap(int numLevel, boolean isInGame) {
		// récupération du level associé au numLevel
		currentLevel = gameLevels.getLevel(numLevel);
		// Création des blocks
		lstDataBlocks = currentLevel.getLstBlocks();
		// vérification des bordure des blocks
		CheckScreenBlockBorders checkScreenBlockBorders = new CheckScreenBlockBorders(this);
		checkScreenBlockBorders.checkDataBlockBorder();
		lstDataBlocks.stream().filter(b -> b.getCoordinate().equals(currentLevel.getGhostRegenerateBlockPoint()))
		        .forEach(ScreenBlock::addGhostReviver);
		// ajout des mega points aléatoires
		if (isInGame) {
			for (int i = 0; i < currentLevel.getNbrMegaPoints(); i++) {
				Point randomPoint = getRandomPosOnAPoint();
				lstDataBlocks.stream().filter(b -> b.getCoordinate().equals(randomPoint))
				        .forEach(ScreenBlock::addMegaPoint);
			}
			// @FIXME : Ajout Téléportation pour test
			Point randomPoint = getRandomPosOnAPoint();
			lstDataBlocks.stream().filter(b -> b.getCoordinate().equals(randomPoint))
			        .forEach(ScreenBlock::addTeleportation);
		}
		lstViewBlocks = new ArrayList<>();
		// on clone la liste
		lstDataBlocks.stream().forEach(sb -> lstViewBlocks.add((ScreenBlock) sb.clone()));
		checkScreenBlockBorders.checkViewBlockBorder();
		initNbrBlocksWithPoint = getNbrBlocksWithPoint();
	}

	/**
	 * Update Point
	 *
	 * FIXME : à mettre ailleurs
	 */
	public void updateScreenBlock(Ladybug ladybug) {
		if (ladybug.isEatenAMegaPoint() || ladybug.isEatenAPoint()) {
			// Suppression du point dans le ScreenBlock de lstDataBlocks
			ScreenBlock currentScreenBlock = ladybug.getLadybugActions().getCurrentScreenBlock();
			// ladybugActions.getCurrentScreenBlock();
			currentScreenBlock.removePoint();
			// Suppression du point dans de ScreenBlock de lstViewBlocks
			lstViewBlocks.stream().filter(sb -> sb.getCoordinate().equals(currentScreenBlock.getCoordinate()))
			        .findFirst().orElse(null).removePoint();
		}
	}

	private int getPosNumPoint(int numPoint) {
		int	nbrPoint = 0;
		int	pos		 = 0;
		for (int i = 0; i < lstDataBlocks.size(); i++) {
			if (lstDataBlocks.get(i).isPoint()) {
				nbrPoint++;
				if (nbrPoint == numPoint)
					pos = i;
			}
		}
		return pos;
	}

	private int getRandomPosNumPoint() {
		int	nbrPoints	= getNbrBlocksWithPoint();
		int	randomPoint	= new Random().nextInt(nbrPoints)+1;
		return getPosNumPoint(randomPoint);
	}
}
