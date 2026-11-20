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
package com.kycox.game.input;

import com.kycox.game.body.GhostsGroup;
import com.kycox.game.body.ladybug.Ladybug;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Où trouver la boîte aux lettres de chaque joueur (étape 5). Les contrôleurs
 * (clavier, manette) et les tests passent par ici pour donner leurs ordres,
 * sans connaître le modèle du jeu.
 */
@Component
public class PlayerInputs {
	private final Ladybug ladybug;
	private final GhostsGroup ghostsGroup;

	public PlayerInputs(Ladybug ladybug, GhostsGroup ghostsGroup) {
		this.ladybug = ladybug;
		this.ghostsGroup = ghostsGroup;
	}

	/** Les ordres donnés à Ladybug. */
	public PlayerInput ladybug() {
		return ladybug.getPlayerInput();
	}

	/**
	 * Les ordres donnés au fantôme joué par le deuxième joueur, s'il y en a un
	 * (étape 5.2). En solo, il n'y en a pas : les touches ZQSD ne font rien.
	 */
	public Optional<PlayerInput> humanGhost() {
		return ghostsGroup.getHumanGhosts().stream().findFirst().map(ghost -> ghost.getPlayerInput());
	}
}
