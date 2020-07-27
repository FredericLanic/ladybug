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
package com.kycox.ladybug.score;

import java.awt.Point;

import com.kycox.ladybug.timer.IncrementScoreTimer;

/**
 * Contenu des valeurs afficées dans la map, lorsque ladybug fait des exploits
 *
 * @author kycox
 *
 */
public class IncrementScore {
  private boolean             dying;

  /** Position dans la fenêtre où le score est affiché */
  private Point               position;

  /** Timer d'affichage du score dans la fenêtre */
  private IncrementScoreTimer scoreTimer;

  /** Valeurà� afficher */
  private String              value;

  /**
   * Constructeur
   *
   * @param position : position d'affichage du score
   * @param value    : valeur à afficher
   */
  public IncrementScore(Point position, String value) {
    this.position = position;
    this.value = value;
    this.setDying(false);
    scoreTimer = new IncrementScoreTimer(this);
    scoreTimer.launch(1000);
  }

  /**
   * Retourne la position
   *
   * @return
   */
  public Point getPosition() {
    return position;
  }

  /** Retourne la valeur du score */
  public String getValue() {
    return value;
  }

  /**
   * Vérifie si l'objet est en train de mourir
   *
   * @return
   */
  public boolean isDying() {
    return dying;
  }

  /**
   * Affecte à l'objet le fait qu'il va disparaitre
   *
   * @param dying
   */
  public void setDying(boolean dying) {
    this.dying = dying;
  }
}
