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
package com.kycox.ladybug.action.ghost;

import com.kycox.ladybug.body.ghost.Ghost;

public class GhostActions {

  private boolean eaten           = false;

  private Ghost   ghost;

  private boolean hasEatenLadybug = false;

  private boolean regenerated     = false;

  public Ghost getGhost() {
    return ghost;
  }

  public boolean hasEatenLadybug() {
    return hasEatenLadybug;
  }

  public boolean isEaten() {
    return eaten;
  }

  public boolean isRegenerated() {
    return regenerated;
  }

  public void setEaten(boolean eaten) {
    this.eaten = eaten;
  }

  public void setGhost(Ghost ghost) {
    this.ghost = ghost;
  }

  public void setHasEatenLadybug(boolean eatLadybug) {
    this.hasEatenLadybug = eatLadybug;
  }

  public void setRegenerated(boolean regenerated) {
    this.regenerated = regenerated;
  }

}
