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

import com.kycox.ladybug.constant.Constants;

/**
 * Class ScreenBlock correspond à une case dans le ScreenData
 *
 */
public class ScreenBlock implements Cloneable {
  private static final int DOWN           = 8;
  private static final int LEFT           = 1;
  private static final int MEGA_POINT     = 32;
  private static final int POINT          = 16;
  private static final int RIGHT          = 4;
  private static final int SURVIVOR_POINT = 64;
  private static final int UP             = 2;
  static final int         NOT_ACCESSIBLE = 128;
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

  /**
   * Clone l'objet en cours
   */
  @Override
  public Object clone() {
    Object o = null;
    try {
      o = super.clone();
    } catch (CloneNotSupportedException cnse) {
      cnse.printStackTrace(System.err);
    }
    return o;
  }

  public int getContent() {
    return content;
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

  public Point getCoordinate() {
    return coordinate;
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
   * Bordure � gauche
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

  public boolean isNotAccessible() {
    return (content & NOT_ACCESSIBLE) != 0;
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

  public void removeDown() {
    content &= ~(DOWN);
  }

  public void removeLeft() {
    content &= ~(LEFT);
  }

  /**
   * Suppression du point dans le block
   */
  public void removePoint() {
    content &= ~(POINT | MEGA_POINT);
  }

  public void removeRight() {
    content &= ~(RIGHT);
  }

  public void removeUp() {
    content &= ~(UP);
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
