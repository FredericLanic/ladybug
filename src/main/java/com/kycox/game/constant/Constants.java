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
package com.kycox.game.constant;

import java.awt.Point;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Constantes utilisées pour le jeu
 *
 * @author kycox
 *
 */
public final class Constants {
	// taille d'un block
	public static final int BLOCK_SIZE = 48;
	// distance ou le fantôme fuit Ladybug
	public static final int DISTANCE_GHOST_SCARED = 100;
	// distance min entre Ladybug et un fantôme => provoque la mort de Ladybug
	public static final int DISTANCE_LADYBUG_DEATH = 20;
	// nombre de vie au début du jeu
	public static final int NBR_INIT_LIFE = 3;
	// nombre de secondes de super pouvoir de pacman
	public static final int NBR_SECONDS_SUPER_POWER = 15;
	// Nouvelle vie en fonction du score incrémental
	public static final int NEW_LIFE_BY_SCORE = 100;
	// Niveau max
	public static final int NIVEAU_MAX = 30;
	// Période du timer du jeu
	public static final int PACE = 40; // rythme en anglais :/
	// Point en bas
	public static final Point POINT_DOWN = new Point(0, 1);
	// Point à gauche
	public static final Point POINT_LEFT = new Point(-1, 0);
	// Point à droite
	public static final Point POINT_RIGHT = new Point(1, 0);
	// Point en haut
	public static final Point POINT_UP = new Point(0, -1);
	// Point neutre
	public static final Point POINT_ZERO = new Point(0, 0);
	// Point neutre
	public static final Point POINT_OUTSIDE = new Point(-1, -1);
	// niveau pour la fenêtre de présentation
	public static final int PRESENTATION_LEVEL = 2;
	// Score quand un fantôme est mangé
	public static final int SCORE_EATEN_GHOST = 15;
	// Score quand le niveau est terminé
	public static final int SCORE_END_LEVEL = 50;
	// Score quand Ladybug mange un super point
	public static final int SCORE_MEGA_POINT = 10;
	// Score quand Ladybug mange un point simpe
	public static final int SCORE_SIMPLE_POINT = 1;
	// Tableau des vitesses disponibles : les vitesses doivent être un multiple de
	// 24 (taille d'un BLOCK_SIZE) pour que le programme puisse détecter le
	// changement de ScreenBlock
	public static final List<Integer> VALID_SPEEDS = Collections
	        .unmodifiableList(Arrays.asList(1, 2, 3, 4, 6, 8, 12, 24));

	private Constants() {
	}
}
