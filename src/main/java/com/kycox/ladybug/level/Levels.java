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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Levels {
	private List<ILevel> lstLevel = new ArrayList<>();
	private Random		 random	  = new Random();

	/**
	 * Constructeur : ajoute les différents niveaux du jeu
	 */
	public Levels() {
		/*
		 * Ici on peut ajouter d'autres niveaux => Kyky aide moi !!! :)
		 */
//    lstLevel.add(new Level1());
		lstLevel.add(new Level2NG());
		lstLevel.add(new Level3NG());
//    lstLevel.add(new Level3());
	}

	/**
	 * Retourne le niveau demand�
	 *
	 * @param i : le numéro du niveau demandé
	 */
	public ILevel getLevel(int i) {
		int num = i;
		if (num > lstLevel.size())
			num = 1 + random.nextInt(lstLevel.size());
		return lstLevel.get(num - 1);
	}
}
