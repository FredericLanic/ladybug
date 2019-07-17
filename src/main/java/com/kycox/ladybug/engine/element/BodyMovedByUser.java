package com.kycox.ladybug.engine.element;

import java.awt.Point;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.level.ScreenBlock;

public class BodyMovedByUser extends Body {

  // Les direction de pacman (requ�te suite aux touches et la vue)
  protected Point requeteDirectionPoint = Constants.POINT_ZERO;

  /**
   * Retourne vrai si l'�l�ment peut bouger en fonction des cases � c�t�
   * 
   * @param direction
   * @param screenBlock
   * @return
   */
  protected boolean canMove(Point direction, ScreenBlock screenBlock) {
    return !(direction.equals(Constants.POINT_LEFT) && screenBlock.isLeft() // pacman est coinc� � gauche
        || direction.equals(Constants.POINT_RIGHT) && screenBlock.isRight() // pacman est coinc� en haut
        || direction.equals(Constants.POINT_UP) && screenBlock.isUp() // pacman est coinc� � droite
        || direction.equals(Constants.POINT_DOWN) && screenBlock.isDown() // pacman est coinc� en bas
    );
  }

  /**
   * D�placement de l'�l�ment
   * 
   * @param screenBlock
   */
  protected void move(ScreenBlock screenBlock) {
    if (changeBlock()) {
      if ((requeteDirectionPoint.x != 0 || requeteDirectionPoint.y != 0)
          && canMove(requeteDirectionPoint, screenBlock)) {
        setDirection(requeteDirectionPoint);
      }
      /**
       * Quand Pacman est coinc� : au d�but ou quand il butte sur un mur.
       */
      if (!canMove(getDirection(), screenBlock)) {
        setDirection(Constants.POINT_ZERO);
      }
    }
  }

  /**
   * SETTERS utilis�s par le mod�le (MVC)
   * 
   * @param requeteDirection
   */
  public void setMovingRequete(Point requeteDirection) {
    this.requeteDirectionPoint = requeteDirection;
  }
}
