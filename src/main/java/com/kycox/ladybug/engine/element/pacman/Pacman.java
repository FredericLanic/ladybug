package com.kycox.ladybug.engine.element.pacman;

import java.awt.Point;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.engine.element.BodyMovedByUser;
import com.kycox.ladybug.engine.element.SpeedFunction;
import com.kycox.ladybug.engine.element.pacman.action.PacmanActions;
import com.kycox.ladybug.engine.element.pacman.set.PacmanStatusEnum;
import com.kycox.ladybug.level.ScreenBlock;
import com.kycox.ladybug.level.ScreenData;
import com.kycox.ladybug.tools.Utils;

/**
 * Spécificités de Pacman
 *
 */
public class Pacman extends BodyMovedByUser {

  // statut de pacman
  private PacmanStatusEnum status;

  // direction de pacman
  private Point            viewDirectionPoint = Constants.POINT_ZERO;

  /**
   * Constructeur
   */
  public Pacman() {
    super();
    status = PacmanStatusEnum.NORMAL;
  }

  /**
   * Getters / Setters pour le statut de pacman
   * 
   * @return
   */
  public PacmanStatusEnum getStatus() {
    return status;
  }

  public Point getViewDirection() {
    return viewDirectionPoint;
  }

  /**
   * Déplacement de Pacman et récupération du score obtenu Lancement du double
   * timer dans le cas du super power
   */
  public PacmanActions movePacman(ScreenData screenData) {
    PacmanActions pacmanActions = new PacmanActions();

    if (getStatus().equals(PacmanStatusEnum.DEAD))
      return pacmanActions;

    if (requeteDirectionPoint.equals(new Point(getDirection().x, -getDirection().y))) {
      setDirection(requeteDirectionPoint);
      this.viewDirectionPoint = getDirection();
    }

    // calcule uniquement lorsque Pacman rempli le block
    if (changeBlock()) {
      ScreenBlock currentScreenBlock = screenData.getBlock(Utils.convertPointToBlockUnit(getPosition()));

      pacmanActions.setCurrentPoint(currentScreenBlock);
      pacmanActions.setHasEatenAPoint(currentScreenBlock.isPoint());
      pacmanActions.setHasEatenAMegaPoint(currentScreenBlock.isMegaPoint());

      if (canMove(requeteDirectionPoint, currentScreenBlock))
        viewDirectionPoint = requeteDirectionPoint;

      move(currentScreenBlock);

    }
    getPosition().translate(getDirection().x * getSpeed(), getDirection().y * getSpeed());

    return pacmanActions;
  }

  /**
   * Caractéristiques de Pacman en début de niveau
   */
  public void setStartLevel(int numLevel, ScreenData screenData) {
    // attributs de pacman
    setPosition(screenData.getInitPacmanPos());
    setDirection(Constants.POINT_ZERO);
    viewDirectionPoint = Constants.POINT_LEFT;
    setMovingRequete(Constants.POINT_ZERO);
    setStatus(PacmanStatusEnum.NORMAL);
    initSpeedIndex(SpeedFunction.getInstance().getSpecificIndexSpeed(numLevel));
  }

  /**
   * Mise à jour du statut de Pacman via le modèle
   * 
   * @param status
   */
  public void setStatus(PacmanStatusEnum status) {
    this.status = status;
  }
}
