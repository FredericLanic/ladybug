package com.kycox.ladybug.engine.element.ladybug;

import java.awt.Point;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.engine.element.BodyMovedByUser;
import com.kycox.ladybug.engine.element.SpeedFunction;
import com.kycox.ladybug.engine.element.ladybug.action.LadybugActions;
import com.kycox.ladybug.engine.element.ladybug.set.LadybugStatusEnum;
import com.kycox.ladybug.level.ScreenBlock;
import com.kycox.ladybug.level.ScreenData;
import com.kycox.ladybug.tools.Utils;

/**
 * Spécificités de Ladybug
 *
 */
public class Ladybug extends BodyMovedByUser {

  // statut de ladybug
  private LadybugStatusEnum status;

  // direction de ladybug
  private Point             viewDirectionPoint = Constants.POINT_ZERO;

  /**
   * Constructeur
   */
  public Ladybug() {
    super();
    status = LadybugStatusEnum.NORMAL;
  }

  /**
   * Getters / Setters pour le statut de ladybug
   *
   * @return
   */
  public LadybugStatusEnum getStatus() {
    return status;
  }

  public Point getViewDirection() {
    return viewDirectionPoint;
  }

  /**
   * Déplacement de ladybug et récupération du score obtenu Lancement du double timer dans le cas du
   * super power
   */
  public LadybugActions moveLadybug(ScreenData screenData) {
    LadybugActions ladybugActions = new LadybugActions();

    if (getStatus().equals(LadybugStatusEnum.DEAD))
      return ladybugActions;

    if (requeteDirectionPoint.equals(new Point(getDirection().x, -getDirection().y))) {
      setDirection(requeteDirectionPoint);
      this.viewDirectionPoint = getDirection();
    }

    // calcule uniquement lorsque ladybug rempli le block
    if (changeBlock()) {
      ScreenBlock currentScreenBlock = screenData
          .getDataBlock(Utils.convertPointToBlockUnit(getPosition()));

      ladybugActions.setCurrentPoint(currentScreenBlock);
      ladybugActions.setHasEatenAPoint(currentScreenBlock.isPoint());
      ladybugActions.setHasEatenAMegaPoint(currentScreenBlock.isMegaPoint());

      if (canMove(requeteDirectionPoint, currentScreenBlock))
        viewDirectionPoint = requeteDirectionPoint;

      move(currentScreenBlock);

    }
    getPosition().translate(getDirection().x * getSpeed(), getDirection().y * getSpeed());

    return ladybugActions;
  }

  /**
   * Caractéristiques de ladybug en début de niveau
   */
  public void setStartLevel(int numLevel, ScreenData screenData) {
    // attributs de ladybug
    setPosition(screenData.getInitLadybugPos());
    setDirection(Constants.POINT_ZERO);
    viewDirectionPoint = Constants.POINT_LEFT;
    setMovingRequete(Constants.POINT_ZERO);
    setStatus(LadybugStatusEnum.NORMAL);
    initSpeedIndex(SpeedFunction.getInstance().getRealIndexSpeed(numLevel));
  }

  /**
   * Mise à jour du statut de ladybug via le modèle
   *
   * @param status
   */
  public void setStatus(LadybugStatusEnum status) {
    this.status = status;
  }
}
