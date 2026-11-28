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

import java.util.List;

/**
 * La photo du salon (étape 20.3) : une ligne par joueur (personnage et
 * commandes), la ligne et la colonne actives. L'écran du salon ne lit que cette
 * photo ; demain, un poste du réseau l'affichera de la même façon (étape 9).
 *
 * @param rows         les lignes, dans l'ordre
 * @param activeRow    la ligne active (surlignée)
 * @param activeColumn {@code CHARACTER} ou {@code CONTROLS}
 * @param ready        la partie peut partir (Cox a un joueur, chaque ligne a ses commandes)
 */
public record LobbySnapshot(List<RowSnapshot> rows, int activeRow, String activeColumn, boolean ready) {

	/**
	 * Une ligne : le personnage ({@code null} : pas de joueur), les commandes (nom de
	 * {@code InputSource}, par exemple {@code XBOX_PAD_2} ; {@code null} si pas
	 * encore choisies) et l'attente d'une manette (« appuie sur A »).
	 */
	public record RowSnapshot(String character, String device, boolean waitingForPad) {
	}

	public LobbySnapshot {
		rows = List.copyOf(rows);
	}

	/** Pas de salon : hors de l'écran de configuration. */
	public static LobbySnapshot empty() {
		return new LobbySnapshot(List.of(), 0, "CHARACTER", false);
	}
}
