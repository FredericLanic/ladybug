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
package com.kycox.game.engine.maze;

import com.kycox.game.tools.Position;
import com.kycox.game.body.ladybug.Ladybug;
import com.kycox.game.constant.contract.LevelStructure;
import com.kycox.game.engine.maze.utils.CheckScreenBlockBorders;
import com.kycox.game.tools.Utils;
import com.kycox.game.tools.dijkstra.UnitDijkstra;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Le labyrinthe du niveau en cours, vu par les règles du jeu : les cases, les
 * gommes, les fruits, les téléportations et les positions de départ.
 *
 * <p>Étape 4.4 : ancien {@code ScreenData}, découpé par un « Extract Class ».
 * Il ne connaît plus ni les pixels ni la copie des cases servant au dessin des
 * murs : c'est {@code MazeOutline}, côté vue, qui la construit. Voir
 * {@code doc/patterns/extract-class.md}. Depuis 4.5, le mode lampe et le style
 * de dessin sont dans {@code LevelState}.</p>
 */
@Component
public final class Maze {
	@Getter
	private LevelStructure currentLevel;
	private List<ScreenBlock> dataScreenBlocks = new ArrayList<>();
	private int initNbrBlocksWithPoint = 0;
	private final ManageLevel manageLevel;

	/**
	 * Numéro de « forme » du labyrinthe : il augmente à chaque niveau préparé. La
	 * vue s'en sert pour savoir quand reconstruire le contour des murs.
	 */
	@Getter
	private int layoutVersion = 0;

	public Maze(ManageLevel manageLevel) {
		this.manageLevel = manageLevel;
	}

	public void addNewFruit(int idRefFruit) {
		var currentScreenBlock = dataScreenBlocks.get(getRandomPosNumEatenPoint());
		currentScreenBlock.setIdRefFruit(idRefFruit);
	}

	public List<UnitDijkstra> convertToDjistraList() {
		List<UnitDijkstra> lstUnitDijkstra = new ArrayList<>();
		dataScreenBlocks.forEach(dataScreenBlock -> lstUnitDijkstra.add(new UnitDijkstra(dataScreenBlock)));
		return lstUnitDijkstra;
	}

	/**
	 * Les cases du niveau, en lecture seule (la vue en tire le contour des murs).
	 */
	public List<ScreenBlock> getScreenBlocks() {
		return Collections.unmodifiableList(dataScreenBlocks);
	}

	public Position getInitLadybugPos() {
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
		return IntStream.range(0, dataScreenBlocks.size()).filter(i -> dataScreenBlocks.get(i).isEatenPoint())
				.limit(numPoint)
				.max()
				.orElse(0);
	}

	private int getPosNumPoint(int numPoint) {
		return IntStream.range(0, dataScreenBlocks.size()).filter(i -> dataScreenBlocks.get(i).isPoint())
				.limit(numPoint)
				.max()
				.orElse(0);
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

	public Position getRandomPosOnAPoint() {
		var pos = getRandomPosNumPoint();
		var yBlock = pos / currentLevel.getNbrBlocksByLine();
		var xBlock = pos % currentLevel.getNbrBlocksByLine();
		return new Position(xBlock, yBlock);
	}

	public Position getRevivorGhostPos() {
		return Utils.convertBlockPointToGraphicPoint(currentLevel.getGhostRegenerateBlockPoint());
	}

	public ScreenBlock getScreenBlock(Position posPoint) {
		return dataScreenBlocks.stream().filter(b -> b.getCoordinate().equals(posPoint)).findFirst().orElse(null);
	}

	public void setLevelMap(int numLevel, boolean isInGame) {
   		// récupération du level associé au numLevel
		currentLevel = manageLevel.getLevel(numLevel);
		// Création des blocks
		dataScreenBlocks = currentLevel.getLstBlocks();
		// vérification des bordures des blocks
		var checkScreenBlockBorders = new CheckScreenBlockBorders(this);
		checkScreenBlockBorders.checkDataBlockBorder();
		dataScreenBlocks.stream()
				.filter(b -> b.getCoordinate().equals(currentLevel.getGhostRegenerateBlockPoint()))
		        .forEach(ScreenBlock::addGhostReviver);
		if (isInGame) {
			// ajout des mega points aléatoires
			for (var i = 0; i < currentLevel.getNbrMegaPoints(); i++) {
				var randomPoint = getRandomPosOnAPoint();
				dataScreenBlocks.stream()
						.filter(b -> b.getCoordinate().equals(randomPoint))
				        .forEach(ScreenBlock::addMegaPoint);
			}
			// ajoute des points de téléportation s'il existe dans le level
			var teleportPoints = currentLevel.getTeleportPoints();
			for (Map.Entry<Position, Position> m : teleportPoints.entrySet()) {
				dataScreenBlocks.stream()
						.filter(b -> b.getCoordinate().equals(m.getKey()))
				        .forEach(sb -> sb.addTeleportation(m.getValue()));
			}
		}
		initNbrBlocksWithPoint = getNbrBlocksWithPoint();
		layoutVersion++;
	}

	public void updateScreenBlock(Ladybug ladybug) {
		// Suppression du point (ou du fruit) dans la case mangée
		var currentScreenBlock = ladybug.getLadybugActions().getCurrentScreenBlock();
		if (ladybug.isEatenAMegaPoint() || ladybug.isEatenAPoint()) {
			currentScreenBlock.removePoint();
			currentScreenBlock.addEatenPoint();
		}
		if (ladybug.hasEatenAFruit()) {
			currentScreenBlock.resetIdRefFruit();
		}
	}
}
