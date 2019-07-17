package com.kycox.ladybug.score;

import java.awt.Point;

import com.kycox.ladybug.engine.timer.IncrementScoreTimer;

/**
 * Contenu des valeurs affich� dans la map, lorsque pacman fait des exploits
 * 
 * @author kycox
 *
 */
public class IncrementScore {
  private boolean    dying;

  /** Position dans la fen�tre o� le score est affich� */
  private Point      position;

  /** Timer d'affichage du score dans la fen�tre */
  private IncrementScoreTimer scoreTimer;

  /** Valeur � afficher */
  private String     value;

  /**
   * Constructeur
   * 
   * @param position : position d'affichage du score
   * @param value    : valeur � afficher
   */
  public IncrementScore(Point position, String value) {
    this.position = position;
    this.value = value;
    this.setDying(false);
    scoreTimer = new IncrementScoreTimer(this);
    scoreTimer.launch(1000);
  }

  /**
   * Retourne la position
   *
   * @return
   */
  public Point getPosition() {
    return position;
  }

  /** Retourne la valeur du score */
  public String getValue() {
    return value;
  }

  /**
   * V�rifie si l'objet est en train de mourir
   * 
   * @return
   */
  public boolean isDying() {
    return dying;
  }

  /**
   * Affecte � l'objet le fait qu'il va disparaitre
   * 
   * @param dying
   */
  public void setDying(boolean dying) {
    this.dying = dying;
  }
}
