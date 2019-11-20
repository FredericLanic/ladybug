package com.kycox.ladybug.engine.element.ghost;

import com.kycox.ladybug.engine.element.SpeedFunction;
import com.kycox.ladybug.engine.element.ghost.set.GhostStatusEnum;
import com.kycox.ladybug.engine.element.ghost.set.GhostsSettingsEnum;

/**
 * Spécificité du fantôme Clyde
 *
 */
public class Clyde extends Ghost {

  public static final int ID = 2;

  /**
   * Constructeur lors du jeu
   */
  public Clyde(int numLevel) {
    super(numLevel);
    setGhostSettings(GhostsSettingsEnum.CLYDE);
  }

  @Override
  public void getInitSpeed(int numLevel) {
    /*
     * Idée : faire accélerer ou ralentir Clyde de manière aléatoire....
     */
    initSpeedIndex(SpeedFunction.getInstance().getRealIndexSpeed(numLevel));
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
