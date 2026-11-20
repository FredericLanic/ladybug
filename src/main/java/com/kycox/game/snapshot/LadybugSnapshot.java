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

import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.tools.Position;

/**
 * Ladybug à un battement donné (étape 4.6).
 *
 * @param name            nom affiché
 * @param position        position en pixels
 * @param block           case occupée
 * @param viewDirection   direction du regard (pour l'image)
 * @param status          statut (normal, mourante, morte…)
 * @param leftLifes       vies restantes
 * @param newLife         vrai au battement où une vie est gagnée
 * @param speedIndex      indice de vitesse
 * @param eatenAMegaPoint vrai au battement où une méga-gomme est mangée
 * @param toBeTeleported  vrai juste avant une téléportation
 * @param agonyDuration   durée de l'animation de mort, en millisecondes
 */
public record LadybugSnapshot(String name, Position position, Position block, Position viewDirection,
        LadybugStatus status, int leftLifes, boolean newLife, int speedIndex, boolean eatenAMegaPoint,
        boolean toBeTeleported, long agonyDuration) {
}
