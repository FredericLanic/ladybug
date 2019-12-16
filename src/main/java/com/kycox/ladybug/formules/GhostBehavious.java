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
package com.kycox.ladybug.formules;

import java.util.Random;

import com.kycox.ladybug.constant.Constants;

/**
 * Probabilité de comportement
 *
 * Fonction y = ax + b où x est le niveau
 *
 * Qui r�soud : (1, 20); (nbrMaxNiveau, 90)
 *
 * Retourne vrai si le nombre aléatoire (0-100) est en dessous de y
 *
 */
public class GhostBehavious {

  private double a               = 0;
  private double b               = 0;

  // �chelle de 1 � 10
  private double limiteBasse     = 20;
  private double limiteHaute     = 90;

  private Random randomGenerator = new Random();

  private int    scale           = 100;

  private double y               = 0;

  /**
   * Constructeur privé
   *
   */
  public GhostBehavious(int numLevel) {
    a = (limiteHaute - limiteBasse) / (Constants.NIVEAU_MAX - 1);
    b = limiteBasse - a;

    y = (a * numLevel + b);
  }

  /**
   *
   * Créé un nombre al�atoire [0-100] et définie si ce nombre est inférieur à une règle spécifique
   * [limiteBasse, limiteHaute]
   *
   * @param numLevel
   * @return
   */
  public boolean isActive() {
    return (randomGenerator.nextInt(scale) + 1) < y;
  }

}
