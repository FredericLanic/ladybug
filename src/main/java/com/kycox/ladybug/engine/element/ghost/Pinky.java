package com.kycox.ladybug.engine.element.ghost;

import com.kycox.ladybug.engine.element.SpeedFunction;
import com.kycox.ladybug.engine.element.ghost.set.GhostStatusEnum;
import com.kycox.ladybug.engine.element.ghost.set.GhostsSettingsEnum;

/**
 * Spécificités du fantôme Pinky
 *
 */
public class Pinky extends Ghost {

  public Pinky(int numLevel) {
    super(numLevel);
    setGhostSettings(GhostsSettingsEnum.PINKY);
  }

  @Override
  public void getInitSpeed(int numLevel) {
    initSpeedIndex(SpeedFunction.getInstance().getRealIndexSpeedMinus(numLevel));
  }

  @Override
  public void setSpeedDuringGame(int numLevel) {
    if (!changeBlock()) {
      setSpeedIndex(getSpeedIndex());
      return;
    }

    if (GhostStatusEnum.isScared().test(this))
      setSpeedIndex(SpeedFunction.getInstance().getRealIndexSpeedMinus(numLevel) - 1);
    else
      setSpeedIndex(getStartIndexSpeed());
  }

}
