package com.kycox.ladybug.engine.element.ghost;

import com.kycox.ladybug.engine.element.SpeedFunction;
import com.kycox.ladybug.engine.element.ghost.set.GhostStatusEnum;
import com.kycox.ladybug.engine.element.ghost.set.GhostsSettingsEnum;
import com.kycox.ladybug.formules.BlinkyIncrementSpeed;

/**
 * Setting du fantôme Blinky
 *
 */
public class Blinky extends Ghost {

  private BlinkyIncrementSpeed blinkyIncrementSpeed = null;

  /**
   * Constructeur
   */
  public Blinky(int numLevel) {
    super(numLevel);
    setGhostSettings(GhostsSettingsEnum.BLINKY);
  }

  @Override
  public void getInitSpeed(int numLevel) {
    // Vitesse de Pacman moins 1
    initSpeedIndex(SpeedFunction.getInstance().getRealIndexSpeedMinus(numLevel));
  }

  public void setBlinkyIncrementSpeed(BlinkyIncrementSpeed blinkyIncrementSpeed) {
    this.blinkyIncrementSpeed = blinkyIncrementSpeed;
  }

  @Override
  public void setSpeedDuringGame() {
    if (!getStatus().equals(GhostStatusEnum.NORMAL)) {
      setSpeedIndex(getSpeedIndex());
      return;
    }
    // Calcul de la vitesse de Blinky en fonction du nombre de point restant dans la
    // map
    int incrementSpeedIndex = blinkyIncrementSpeed.getIncrementSpeedIndex();
    int newIndex = getStartIndexSpeed() + incrementSpeedIndex;
    if (newIndex != getSpeedIndex() && changeBlock()) {
      setSpeedIndex(getStartIndexSpeed() + incrementSpeedIndex);
    }
    // de toute manière on réaffecte
    setSpeedIndex(getSpeedIndex());
  }
}
