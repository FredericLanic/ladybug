package com.kycox.ladybug.engine.element.ghost;

import com.kycox.ladybug.engine.element.SpeedFunction;
import com.kycox.ladybug.engine.element.ghost.set.GhostsSettingsEnum;

/**
 * Sp�cificit� du fant�me Clyde
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
     * Id�e : faire acc�lerer ou ralentir Clyde de mani�re al�atoire....
     */
    initSpeedIndex(SpeedFunction.getInstance().getRealIndexSpeed(numLevel));
  }

  @Override
  public void setSpeedDuringGame() {
    setSpeedIndex(getSpeedIndex());
  }
}
