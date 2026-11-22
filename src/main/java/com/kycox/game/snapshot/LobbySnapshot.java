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
 * La photo du salon (étape 20.2) : les joueurs qui ont rejoint, leur fantôme,
 * validé ou non. L'écran « rejoindre » (20.3) ne lit que cette photo.
 *
 * @param maxSeats places possibles (3 au plus, C52)
 * @param seats    les places prises, dans l'ordre d'arrivée
 */
public record LobbySnapshot(int maxSeats, List<SeatSnapshot> seats) {

	/**
	 * Une place : l'appareil (nom de {@code InputSource}, par exemple
	 * {@code KEYBOARD_ZQSD}), le fantôme choisi, validé ou non.
	 */
	public record SeatSnapshot(String device, String ghost, boolean confirmed) {
	}

	public LobbySnapshot {
		seats = List.copyOf(seats);
	}

	/** Un salon vide : partie solo, ou personne n'a encore rejoint. */
	public static LobbySnapshot empty() {
		return new LobbySnapshot(0, List.of());
	}
}
