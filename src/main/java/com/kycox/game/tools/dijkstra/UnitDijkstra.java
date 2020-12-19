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
package com.kycox.game.tools.dijkstra;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.kycox.game.level.ScreenBlock;

import lombok.Getter;
import lombok.Setter;

/**
 * Valeur unitaire lors du parcours de l'algorithme Dijkstra
 *
 */
public class UnitDijkstra {
	@Getter
	@Setter
	private Point coordinate;
	/**
	 * Valeurs des poids : 999_999_999 : jamais calculé à : initialisation
	 */
	@Getter
	@Setter
	private int	  distance;
	@Getter
	@Setter
	UnitDijkstra  previousUnitDijkstra;
	@Getter
	@Setter
	ScreenBlock	  screenBlock;

	/**
	 * Constructeur
	 *
	 * @param screenBlock
	 */
	public UnitDijkstra(ScreenBlock screenBlock) {
		this.distance		 = 999_999_999;
		this.screenBlock	 = screenBlock;
		previousUnitDijkstra = null;
	}

	/**
	 * D�tricote le résultat obtenu
	 *
	 * @return une liste de point � suivre pour aller du point A au point B (unités
	 *         GRAPHIQUE)
	 */
	public List<Point> getShorterWay() {
		List<Point> lstPoint = new ArrayList<>();
		if (getDistance() != 0) {
			lstPoint.addAll(previousUnitDijkstra.getShorterWay());
		}
		lstPoint.add(coordinate);
		return lstPoint;
	}

	/**
	 * Affichage du chemin remontant depuis cet unité
	 */
	@Override
	public String toString() {
		String toString = "coord: (" + coordinate.x + "," + coordinate.y + ")\n";
		if (getPreviousUnitDijkstra() != null) {
			toString += getPreviousUnitDijkstra().toString();
		}
		return toString;
	}
}
