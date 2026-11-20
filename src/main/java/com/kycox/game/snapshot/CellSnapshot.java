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
package com.kycox.game.snapshot;

import com.kycox.game.tools.Position;

/**
 * Une case du labyrinthe à un battement donné (étape 4.6) : sa forme (bordures,
 * mur) et son contenu (gomme, fruit…).
 *
 * @param coordinate    position de la case
 * @param borderLeft    bordure à gauche
 * @param borderUp      bordure en haut
 * @param borderRight   bordure à droite
 * @param borderDown    bordure en bas
 * @param notAccessible case de mur, où personne ne passe
 * @param point         gomme pas encore mangée
 * @param megaPoint     la gomme est une méga-gomme
 * @param ghostReviver  point de résurrection des fantômes
 * @param teleportation point de téléportation
 * @param idRefFruit    fruit présent ({@code GameMainConstants.NOFRUITID} si aucun)
 */
public record CellSnapshot(Position coordinate, boolean borderLeft, boolean borderUp, boolean borderRight,
        boolean borderDown, boolean notAccessible, boolean point, boolean megaPoint, boolean ghostReviver,
        boolean teleportation, int idRefFruit) {
}
