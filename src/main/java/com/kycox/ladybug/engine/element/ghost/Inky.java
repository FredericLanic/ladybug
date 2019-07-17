package com.kycox.ladybug.engine.element.ghost;

import com.kycox.ladybug.engine.element.SpeedFunction;
import com.kycox.ladybug.engine.element.ghost.set.GhostsSettingsEnum;

/**
 * Sp�cificit�s du fant�me Inky
 *
 */
public class Inky extends Ghost {

  public static final int ID = 3;

  public Inky(int numLevel) {
    super(numLevel);
    setGhostSettings(GhostsSettingsEnum.INKY);
  }

  @Override
  public void getInitSpeed(int numLevel) {
    initSpeedIndex(SpeedFunction.getInstance().getRealIndexSpeedMinus(numLevel));
  }

  @Override
  public void setSpeedDuringGame() {
    setSpeedIndex(getSpeedIndex());
  }

}
