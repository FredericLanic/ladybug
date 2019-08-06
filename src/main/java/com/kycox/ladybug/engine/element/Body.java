package com.kycox.ladybug.engine.element;

import java.awt.Point;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.kycox.ladybug.constant.Constants;

/**
 * Classe abstraite pour les éléments qui bouge dans le jeu
 * 
 * Le mouvement peut être calculé ou bien en fonction des joueurs
 */
public abstract class Body {
  // Tableau des vitesses disponibles : les vitesses doivent être un multiple de
  // 24 (taille d'un BLOCK_SIZE) pour que le programme puisse détecter le
  // changement de ScreenBlock
  protected static final List<Integer> VALID_SPEEDS    = Collections
      .unmodifiableList(Arrays.asList(1, 2, 3, 4, 6, 8, 12, 24));
  // Direction du fantôme
  private Point                        dirPoint        = Constants.POINT_ZERO;
  // nombre de vie restant pour ce body
  private int                          lifesLeft       = 0;
  // Position dans le JPanel
  private Point                        posPoint        = Constants.POINT_ZERO;

  // Vitesse : index dans le tableau VALID_SPEEDS
  private int                          speedIndex      = 0;

  // Vitesse initiale : index dans le tableau VALID_SPEEDS
  private int                          startSpeedIndex = 0;

  public void addNewLife() {
    lifesLeft++;
  }

  /**
   * Le fantôme ou pacman change de block
   * 
   * @param pointPos : coordonnées DE L'ECRAN (x,y) dans la fenêtre
   * @return
   */
  public boolean changeBlock() {
    Point pointPos = getPosition();
    return pointPos.x % Constants.BLOCK_SIZE == 0 && pointPos.y % Constants.BLOCK_SIZE == 0;
  }

  /**
   * Retourne la direction
   * 
   * @return
   */
  public Point getDirection() {
    return dirPoint;
  }

  public int getLifesLeft() {
    return lifesLeft;
  }

  /**
   * Retourne la position
   * 
   * @return
   */
  public Point getPosition() {
    return posPoint;
  }

  /**
   * Getters et setters pour la vitesse
   * 
   * @return
   */
  public int getSpeed() {
    return VALID_SPEEDS.get(speedIndex);
  }

  public int getSpeedIndex() {
    return speedIndex;
  }

  /**
   * Return the start speed
   * 
   * @return
   */
  public int getStartIndexSpeed() {
    return startSpeedIndex;
  }

  /**
   * Initialise la vitesse du fantôme à la construction
   * 
   * (speed & startSpeed)
   * 
   * @param speedIndex
   */
  public void initSpeedIndex(int speedIndex) {
    this.speedIndex = speedIndex;
    this.startSpeedIndex = speedIndex;
  }

  public void minusLifesLeft() {
    lifesLeft--;
    System.out.println("lifesLeft:" + lifesLeft);
  }

  /**
   * Affecte une direction
   * 
   * @param dirPoint
   */
  public void setDirection(Point dirPoint) {
    this.dirPoint = dirPoint;
  }

  public void setLifesLeft(int lifesLeft) {
    this.lifesLeft = lifesLeft;
  }

  /**
   * Affecte une position
   * 
   * @param posPoint
   */
  public void setPosition(Point posPoint) {
    this.posPoint = posPoint;
  }

  /**
   * Affecte la vitesse
   * 
   * @param speedIndex
   */
  public void setSpeedIndex(int speedIndex) {
    // FIXME : rajouter un test si speenIndex > VALID_SPEEDS.length
    // ou pas, comme ça j'aurai une exception et je pourrai corriger le problème
    this.speedIndex = speedIndex;
  }
}
