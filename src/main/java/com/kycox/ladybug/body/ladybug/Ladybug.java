/**
  Copyright (C) 2019  Frédéric Lanic frederic.lanic@outlook.fr

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.kycox.ladybug.body.ladybug;

import java.awt.Point;

import com.kycox.ladybug.action.ladybug.LadybugActions;
import com.kycox.ladybug.body.BodyMovedByUser;
import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.constant.ladybug.LadybugStatusEnum;
import com.kycox.ladybug.level.ScreenBlock;
import com.kycox.ladybug.level.ScreenData;
import com.kycox.ladybug.maths.SpeedFunction;
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
