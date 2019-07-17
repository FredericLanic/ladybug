package com.kycox.ladybug.formules;

import java.util.Random;

import com.kycox.ladybug.constant.Constants;

/**
 * Probabilit� de comportement
 * 
 * Fonction y = ax + b o� x est le niveau
 * 
 * Qui r�soud : (1, 20); (nbrMaxNiveau, 90)
 * 
 * Retourne vrai si le nombre al�atoire (0-100) est en dessous de y
 *
 */
public class GhostBehavious {

  private double a               = 0;
  private double b               = 0;

  // �chelle de 1 � 10
  private double limiteBasse     = 20;
  private double limiteHaute     = 90;

  private Random randomGenerator = new Random();

  private int    scale           = 100;

  private double y               = 0;

  /**
   * Constructeur priv�
   * 
   */
  public GhostBehavious(int numLevel) {
    a = (limiteHaute - limiteBasse) / (Constants.NIVEAU_MAX - 1);
    b = limiteBasse - a;

    y = (a * ++numLevel + b);
  }

  /**
   * 
   * Cr�� un nombre al�atoire [0-100] et d�finie si ce nombre est inf�rieur � une
   * r�gle sp�cifique [limiteBasse, limiteHaute]
   * 
   * @param numLevel
   * @return
   */
  public boolean isActive() {
    return (randomGenerator.nextInt(scale) + 1) < y;
  }

}
