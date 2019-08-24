package com.kycox.ladybug.level;

import java.awt.Point;

public class CheckScreenBlockBorders {

  private ScreenData screenData;

  /**
   * Construteur
   *
   * @param screenData
   */
  public CheckScreenBlockBorders(ScreenData screenData) {
    this.screenData = screenData;
  }

  /**
   * Vérifie les bordures des ScreenBlocks utilisés pour la MAP
   */
  public void checkDataBlockBorder() {
    for (int x = 0; x < screenData.getCurrentLevel().getNbrBlocksByLine(); x++) {
      for (int y = 0; y < screenData.getCurrentLevel().getNbrLines(); y++) {
        checkDataBlockBorder(new Point(x, y));
      }
    }
  }

  /**
   * Vérifie les bordures des ScreenBlocks utilisés pour la Vue
   */
  public void checkViewBlockBorder() {
    for (int x = 0; x < screenData.getCurrentLevel().getNbrBlocksByLine(); x++) {
      for (int y = 0; y < screenData.getCurrentLevel().getNbrLines(); y++) {
        checkViewBlockBorder(new Point(x, y));
      }
    }
  }

  // FIXME : un peu de refacto ?
  private void checkDataBlockBorder(Point point) {
    ScreenBlock screenBlock = screenData.getDataBlock(point);
    ScreenBlock otherScreenBlock;

    if (screenBlock == null)
      return;

    // vérification des bordures principales !!!
    if (point.x == 0 && !screenBlock.isLeft())
      screenBlock.addLeft();

    if (point.x == (screenData.getCurrentLevel().getNbrBlocksByLine() - 1)
        && !screenBlock.isRight())
      screenBlock.addRight();

    if (point.y == 0 && !screenBlock.isUp())
      screenBlock.addUp();

    if (point.y == (screenData.getCurrentLevel().getNbrLines() - 1) && !screenBlock.isDown())
      screenBlock.addDown();

    // vérification de la bordure en haut
    otherScreenBlock = screenData.getDataBlock(new Point(point.x, point.y - 1));
    if (screenBlock.isUp() && point.y > 0) {
      if (otherScreenBlock != null && !otherScreenBlock.isDown())
        otherScreenBlock.addDown();
    }

    // vérification de la bordure en bas
    otherScreenBlock = screenData.getDataBlock(new Point(point.x, point.y + 1));
    if (screenBlock.isDown() && point.y < screenData.getCurrentLevel().getNbrLines() - 1) {
      if (otherScreenBlock != null && !otherScreenBlock.isUp())
        otherScreenBlock.addUp();
    }

    // vérification de la bordure à droite
    otherScreenBlock = screenData.getDataBlock(new Point(point.x + 1, point.y));
    if (screenBlock.isRight() && point.x < screenData.getCurrentLevel().getNbrBlocksByLine() - 1) {

      if (otherScreenBlock != null && !otherScreenBlock.isLeft())
        otherScreenBlock.addLeft();
    }

    // vérification de la bordure à gauche
    otherScreenBlock = screenData.getDataBlock(new Point(point.x - 1, point.y));
    if (screenBlock.isLeft() && point.x > 0) {
      if (otherScreenBlock != null && !otherScreenBlock.isRight())
        otherScreenBlock.addRight();

    }
  }

  private void checkViewBlockBorder(Point point) {
    ScreenBlock screenBlock = screenData.getViewBlock(point);
    ScreenBlock otherScreenBlock;

    if (!screenBlock.isNotAccessible())
      return;

    otherScreenBlock = screenData.getViewBlock(new Point(point.x, point.y - 1));
    if (otherScreenBlock != null && screenBlock.isUp()) {
      otherScreenBlock.removeDown();
    }

    otherScreenBlock = screenData.getViewBlock(new Point(point.x, point.y + 1));
    if (otherScreenBlock != null && screenBlock.isDown()) {
      otherScreenBlock.removeUp();
    }

    otherScreenBlock = screenData.getViewBlock(new Point(point.x - 1, point.y));
    if (otherScreenBlock != null && screenBlock.isLeft()) {
      otherScreenBlock.removeRight();
    }

    otherScreenBlock = screenData.getViewBlock(new Point(point.x + 1, point.y));
    if (otherScreenBlock != null && screenBlock.isRight()) {
      otherScreenBlock.removeLeft();
    }

  }
}
