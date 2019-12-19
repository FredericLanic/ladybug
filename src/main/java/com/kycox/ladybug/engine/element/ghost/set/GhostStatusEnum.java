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
package com.kycox.ladybug.engine.element.ghost.set;

import java.util.function.Predicate;

import com.kycox.ladybug.engine.element.ghost.Ghost;

/**
 * Enumération des états des fantômes
 *
 */
public enum GhostStatusEnum {
  DYING, FLASH, NORMAL, REGENERATING, SCARED;

  public static Predicate<Ghost> isDying() {
    return g -> g.getStatus().equals(GhostStatusEnum.DYING);
  }

  public static Predicate<Ghost> isFlashing() {
    return g -> g.getStatus().equals(GhostStatusEnum.FLASH);
  }

  public static Predicate<Ghost> isNormal() {
    return g -> g.getStatus().equals(GhostStatusEnum.NORMAL);
  }

  public static Predicate<Ghost> isRegenerating() {
    return g -> g.getStatus().equals(GhostStatusEnum.REGENERATING);
  }

  public static Predicate<Ghost> isScared() {
    return g -> g.getStatus().equals(GhostStatusEnum.SCARED);
  }
}
