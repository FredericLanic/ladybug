package com.kycox.ladybug.formules;

import com.kycox.ladybug.level.ScreenData;

public class BlinkyIncrementSpeed {

  private double     a;

  private double     b;

  private double     R          = 80.0;

  private ScreenData screenData = null;

  /**
   * aX²+bX+c (c=0)
   * 
   * Point (0,0) Point (R,1) Point (T,3) (ici T=100%)
   * 
   * 1 < R < T
   * 
   */

  private double     T          = 100.0;

  public BlinkyIncrementSpeed(int numLevel, ScreenData screenData) {
    this.screenData = screenData;

    // calcul de R en fonction du niveau
    // pour commencer : on met R = 80
    // puis on devrait faire R = 80 pour le niveau 1, et R = 20 pour le dernier
    // niveau

    a = (T - 3 * R) / (R * R * T - R * T * T);
    b = (T * T - 3 * R * R) / (R * T * (T - R));
  }

  /**
   * Return the number of index to increment
   * 
   * @param x
   * @return
   */
  public int getIncrementSpeedIndex() {
    int x = screenData.getPercentageEatenPoint();
    return (int) (a * x * x + b * x);
  }
}
