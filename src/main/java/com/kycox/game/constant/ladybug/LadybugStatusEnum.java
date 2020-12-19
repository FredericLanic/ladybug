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
package com.kycox.game.constant.ladybug;

/**
 * Enumération des états de ladybug
 *
 *
 */
public enum LadybugStatusEnum {
	DEAD, DYING, NORMAL;
//	public static Predicate<Ladybug> isDead() {
//		return p -> p.getStatus().equals(DEAD);
//	}
//
//	public static Predicate<Ladybug> isDying() {
//		return p -> p.getStatus().equals(DYING);
//	}
//
//	public static Predicate<Ladybug> isNormal() {
//		return p -> p.getStatus().equals(NORMAL);
//	}

	private LadybugStatusEnum() {
	}
}
