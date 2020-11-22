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
package com.kycox.ladybug.constant;

import java.net.URL;

import lombok.Getter;

/**
 * Enumération des sons à écouter dans le jeu
 *
 */
public enum SoundsEnum {
	COMMON_TELEPORT(Math.pow(2, 14), "common_teleport.wav"),
	GAME_BEGIN_LEVEL(Math.pow(2, 0), "game_begin_level.wav"), GAME_SIREN_0(Math.pow(2, 10), "game_siren_0.wav"),
	GAME_SIREN_1(Math.pow(2, 11), "game_siren_1.wav"), GAME_SIREN_2(Math.pow(2, 12), "game_siren_2.wav"),
	GAME_SIREN_3(Math.pow(2, 13), "game_siren_3.wav"), GHOST_EATEN(Math.pow(2, 9), "ghost_eaten.wav"),
	GHOST_REGENERATE(Math.pow(2, 8), "ghost_regenerate.wav"), GHOST_SURVIVOR(Math.pow(2, 7), "ghost_survivor.wav"),
	LADYBUG_CHOMP(Math.pow(2, 1), "ladybug_chomp.wav"), LADYBUG_EAT_FRUIT(Math.pow(2, 3), "ladybug_eatfruit.wav"),
	LADYBUG_EAT_GHOST(Math.pow(2, 4), "ladybug_eatghost.wav"),
	LADYBUG_EXTRA_PAC(Math.pow(2, 5), "ladybug_extralife.wav"),
	LADYBUG_INTERMISSION(Math.pow(2, 6), "ladybug_intermission.wav"),
	LADYBUG_IS_DYING(Math.pow(2, 2), "ladybug_death.wav");

	@Getter
	private int	index = 0;
	@Getter
	private URL	url	  = null;

	/**
	 * Constructeur
	 *
	 * @param fileName
	 */
	private SoundsEnum(double index, String fileName) {
		this.index = (int) index;
		this.url   = SoundsEnum.class.getClassLoader().getResource("sound/wav/" + fileName);
	}
}
