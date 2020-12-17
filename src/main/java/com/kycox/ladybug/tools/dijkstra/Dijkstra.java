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
package com.kycox.ladybug.tools.dijkstra;

import java.awt.Point;
import java.util.List;

import com.kycox.ladybug.level.ScreenBlock;
import com.kycox.ladybug.level.ScreenData;

import lombok.Getter;

/**
 * Algorithme Dijkstra pour trouver un plus court chemin dans un ScreenData
 *
 */
public class Dijkstra {
	/**
	 * Retourne une liste de point en coordonnées (unité block) pour aller du point
	 * A au point B
	 *
	 * @param screenData      : jeu en cours
	 * @param startCoordinate : coordonnées de départ
	 * @param endCoordinate   : coordonnées d'arrivée
	 * @return List<Point>
	 */
	public static List<Point> getShorterWay(Point startCoordinate, Point endCoordinate, ScreenData screenData) {
		List<Point>	shorterWay;
		Dijkstra	dijkstra = new Dijkstra(screenData);
		dijkstra.init(startCoordinate, screenData);
		// Lancement de la recherche
		dijkstra.search(startCoordinate, null, screenData);
		// Récupération de l'unité final
		int			 idx			= dijkstra.getPos(endCoordinate, screenData);
		UnitDijkstra unitDijstraEnd	= dijkstra.getListUnitDijkstra().get(idx);
		// Récupération du chemin le plus court
		shorterWay = unitDijstraEnd.getShorterWay();
		// FIXME : peut être des vérifications ? / capture d'exceptions au cas de Level
		// non valide ?
		return shorterWay;
	}

	@Getter
	private List<UnitDijkstra> listUnitDijkstra;
	int						   nbrAccessibleBlocks;

	/**
	 * Calcul du parcours le plus court en partant de startCoordonnate (coordonnées
	 * de Block) dans un Level de ladybug IMPORTANT : On part du principe que le
	 * level a été vérifié : - tous les blocks ont les bonnes bordures
	 * INDIVIDUELLEMENT
	 *
	 * @param screenData      : jeu en cours
	 * @param startCoordinate : coordonnées de départ (unité : le block du
	 *                        screenData)
	 */
	private Dijkstra(ScreenData screenData) {
		// Création de la list des unités Dijkstra
		listUnitDijkstra = screenData.convertToDjistraList();
	}

	/**
	 * Retourne l'index de listUnitDijkstra en fonction des coordonnées
	 *
	 * @param coordinate (unité x et y : block du genericData) /!\ intimmement lié
	 *                   au genericData
	 * @return
	 */
	private int getPos(Point coordinate, ScreenData screenData) {
		return coordinate.x + coordinate.y * screenData.getCurrentLevel().getNbrBlocksByLine();
	}

	/*
	 * Initialisation de Dijkstra
	 */
	private void init(Point startCoordinate, ScreenData screenData) {
		// Initialisation du block de début
		int posStart = getPos(startCoordinate, screenData);
		listUnitDijkstra.get(posStart).setDistance(0);
	}

	/**
	 * Recherche (méthode récursice)
	 *
	 * @param currentCoordinate    : coordonnées à gérer (unité x et y : block du
	 *                             genericData)
	 * @param previousUnitDijkstra
	 */
	private void search(Point currentCoordinate, UnitDijkstra previousUnitDijkstra, ScreenData screenData) {
		// on rcupère la position
		int currentPos = getPos(currentCoordinate, screenData);
		// on initialise le flag weightHasChanged
		boolean		 weightHasChanged	 = false;
		UnitDijkstra currentUnitDijkstra = listUnitDijkstra.get(currentPos);
		// on pose le poids
		if (previousUnitDijkstra == null) { // toute première case
			currentUnitDijkstra.setDistance(0);
			currentUnitDijkstra.setCoordinate(currentCoordinate);
			currentUnitDijkstra.setPreviousUnitDijkstra(previousUnitDijkstra);
			weightHasChanged = true;
			// on doit faire les cases autour
		} else if (previousUnitDijkstra.getDistance() + 1 < currentUnitDijkstra.getDistance()) {
			// je pense qu'il y a une erreur dans la condition
			currentUnitDijkstra.setDistance(previousUnitDijkstra.getDistance() + 1);
			currentUnitDijkstra.setCoordinate(currentCoordinate);
			currentUnitDijkstra.setPreviousUnitDijkstra(previousUnitDijkstra);
			weightHasChanged = true;
		}
		// On récupère le Block courant
		ScreenBlock screenBlockCurrent = currentUnitDijkstra.getScreenBlock();
		// Définition des prochains points
		Point coordinateUp	  = new Point(currentCoordinate.x, currentCoordinate.y - 1);
		Point coordinateLeft  = new Point(currentCoordinate.x - 1, currentCoordinate.y);
		Point coordinateRight = new Point(currentCoordinate.x + 1, currentCoordinate.y);
		Point coordinateDown  = new Point(currentCoordinate.x, currentCoordinate.y + 1);
		// Lancement de la recherche en haut quand c'est possible
		if (weightHasChanged && !screenBlockCurrent.isBorderUp()) {
			search(coordinateUp, currentUnitDijkstra, screenData);
		}
		// Lancement de la recherche à gauche quand c'est possible
		if (weightHasChanged && !screenBlockCurrent.isBorderLeft()) {
			search(coordinateLeft, currentUnitDijkstra, screenData);
		}
		// Lancement de la recherche à droite quand c'est possible
		if (weightHasChanged && !screenBlockCurrent.isBorderRight()) {
			search(coordinateRight, currentUnitDijkstra, screenData);
		}
		// Lancement de la recherche en bas quand c'est possible
		if (weightHasChanged && !screenBlockCurrent.isBorderDown()) {
			search(coordinateDown, currentUnitDijkstra, screenData);
		}
	}
}
