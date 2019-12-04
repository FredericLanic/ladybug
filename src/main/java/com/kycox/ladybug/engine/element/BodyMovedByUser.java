package com.kycox.ladybug.engine.element;

import java.awt.Point;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.level.ScreenBlock;

public class BodyMovedByUser extends Body {

  // Les direction de ladyBug (requête suite aux touches et la vue)
  protected Point requeteDirectionPoint = Constants.POINT_ZERO;

  /**
   * SETTERS utilisés par le modèle (MVC)
   *
   * @param requeteDirection
   */
  public void setMovingRequete(Point requeteDirection) {
    this.requeteDirectionPoint = requeteDirection;
  }

  /**
   * Retourne vrai si l'élément peut bouger en fonction des cases à côté
   *
   * @param direction
   * @param screenBlock
   * @return
   */
  protected boolean canMove(Point direction, ScreenBlock screenBlock) {
    return !(direction.equals(Constants.POINT_LEFT) && screenBlock.isLeft() // ladybug est coincé à
                                                                            // gauche
        || direction.equals(Constants.POINT_RIGHT) && screenBlock.isRight() // ladybug est coincé en
                                                                            // haut
        || direction.equals(Constants.POINT_UP) && screenBlock.isUp() // ladybug est coincé à droite
        || direction.equals(Constants.POINT_DOWN) && screenBlock.isDown() // ladybug est coincé en
                                                                          // bas
    );
  }

  /**
   * Déplacement de l'élément
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
       * Quand ladybug est coincé : au début ou quand il butte sur un mur.
       */
      if (!canMove(getDirection(), screenBlock)) {
        setDirection(Constants.POINT_ZERO);
      }
    }
  }
}
