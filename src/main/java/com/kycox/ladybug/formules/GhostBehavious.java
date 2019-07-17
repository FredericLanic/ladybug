package com.kycox.ladybug.formules;

import java.util.Random;

import com.kycox.ladybug.constant.Constants;

/**
 * Probabilité de comportement
 * 
 * Fonction y = ax + b où x est le niveau
 * 
 * Qui résoud : (1, 20); (nbrMaxNiveau, 90)
 * 
 * Retourne vrai si le nombre aléatoire (0-100) est en dessous de y
 *
 */
public class GhostBehavious {

  private double a               = 0;
  private double b               = 0;

  // échelle de 1 à 10
  private double limiteBasse     = 20;
  private double limiteHaute     = 90;

  private Random randomGenerator = new Random();

  private int    scale           = 100;

  private double y               = 0;

  /**
   * Constructeur privé
   * 
   */
  public GhostBehavious(int numLevel) {
    a = (limiteHaute - limiteBasse) / (Constants.NIVEAU_MAX - 1);
    b = limiteBasse - a;

    y = (a * ++numLevel + b);
  }

  /**
   * 
   * Créé un nombre aléatoire [0-100] et définie si ce nombre est inférieur à une
   * règle spécifique [limiteBasse, limiteHaute]
   * 
   * @param numLevel
   * @return
   */
  public boolean isActive() {
    return (randomGenerator.nextInt(scale) + 1) < y;
  }

}
