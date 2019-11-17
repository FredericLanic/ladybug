package com.kycox.ladybug.constant;

import java.awt.Point;

/**
 * Constantes utilisées pour le jeu
 *
 * @author kycox
 *
 */
public final class Constants {
  // taille d'un block
  public static final int   BLOCK_SIZE         = 48;
  // distance min entre Pacman et un fantôme
  public static final int   DISTANCE_MIN       = 20;
  // Nouvelle vie en fonction du score incrémental
  public static final int   NEW_LIFE_BY_SCORE  = 100;
  // Niveau max
  public static final int   NIVEAU_MAX         = 10;
  public static final Point NO_POINT           = new Point(-1, -1);
  // Période du timer du jeu
  public static final int   PACE               = 25;               // rythme en anglais :/
  // Point en bas
  public static final Point POINT_DOWN         = new Point(0, 1);
  // Point à gauche
  public static final Point POINT_LEFT         = new Point(-1, 0);
  // Point à droite
  public static final Point POINT_RIGHT        = new Point(1, 0);
  // Point en haut
  public static final Point POINT_UP           = new Point(0, -1);
  // Point neutre
  public static final Point POINT_ZERO         = new Point(0, 0);
  // Score quand un fantôme est mangé
  public static final int   SCORE_EATEN_GHOST  = 15;
  // Score quand le niveau est terminé
  public static final int   SCORE_END_LEVEL    = 50;
  // Score quand Pacman mange un super point
  public static final int   SCORE_MEGA_POINT   = 10;
  // Score quand Pacman mange un point simpe
  public static final int   SCORE_SIMPLE_POINT = 1;

  private Constants() {
  }
}
