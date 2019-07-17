package com.kycox.ladybug.level;

import java.awt.Point;

import com.kycox.ladybug.constant.Constants;

/**
 * Class ScreenBlock Correspond à une case dans le ScreenData
 *
 */
public class ScreenBlock {
  private static final int DOUBLE_LINES   = 128;
  private static final int DOWN           = 8;
  private static final int LEFT           = 1;
  private static final int MEGA_POINT     = 32;
  private static final int POINT          = 16;
  private static final int RIGHT          = 4;
  private static final int SURVIVOR_POINT = 64;
  private static final int UP             = 2;
  private int              content;

  private Point            coordinate     = Constants.POINT_ZERO;

  /**
   * Constructeur
   * 
   * @param content
   */
  public ScreenBlock(int content) {
    this.content = content;
  }

  public void addDown() {
    content |= DOWN;
  }

  public void addLeft() {
    content |= LEFT;

  }

  public void addRight() {
    content |= RIGHT;

  }

  public void addSurvivorPoint() {
    content |= SURVIVOR_POINT;
  }

  public void addUp() {
    content |= UP;
  }

  public int getContent() {
    return content;
  }

  public Point getCoordinate() {
    return coordinate;
  }

// Mis en commentaire; l'idée est bonne; 
// je ne souhaite pas actuellement mettre en public les différentes valeurs static de la classe.
//  /**
//   * Bordure en bas
//   * 
//   * @return
//   */
//  public boolean is(int check) {
//    return (content & check) != 0;
//  }

  public boolean isDoubleLines() {
    return (content & DOUBLE_LINES) != 0;
  }

  /**
   * Bordure en bas
   * 
   * @return
   */
  public boolean isDown() {
    return (content & DOWN) != 0;
  }

  /**
   * Bordure à gauche
   * 
   * @return
   */
  public boolean isLeft() {
    return (content & LEFT) != 0;
  }

  /**
   * Contient un méga point
   * 
   * @return
   */
  public boolean isMegaPoint() {
    return (content & MEGA_POINT) != 0;
  }

  /**
   * Contient un point
   * 
   * @return
   */
  public boolean isPoint() {
    return (content & POINT) != 0;
  }

  /**
   * Contient un point de revie des fantômes
   * 
   * @return
   */
  public boolean isReviverGhostPoint() {
    return (content & SURVIVOR_POINT) != 0;
  }

  /**
   * Bordure droite
   * 
   * @return
   */
  public boolean isRight() {
    return (content & RIGHT) != 0;
  }

  public boolean isSurvivorGhost() {
    return (content & SURVIVOR_POINT) != 0;
  }

  /**
   * Bordure en haut
   * 
   * @return
   */
  public boolean isUp() {
    return (content & UP) != 0;
  }

  /**
   * Suppression du point dans le block
   */
  public void removePoint() {
    content &= ~(POINT | MEGA_POINT);
  }

  public void setContent(int content) {
    this.content = content;
  }

  public void setCoordinate(Point coordinate) {
    this.coordinate = coordinate;
  }

  public void setMegaPoint() {
    content |= MEGA_POINT;
  }
}
