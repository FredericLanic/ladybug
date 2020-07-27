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
package com.kycox.ladybug.action.ladybug;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.level.ScreenBlock;
import com.kycox.ladybug.score.GroupIncrementScores;
import com.kycox.ladybug.tools.Utils;

public class LadybugActions {

  private ScreenBlock currentPoint;

  private boolean     hasEatenAMegaPoint;

  private boolean     hasEatenAPoint;

  public void addIncrementScores(GroupIncrementScores groupIncrementScores) {
    if (hasEatenAMegaPoint())
      groupIncrementScores.add(Utils.convertPointToGraphicUnit(currentPoint.getCoordinate()),
          Integer.toString(Constants.SCORE_MEGA_POINT));
  }

  public ScreenBlock getCurrentScreenBlock() {
    return currentPoint;
  }

  public boolean hasEatenAMegaPoint() {
    return hasEatenAMegaPoint;
  }

  public boolean hasEatenAPoint() {
    return hasEatenAPoint;
  }

  public void init() {
    currentPoint = null;
    hasEatenAMegaPoint = false;
    hasEatenAPoint = false;
  }

  public void setCurrentPoint(ScreenBlock currentPoint) {
    this.currentPoint = currentPoint;
  }

  public void setHasEatenAMegaPoint(boolean hasEatenAMegaPoint) {
    this.hasEatenAMegaPoint = hasEatenAMegaPoint;
  }

  public void setHasEatenAPoint(boolean hasEatenAPoint) {
    this.hasEatenAPoint = hasEatenAPoint;
  }

}
