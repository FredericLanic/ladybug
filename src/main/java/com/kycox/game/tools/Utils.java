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
package com.kycox.game.tools;

import java.awt.Point;
import java.security.SecureRandom;

import com.kycox.game.constant.Constants;

public final class Utils {
	private static SecureRandom secureRandom = new SecureRandom();

	/**
	 * Converti un point du graphique en un block
	 *
	 * @param point
	 * @return
	 */
	public static Point convertPointToBlockUnit(Point point) {
		return new Point(point.x / Constants.BLOCK_SIZE, point.y / Constants.BLOCK_SIZE);
	}

	/**
	 * Converti un block en un point du graphique
	 *
	 * @param point
	 * @return
	 */
	public static Point convertPointToGraphicUnit(Point point) {
		return new Point(point.x * Constants.BLOCK_SIZE, point.y * Constants.BLOCK_SIZE);
	}

	public static int generateRandomInt(int max) {
		return secureRandom.nextInt(max);
	}

	// origin : https://algorithms.tutorialhorizon.com/convert-integer-to-roman/
	public static String integerToRoman(int num) {
		int[] values = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };
		String[] romanLiterals = { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };
		var roman = new StringBuilder();
		for (var i = 0; i < values.length; i++) {
			while (num >= values[i]) {
				num -= values[i];
				roman.append(romanLiterals[i]);
			}
		}
		return roman.toString();
	}

	/**
	 * Constructeur privé
	 */
	private Utils() {
	}
}
