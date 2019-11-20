package com.kycox.ladybug.engine.view.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.level.ScreenBlock;
import com.kycox.ladybug.level.ScreenData;
import com.kycox.ladybug.tools.Utils;

public class ScreenBlockView {

  private static final Color bluePacman     = new Color(33, 33, 255);

  // Couleur d'un point
  private static final Color dotColor       = new Color(192, 192, 0);

  // Couleur du labyrinthe
  private static final Color megaPointColor = new Color(255, 128, 0);

  // Couleur du lieu revivor
  private static final Color revivorColor   = new Color(128, 255, 255);

  public static void display(Graphics2D g2d, ScreenData screenData, int x, int y) {

//    g2d.setColor(Color.LIGHT_GRAY);
//    g2d.drawRect(x, y, Constants.BLOCK_SIZE, Constants.BLOCK_SIZE);

    displayBorders(g2d, screenData, x, y);
    displayPoints(g2d, screenData, x, y);

//    if (screenBlock.isNotAccessible())
//      displayInsideLines(g2d, screenBlock);
  }

  /**
   * Affichage des bordures
   *
   * Note : faire confiance, je sais c'est immaintenable :)
   *
   * @param g2d
   * @param screenBlock
   * @param x
   * @param y
   */
  private static void displayBorders(Graphics2D g2d, ScreenData screenData, int x, int y) {

    ScreenBlock screenBlock = screenData
        .getViewBlock(Utils.convertPointToBlockUnit(new Point(x, y)));

    g2d.setColor(bluePacman);
    g2d.setStroke(new BasicStroke(2));

    Point currentCoord = screenBlock.getCoordinate();

    // affichage de la barre à gauche
    if (screenBlock.isLeft() && !screenBlock.isUp() && !screenBlock.isDown() || currentCoord.x == 0
        && currentCoord.y != 0 && currentCoord.y != screenData.getNbrLines() - 1) {
      g2d.drawLine(x, y, x, y + Constants.BLOCK_SIZE);
    }

    // affichage de la barre en haut
    if (screenBlock.isUp() && !screenBlock.isLeft() && !screenBlock.isRight() || currentCoord.y == 0
        && currentCoord.x != 0 && currentCoord.x != screenData.getNbrBlocksPerLine() - 1) {
      g2d.drawLine(x, y, x + Constants.BLOCK_SIZE, y);
    }

    // affichage de la barre à droite
    if (screenBlock.isRight() && !screenBlock.isUp() && !screenBlock.isDown()
        || currentCoord.x == screenData.getNbrBlocksPerLine() - 1 && currentCoord.y != 0
            && currentCoord.y != screenData.getNbrLines() - 1) {
      g2d.drawLine(x + Constants.BLOCK_SIZE, y, x + Constants.BLOCK_SIZE, y + Constants.BLOCK_SIZE);
    }

    // affichage de la barre en bas
    if (screenBlock.isDown() && !screenBlock.isLeft() && !screenBlock.isRight()
        || currentCoord.y == screenData.getNbrLines() - 1 && currentCoord.x != 0
            && currentCoord.x != screenData.getNbrBlocksPerLine() - 1) {
      g2d.drawLine(x, y + Constants.BLOCK_SIZE, x + Constants.BLOCK_SIZE, y + Constants.BLOCK_SIZE);
    }

    int rayon = Constants.BLOCK_SIZE;

    // affichage de la courbe gauche vers haut
    if (screenBlock.isLeft() && screenBlock.isUp()) {
      g2d.drawArc(x, y, rayon, rayon, 90, 90);
    }

    // affichage de la courbe gauche vers bas
    if (screenBlock.isLeft() && screenBlock.isDown()) {
      g2d.drawArc(x, y, rayon, rayon, 270, -90);
    }

    // affichage de la courbe haut vers droite
    if (screenBlock.isRight() && screenBlock.isUp()) {
      g2d.drawArc(x, y, rayon, rayon, 0, 90);
    }

    // affichage de la courbe doite vers bas
    if (screenBlock.isRight() && screenBlock.isDown()) {
      g2d.drawArc(x, y, rayon, rayon, 270, 90);
    }

//    g2d.setColor(Color.BLUE);

    if (screenBlock.isLeft() && screenBlock.isUp()
        && currentCoord.x < screenData.getNbrBlocksPerLine() - 1
        && screenData.getViewBlock(new Point(currentCoord.x + 1, currentCoord.y)).isUp()) {
      g2d.drawLine(x + Constants.BLOCK_SIZE / 2, y, x + Constants.BLOCK_SIZE, y);
    }

    if (screenBlock.isUp() && currentCoord.x < screenData.getNbrBlocksPerLine() - 2
        && currentCoord.y > 1
        && screenData.getViewBlock(new Point(currentCoord.x + 1, currentCoord.y - 1)).isLeft()) {
      g2d.drawLine(x + Constants.BLOCK_SIZE / 2, y, x + Constants.BLOCK_SIZE, y);
    }

//    g2d.setColor(Color.RED);

    if (screenBlock.isLeft() && screenBlock.isDown()
        && currentCoord.x < screenData.getNbrBlocksPerLine() - 1
        && screenData.getViewBlock(new Point(currentCoord.x + 1, currentCoord.y)).isDown()) {
      g2d.drawLine(x + Constants.BLOCK_SIZE / 2, y + Constants.BLOCK_SIZE, x + Constants.BLOCK_SIZE,
          y + Constants.BLOCK_SIZE);
    }

    if (screenBlock.isDown() && currentCoord.x < screenData.getNbrBlocksPerLine() - 2
        && currentCoord.y < screenData.getNbrLines() - 2
        && screenData.getViewBlock(new Point(currentCoord.x + 1, currentCoord.y + 1)).isLeft()) {
      g2d.drawLine(x + Constants.BLOCK_SIZE / 2, y + Constants.BLOCK_SIZE, x + Constants.BLOCK_SIZE,
          y + Constants.BLOCK_SIZE);
    }

//    g2d.setColor(Color.YELLOW);

    if (screenBlock.isRight() && screenBlock.isUp() && currentCoord.x > 0
        && screenData.getViewBlock(new Point(currentCoord.x - 1, currentCoord.y)).isUp()) {
      g2d.drawLine(x, y, x + Constants.BLOCK_SIZE / 2, y);
    }

    if (screenBlock.isUp() && currentCoord.x > 1 && currentCoord.y > 1
        && screenData.getViewBlock(new Point(currentCoord.x - 1, currentCoord.y - 1)).isRight()) {
      g2d.drawLine(x, y, x + Constants.BLOCK_SIZE / 2, y);
    }

//    g2d.setColor(Color.WHITE);

    ScreenBlock sbTmp  = screenData.getViewBlock(new Point(currentCoord.x - 1, currentCoord.y));
    ScreenBlock sbTmp1 = screenData.getViewBlock(new Point(currentCoord.x - 1, currentCoord.y + 1));

    if (screenBlock.isRight() && screenBlock.isDown() && currentCoord.x > 0
        && (sbTmp.isDown() || sbTmp.isRight())) {
      g2d.drawLine(x, y + Constants.BLOCK_SIZE, x + Constants.BLOCK_SIZE / 2,
          y + Constants.BLOCK_SIZE);
    }

    if (screenBlock.isDown() && currentCoord.x > 1 && currentCoord.y < screenData.getNbrLines() - 2
        && (sbTmp.isDown() || sbTmp1.isRight())) {
      g2d.drawLine(x, y + Constants.BLOCK_SIZE, x + Constants.BLOCK_SIZE / 2,
          y + Constants.BLOCK_SIZE);
    }

    // ********************//

//    g2d.setColor(Color.BLUE);

    if (screenBlock.isRight() && screenBlock.isUp() && currentCoord.y < screenData.getNbrLines() - 1
        && screenData.getViewBlock(new Point(currentCoord.x, currentCoord.y + 1)).isRight()) {
      g2d.drawLine(x + Constants.BLOCK_SIZE, y + Constants.BLOCK_SIZE / 2, x + Constants.BLOCK_SIZE,
          y + Constants.BLOCK_SIZE);
    }

    if (screenBlock.isRight() && currentCoord.y < screenData.getNbrLines() - 2
        && currentCoord.x < screenData.getNbrBlocksPerLine() - 2
        && screenData.getViewBlock(new Point(currentCoord.x + 1, currentCoord.y + 1)).isUp()) {
      g2d.drawLine(x + Constants.BLOCK_SIZE, y + Constants.BLOCK_SIZE / 2, x + Constants.BLOCK_SIZE,
          y + Constants.BLOCK_SIZE);
    }

//    g2d.setColor(Color.RED);

    if (screenBlock.isLeft() && screenBlock.isUp() && currentCoord.y < screenData.getNbrLines() - 1
        && screenData.getViewBlock(new Point(currentCoord.x, currentCoord.y + 1)).isLeft()) {
      g2d.drawLine(x, y + Constants.BLOCK_SIZE / 2, x, y + Constants.BLOCK_SIZE);
    }

    if (screenBlock.isLeft() && currentCoord.y < screenData.getNbrLines() - 2 && currentCoord.x > 1
        && screenData.getViewBlock(new Point(currentCoord.x - 1, currentCoord.y + 1)).isUp()) {
      g2d.drawLine(x, y + Constants.BLOCK_SIZE / 2, x, y + Constants.BLOCK_SIZE);
    }

//    g2d.setColor(Color.WHITE);

    if (screenBlock.isRight() && screenBlock.isDown() && currentCoord.y > 0
        && screenData.getViewBlock(new Point(currentCoord.x, currentCoord.y - 1)).isRight()) {
      g2d.drawLine(x + Constants.BLOCK_SIZE, y, x + Constants.BLOCK_SIZE,
          y + Constants.BLOCK_SIZE / 2);
    }

    if (screenBlock.isRight() && currentCoord.y > 1
        && currentCoord.x < screenData.getNbrBlocksPerLine() - 2
        && screenData.getViewBlock(new Point(currentCoord.x + 1, currentCoord.y - 1)).isDown()) {
      g2d.drawLine(x + Constants.BLOCK_SIZE, y, x + Constants.BLOCK_SIZE,
          y + Constants.BLOCK_SIZE / 2);
    }

//    g2d.setColor(Color.YELLOW);

    if (screenBlock.isLeft() && screenBlock.isDown() && currentCoord.y > 0
        && screenData.getViewBlock(new Point(currentCoord.x, currentCoord.y - 1)).isLeft()) {
      g2d.drawLine(x, y, x, y + Constants.BLOCK_SIZE / 2);
    }

    if (screenBlock.isLeft() && currentCoord.y > 1 && currentCoord.x > 1
        && screenData.getViewBlock(new Point(currentCoord.x - 1, currentCoord.y - 1)).isDown()) {
      g2d.drawLine(x, y, x, y + Constants.BLOCK_SIZE / 2);
    }

  }

//  /**
//   * Display double lines
//   *
//   * @param g2d
//   * @param screenBlock
//   * @param x
//   * @param y
//   */
//  private static void displayInsideLines(Graphics2D g2d, ScreenBlock screenBlock) {
//    g2d.setColor(Color.YELLOW);
//
//    Point position = Utils.convertPointToGraphicUnit(screenBlock.getCoordinate());
//    int   x        = position.x;
//    int   y        = position.y;
//
//    // Gestion du trait double à l'intérieur - trait à gauche
//    if (screenBlock.isLeft() && screenBlock.isUp() && screenBlock.isDown())
//      g2d.drawLine(x + 4, y + 4, x + 4, y + Constants.BLOCK_SIZE - 1 - 4);
//    else if (screenBlock.isLeft() && screenBlock.isUp())
//      g2d.drawLine(x + 4, y + 4, x + 4, y + Constants.BLOCK_SIZE - 1);
//    else if (screenBlock.isLeft() && screenBlock.isDown())
//      g2d.drawLine(x + 4, y, x + 4, y + Constants.BLOCK_SIZE - 1 - 4);
//    else if (screenBlock.isLeft())
//      g2d.drawLine(x + 4, y, x + 4, y + Constants.BLOCK_SIZE - 1);
//
//    if (screenBlock.isDown() && screenBlock.isRight() && screenBlock.isLeft())
//      g2d.drawLine(x + 4, y + Constants.BLOCK_SIZE - 1 - 4, x + Constants.BLOCK_SIZE - 1 - 4,
//          y + Constants.BLOCK_SIZE - 1 - 4);
//    else if (screenBlock.isDown() && screenBlock.isRight())
//      g2d.drawLine(x, y + Constants.BLOCK_SIZE - 1 - 4, x + Constants.BLOCK_SIZE - 1 - 4,
//          y + Constants.BLOCK_SIZE - 1 - 4);
//    else if (screenBlock.isDown() && screenBlock.isLeft())
//      g2d.drawLine(x + 4, y + Constants.BLOCK_SIZE - 1 - 4, x + Constants.BLOCK_SIZE - 1,
//          y + Constants.BLOCK_SIZE - 1 - 4);
//    else if (screenBlock.isDown())
//      g2d.drawLine(x, y + Constants.BLOCK_SIZE - 1 - 4, x + Constants.BLOCK_SIZE - 1,
//          y + Constants.BLOCK_SIZE - 1 - 4);
//
//    if (screenBlock.isUp() && screenBlock.isRight() && screenBlock.isLeft())
//      g2d.drawLine(x + 4, y + 4, x + Constants.BLOCK_SIZE - 1 - 4, y + 4);
//    else if (screenBlock.isUp() && screenBlock.isRight())
//      g2d.drawLine(x, y + 4, x + Constants.BLOCK_SIZE - 1 - 4, y + 4);
//    else if (screenBlock.isUp() && screenBlock.isLeft())
//      g2d.drawLine(x + 4, y + 4, x + Constants.BLOCK_SIZE - 1, y + 4);
//    else if (screenBlock.isUp())
//      g2d.drawLine(x, y + 4, x + Constants.BLOCK_SIZE - 1, y + 4);
//
//    // Gestion du trait double à l'int�rieur - trait à droite
//    if (screenBlock.isRight() && screenBlock.isUp() && screenBlock.isDown())
//      g2d.drawLine(x + Constants.BLOCK_SIZE - 1 - 4, y + 4, x + Constants.BLOCK_SIZE - 1 - 4,
//          y + Constants.BLOCK_SIZE - 1 - 4);
//    else if (screenBlock.isRight() && screenBlock.isUp())
//      g2d.drawLine(x + Constants.BLOCK_SIZE - 1 - 4, y + 4, x + Constants.BLOCK_SIZE - 1 - 4,
//          y + Constants.BLOCK_SIZE - 1);
//    else if (screenBlock.isRight() && screenBlock.isDown())
//      g2d.drawLine(x + Constants.BLOCK_SIZE - 1 - 4, y, x + Constants.BLOCK_SIZE - 1 - 4,
//          y + Constants.BLOCK_SIZE - 1 - 4);
//    else if (screenBlock.isRight())
//      g2d.drawLine(x + Constants.BLOCK_SIZE - 1 - 4, y, x + Constants.BLOCK_SIZE - 1 - 4,
//          y + Constants.BLOCK_SIZE - 1);
//  }

  /**
   * Display the points into the map
   *
   * @param g2d
   * @param screenBlock
   * @param x
   * @param y
   */
  private static void displayPoints(Graphics2D g2d, ScreenData screenData, int x, int y) {

    ScreenBlock screenBlock = screenData
        .getDataBlock(Utils.convertPointToBlockUnit(new Point(x, y)));

    // affichage du méga point
    if (screenBlock.isPoint() && screenBlock.isMegaPoint()) {
      g2d.setColor(megaPointColor);
      g2d.fillOval(x + Constants.BLOCK_SIZE / 2 - 6, y + Constants.BLOCK_SIZE / 2 - 6, 8, 8);
    }

    // affichage du point de survie des fantômes
    if (screenBlock.isReviverGhostPoint()) {
      g2d.setColor(revivorColor);
      g2d.fillOval(x + Constants.BLOCK_SIZE / 2 - 6, y + Constants.BLOCK_SIZE / 2 - 6, 8, 8);
    }

    // affichage des points
    g2d.setColor(dotColor);
    if (screenBlock.isPoint() && !screenBlock.isMegaPoint()) {
      g2d.fillRect(x + Constants.BLOCK_SIZE / 2 - 4, y + Constants.BLOCK_SIZE / 2 - 4, 4, 4);
    }

    if (screenBlock.isPoint() && !screenBlock.isUp()) {
      g2d.fillRect(x + Constants.BLOCK_SIZE / 2 - 2,
          y + Constants.BLOCK_SIZE / 2 - 2 - Constants.BLOCK_SIZE / 3, 2, 2);
    }

    if (screenBlock.isPoint() && !screenBlock.isDown()) {
      g2d.fillRect(x + Constants.BLOCK_SIZE / 2 - 2,
          y + Constants.BLOCK_SIZE / 2 - 2 + Constants.BLOCK_SIZE / 3, 2, 2);
    }

    if (screenBlock.isPoint() && !screenBlock.isLeft()) {
      g2d.fillRect(x + Constants.BLOCK_SIZE / 2 - 2 - Constants.BLOCK_SIZE / 3,
          y + Constants.BLOCK_SIZE / 2 - 2, 2, 2);
    }

    if (screenBlock.isPoint() && !screenBlock.isRight()) {
      g2d.fillRect(x + Constants.BLOCK_SIZE / 2 - 2 + Constants.BLOCK_SIZE / 3,
          y + Constants.BLOCK_SIZE / 2 - 2, 2, 2);
    }
  }

  /**
   * Constructeur privé
   */
  private ScreenBlockView() {
  }
}
