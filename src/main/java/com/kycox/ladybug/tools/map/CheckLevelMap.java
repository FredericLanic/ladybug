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
package com.kycox.ladybug.tools.map;

import java.awt.Point;

import com.kycox.ladybug.level.ScreenBlock;
import com.kycox.ladybug.level.ScreenData;

/**
 * Vérification et ajustement du niveau pour éviter les erreurs humaines
 *
 */
public class CheckLevelMap {

  private int nbrBlocksByLines;

  private int nbrLines;

  /**
   * Vérifie le ScreenData
   *
   * @param data
   */
  public void check(ScreenData screenData) {
    this.nbrBlocksByLines = screenData.getNbrBlocksPerLine();
    this.nbrLines = screenData.getNbrLines();

    for (int x = 0; x < nbrBlocksByLines; x++) {
      for (int y = 0; y < nbrLines; y++) {
        Point       currentPoint = new Point(x, y);
        ScreenBlock block        = screenData.getDataBlock(currentPoint);
        checkScreenBlock(block, screenData);
      }
    }
  }

  /**
   *
   * Vérifie le block
   *
   * @param block
   * @return
   */
  private boolean checkScreenBlock(ScreenBlock block, ScreenData screenData) {
    boolean     hasChanged = false;

    Point       checkedPoint;
    ScreenBlock previousScreenBlock;

    boolean     isLeft     = block.isLeft();
    boolean     isRight    = block.isRight();
    boolean     isUp       = block.isUp();
    boolean     isDown     = block.isDown();

    // vérification des bordures !!!

    if (block.getCoordinate().x == 0 && !isLeft) {
      block.addLeft();
      hasChanged = true;
    }

    if (block.getCoordinate().x == (nbrBlocksByLines - 1) && !isRight) {
      block.addRight();
      hasChanged = true;
    }

    if (block.getCoordinate().y == 0 && !isUp) {
      block.addUp();
      hasChanged = true;
    }

    if (block.getCoordinate().y == (nbrLines - 1) && !isDown) {
      block.addDown();
      hasChanged = true;
    }

    // vérification de la bordure en haut
    if (isUp && block.getCoordinate().y > 0) {
      checkedPoint = new Point(block.getCoordinate().x, block.getCoordinate().y - 1);
      previousScreenBlock = screenData.getDataBlock(checkedPoint);
      if (!previousScreenBlock.isDown()) {
        hasChanged = true;
        previousScreenBlock.addDown();
      }
    }

    // vérification de la bordure en bas
    if (isDown && block.getCoordinate().y < nbrLines - 1) {
      checkedPoint = new Point(block.getCoordinate().x, block.getCoordinate().y + 1);
      previousScreenBlock = screenData.getDataBlock(checkedPoint);
      if (!previousScreenBlock.isUp()) {
        hasChanged = true;
        previousScreenBlock.addUp();
      }

    }

    // vérification de la bordure à droite
    if (isRight && block.getCoordinate().x < nbrBlocksByLines - 1) {
      checkedPoint = new Point(block.getCoordinate().x + 1, block.getCoordinate().y);
      previousScreenBlock = screenData.getDataBlock(checkedPoint);
      if (!previousScreenBlock.isLeft()) {
        hasChanged = true;
        previousScreenBlock.addLeft();
      }
    }

    // vérification de la bordure à gauche
    if (isLeft && block.getCoordinate().x > 0) {
      checkedPoint = new Point(block.getCoordinate().x - 1, block.getCoordinate().y);
      previousScreenBlock = screenData.getDataBlock(checkedPoint);
      if (!previousScreenBlock.isRight()) {
        hasChanged = true;
        previousScreenBlock.addRight();
      }

    }
    return hasChanged;
  }
}
