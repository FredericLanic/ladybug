package com.kycox.ladybug.tools.map;

import java.awt.Point;

import com.kycox.ladybug.level.ScreenBlock;
import com.kycox.ladybug.level.ScreenData;

/**
 * V�rification et ajustement du niveau pour �viter les erreurs humaines
 *
 */
public class CheckLevelMap {

  private int nbrBlocksByLines;

  private int nbrLines;

  /**
   * V�rifie le ScreenData
   * 
   * @param data
   */
  public void check(ScreenData screenData) {
    this.nbrBlocksByLines = screenData.getNbrBlocksPerLine();
    this.nbrLines = screenData.getNbrLines();

    for (int x = 0; x < nbrBlocksByLines; x++) {
      for (int y = 0; y < nbrLines; y++) {
        Point currentPoint = new Point(x, y);
        ScreenBlock block = screenData.getBlock(currentPoint);
        checkScreenBlock(block, screenData);
      }
    }
  }

  /**
   * 
   * V�rifie le block
   * 
   * @param block
   * @return
   */
  private boolean checkScreenBlock(ScreenBlock block, ScreenData screenData) {
    boolean hasChanged = false;

    Point checkedPoint;
    ScreenBlock previousScreenBlock;

    boolean isLeft = block.isLeft();
    boolean isRight = block.isRight();
    boolean isUp = block.isUp();
    boolean isDown = block.isDown();

    // v�rification des bordures !!!

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

    // v�rification de la bordure en haut
    if (isUp && block.getCoordinate().y > 0) {
      checkedPoint = new Point(block.getCoordinate().x, block.getCoordinate().y - 1);
      previousScreenBlock = screenData.getBlock(checkedPoint);
      if (!previousScreenBlock.isDown()) {
        hasChanged = true;
        previousScreenBlock.addDown();
      }
    }

    // v�rification de la bordure en bas
    if (isDown && block.getCoordinate().y < nbrLines - 1) {
      checkedPoint = new Point(block.getCoordinate().x, block.getCoordinate().y + 1);
      previousScreenBlock = screenData.getBlock(checkedPoint);
      if (!previousScreenBlock.isUp()) {
        hasChanged = true;
        previousScreenBlock.addUp();
      }

    }

    // v�rification de la bordure � droite
    if (isRight && block.getCoordinate().x < nbrBlocksByLines - 1) {
      checkedPoint = new Point(block.getCoordinate().x + 1, block.getCoordinate().y);
      previousScreenBlock = screenData.getBlock(checkedPoint);
      if (!previousScreenBlock.isLeft()) {
        hasChanged = true;
        previousScreenBlock.addLeft();
      }
    }

    // v�rification de la bordure � gauche
    if (isLeft && block.getCoordinate().x > 0) {
      checkedPoint = new Point(block.getCoordinate().x - 1, block.getCoordinate().y);
      previousScreenBlock = screenData.getBlock(checkedPoint);
      if (!previousScreenBlock.isRight()) {
        hasChanged = true;
        previousScreenBlock.addRight();
      }

    }
    return hasChanged;
  }
}
