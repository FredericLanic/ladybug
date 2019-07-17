package com.kycox.ladybug.engine.element;

import com.kycox.ladybug.constant.Constants;

public class SpeedFunction {
  private static final SpeedFunction pacmanSpeed = new SpeedFunction();

  public static SpeedFunction getInstance() {
    return pacmanSpeed;
  }

  private double a           = 0;
  private double b           = 0;

  // échelle de 1 à 10
  private double limiteBasse = 2;

  private double limiteHaute = Body.VALID_SPEEDS.size();

  private SpeedFunction() {
    a = (limiteHaute - limiteBasse) / (Constants.NIVEAU_MAX - 1);
    b = limiteBasse - a;
  }

  /**
   * Retourne la vitesse calculée de Pacman en fonction du niveau
   * 
   * @param numLevel
   * @return
   */
  public int getRealIndexSpeed(int numLevel) {
    // calcul de l'index de la vitesse
    int id = (int) (a * numLevel + b);
    return id;
  }

  /**
   * Retourne la vitesse calculée -1 de Pacman en fonction du niveau
   * 
   * @param numLevel
   * @return
   */
  public int getRealIndexSpeedMinus(int numLevel) {
    // calcul de l'index de la vitesse
    int id = (int) (a * numLevel + b);
    if (id > 0)
      id--;
    return id;
  }

  /**
   * Retourne la vitesse calculée +1 de Pacman en fonction du niveau
   * 
   * @param numLevel
   * @return
   */
  public int getRealIndexSpeedPlus(int numLevel) {
    // calcul de l'index de la vitesse
    int id = (int) (a * numLevel + b);
    if (id < limiteHaute - 1)
      id++;
    return id;
  }

  /**
   * Retourne la vitesse de Pacman spéficique
   * 
   */
  public int getSpecificIndexSpeed(int numLevel) {
    int id = (int) (a * numLevel + b);

    if (id < limiteHaute - 1 && numLevel <= Constants.NIVEAU_MAX / 2)
      id++;
    return id;
  }
}
