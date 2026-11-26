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

import com.kycox.game.body.ghost.Ghost.MoveStyle;
import com.kycox.game.body.ghost.GhostColor;
import com.kycox.game.constant.ghost.GhostStatus;
import com.kycox.game.tools.Position;

import java.util.Optional;

/**
 * Un fantôme à un battement donné (étape 4.6).
 *
 * @param name              nom (Blinky, Pinky…)
 * @param color             couleur ; la vue en déduit l'image
 * @param position          position en pixels
 * @param block             case occupée
 * @param direction         direction du déplacement (pour le bandeau)
 * @param status            statut (normal, effrayé, mourant…)
 * @param computed          vrai si l'ordinateur le joue, faux pour un joueur humain
 * @param leftLifes         vies restantes (affichées dans le bandeau pour un fantôme humain)
 * @param camouflage        vrai s'il est camouflé (touche F7)
 * @param dying             vrai s'il vient d'être mangé
 * @param toBeTeleported    vrai juste avant une téléportation
 * @param speedIndex        indice de vitesse (debug)
 * @param moveStyle         façon de se déplacer (debug)
 * @param maximumAttackDist distance d'attaque en pixels (debug)
 * @param target            case visée (debug), ou {@code null} quand le fantôme
 *                          fuit, va au hasard ou est joué par un humain
 */
public record GhostSnapshot(String name, GhostColor color, Position position, Position block, Position direction,
        GhostStatus status, boolean computed, int leftLifes, boolean camouflage, boolean dying, boolean toBeTeleported,
        int speedIndex, MoveStyle moveStyle, int maximumAttackDist, Position target) {

	/** La case visée, s'il y en a une. */
	public Optional<Position> targetBlock() {
		return Optional.ofNullable(target);
	}
}
