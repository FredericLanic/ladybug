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

import java.awt.Point;

import javax.inject.Named;

@Named("Level2")
public class Level2 extends Level {
  public Level2() {
    // nombre de blocks par ligne
    nbrBlocksByLine = 15;
    // nombre de lignes
    nbrLines = 15;

    levelDATA = new int[] { 19, 26, 26, 22, 137, 140, 19, 26, 22, 137, 140, 19, 26, 26, 22, 21, 139,
        142, 17, 26, 26, 20, 143, 17, 26, 26, 20, 139, 142, 21, 17, 26, 26, 20, 139, 134, 17, 26,
        20, 131, 142, 17, 26, 26, 20, 21, 131, 134, 25, 22, 133, 21, 135, 21, 133, 19, 28, 131, 134,
        21, 21, 137, 136, 142, 21, 141, 21, 133, 21, 141, 21, 139, 136, 140, 21, 25, 18, 26, 18, 24,
        18, 28, 133, 25, 18, 24, 18, 26, 18, 28, 134, 21, 135, 21, 135, 21, 131, 128, 134, 21, 135,
        21, 135, 21, 131, 132, 21, 133, 21, 133, 21, 129, 0, 132, 21, 133, 21, 133, 21, 129, 140,
        21, 141, 21, 141, 21, 137, 136, 140, 21, 141, 21, 141, 21, 137, 19, 24, 26, 24, 26, 16, 26,
        18, 26, 16, 26, 24, 26, 24, 22, 21, 131, 130, 130, 134, 21, 143, 21, 143, 21, 131, 130, 130,
        134, 21, 21, 137, 136, 136, 132, 17, 26, 8, 26, 20, 129, 136, 136, 140, 21, 17, 26, 26, 22,
        141, 21, 139, 130, 142, 21, 141, 19, 26, 26, 20, 21, 139, 142, 17, 26, 24, 22, 141, 19, 24,
        26, 20, 139, 142, 21, 25, 26, 26, 28, 131, 134, 25, 26, 28, 131, 134, 25, 26, 26, 28 };
  }

  @Override
  public Point getGhostRegenerateBlockPoint() {
    return new Point(7, 2);
  }

  @Override
  public Point getInitLadybugBlockPos() {
    return new Point(7, 11);
  }

  @Override
  public int getNbrMegaPoints() {
    return 2;
  }

}
