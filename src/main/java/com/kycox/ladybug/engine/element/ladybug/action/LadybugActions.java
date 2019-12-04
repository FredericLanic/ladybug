package com.kycox.ladybug.engine.element.ladybug.action;

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
