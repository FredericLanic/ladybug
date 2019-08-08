package com.kycox.ladybug.engine.view.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.level.ScreenBlock;
import com.kycox.ladybug.tools.Utils;

public class ScreenBlockView {

  // Couleur d'un point
  private static final Color dotColor       = new Color(192, 192, 0);

  // Couleur du labyrinthe
  private static final Color megaPointColor = new Color(255, 128, 0);

  // Couleur du lieu revivor
  private static final Color revivorColor   = new Color(128, 255, 255);

  public static void display(Graphics2D g2d, ScreenBlock screenBlock, int x, int y) {

    displayBorders(g2d, screenBlock, x, y);
    displayPoints(g2d, screenBlock, x, y);

//    if (screenBlock.isDoubleLines())
//      displayInsideLines(g2d, screenBlock);
  }

  /**
   * Display the points into the map
   * 
   * @param g2d
   * @param screenBlock
   * @param x
   * @param y
   */
  private static void displayPoints(Graphics2D g2d, ScreenBlock screenBlock, int x, int y) {
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
      g2d.fillRect(x + Constants.BLOCK_SIZE / 2 - 2, y + Constants.BLOCK_SIZE / 2 - 2, 2, 2);
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
   * Display the borders into the map
   * 
   * @param g2d
   * @param screenBlock
   * @param x
   * @param y
   */
  private static void displayBorders(Graphics2D g2d, ScreenBlock screenBlock, int x, int y) {

    g2d.setColor(Color.GREEN);
    g2d.setStroke(new BasicStroke(2));

    // affichage de la barre à gauche
    if (screenBlock.isLeft()) {
      g2d.drawLine(x, y, x, y + Constants.BLOCK_SIZE);
    }

    // affichage de la barre en haut
    if (screenBlock.isUp()) {
      g2d.drawLine(x, y, x + Constants.BLOCK_SIZE, y);
    }

    // affichage de la barre à droite
    if (screenBlock.isRight()) {
      g2d.drawLine(x + Constants.BLOCK_SIZE, y, x + Constants.BLOCK_SIZE, y + Constants.BLOCK_SIZE);
    }

    // affichage de la barre en bas
    if (screenBlock.isDown()) {
      g2d.drawLine(x, y + Constants.BLOCK_SIZE, x + Constants.BLOCK_SIZE, y + Constants.BLOCK_SIZE);
    }

    int rayon = Constants.BLOCK_SIZE;

    if (screenBlock.isLeft() && screenBlock.isUp()) {
      g2d.drawArc(x, y, rayon, rayon, 90, 90);
    }

    if (screenBlock.isLeft() && screenBlock.isDown()) {
      g2d.drawArc(x, y, rayon, rayon, 270, -90);
    }

    if (screenBlock.isRight() && screenBlock.isUp()) {
      g2d.drawArc(x, y, rayon, rayon, 0, 90);
    }

    if (screenBlock.isRight() && screenBlock.isDown()) {
      g2d.drawArc(x, y, rayon, rayon, 270, 90);
    }
  }

  /**
   * Display double lines
   * 
   * @param g2d
   * @param screenBlock
   * @param x
   * @param y
   */
  private static void displayInsideLines(Graphics2D g2d, ScreenBlock screenBlock) {
    g2d.setColor(Color.YELLOW);

    Point position = Utils.convertPointToGraphicUnit(screenBlock.getCoordinate());
    int   x        = position.x;
    int   y        = position.y;

    // Gestion du trait double à l'intérieur - trait à gauche
    if (screenBlock.isLeft() && screenBlock.isUp() && screenBlock.isDown())
      g2d.drawLine(x + 4, y + 4, x + 4, y + Constants.BLOCK_SIZE - 1 - 4);
    else if (screenBlock.isLeft() && screenBlock.isUp())
      g2d.drawLine(x + 4, y + 4, x + 4, y + Constants.BLOCK_SIZE - 1);
    else if (screenBlock.isLeft() && screenBlock.isDown())
      g2d.drawLine(x + 4, y, x + 4, y + Constants.BLOCK_SIZE - 1 - 4);
    else if (screenBlock.isLeft())
      g2d.drawLine(x + 4, y, x + 4, y + Constants.BLOCK_SIZE - 1);

    if (screenBlock.isDown() && screenBlock.isRight() && screenBlock.isLeft())
      g2d.drawLine(x + 4, y + Constants.BLOCK_SIZE - 1 - 4, x + Constants.BLOCK_SIZE - 1 - 4,
          y + Constants.BLOCK_SIZE - 1 - 4);
    else if (screenBlock.isDown() && screenBlock.isRight())
      g2d.drawLine(x, y + Constants.BLOCK_SIZE - 1 - 4, x + Constants.BLOCK_SIZE - 1 - 4,
          y + Constants.BLOCK_SIZE - 1 - 4);
    else if (screenBlock.isDown() && screenBlock.isLeft())
      g2d.drawLine(x + 4, y + Constants.BLOCK_SIZE - 1 - 4, x + Constants.BLOCK_SIZE - 1,
          y + Constants.BLOCK_SIZE - 1 - 4);
    else if (screenBlock.isDown())
      g2d.drawLine(x, y + Constants.BLOCK_SIZE - 1 - 4, x + Constants.BLOCK_SIZE - 1,
          y + Constants.BLOCK_SIZE - 1 - 4);

    if (screenBlock.isUp() && screenBlock.isRight() && screenBlock.isLeft())
      g2d.drawLine(x + 4, y + 4, x + Constants.BLOCK_SIZE - 1 - 4, y + 4);
    else if (screenBlock.isUp() && screenBlock.isRight())
      g2d.drawLine(x, y + 4, x + Constants.BLOCK_SIZE - 1 - 4, y + 4);
    else if (screenBlock.isUp() && screenBlock.isLeft())
      g2d.drawLine(x + 4, y + 4, x + Constants.BLOCK_SIZE - 1, y + 4);
    else if (screenBlock.isUp())
      g2d.drawLine(x, y + 4, x + Constants.BLOCK_SIZE - 1, y + 4);

    // Gestion du trait double à l'int�rieur - trait à droite
    if (screenBlock.isRight() && screenBlock.isUp() && screenBlock.isDown())
      g2d.drawLine(x + Constants.BLOCK_SIZE - 1 - 4, y + 4, x + Constants.BLOCK_SIZE - 1 - 4,
          y + Constants.BLOCK_SIZE - 1 - 4);
    else if (screenBlock.isRight() && screenBlock.isUp())
      g2d.drawLine(x + Constants.BLOCK_SIZE - 1 - 4, y + 4, x + Constants.BLOCK_SIZE - 1 - 4,
          y + Constants.BLOCK_SIZE - 1);
    else if (screenBlock.isRight() && screenBlock.isDown())
      g2d.drawLine(x + Constants.BLOCK_SIZE - 1 - 4, y, x + Constants.BLOCK_SIZE - 1 - 4,
          y + Constants.BLOCK_SIZE - 1 - 4);
    else if (screenBlock.isRight())
      g2d.drawLine(x + Constants.BLOCK_SIZE - 1 - 4, y, x + Constants.BLOCK_SIZE - 1 - 4,
          y + Constants.BLOCK_SIZE - 1);
  }

  /**
   * Constructeur privé
   */
  private ScreenBlockView() {
  }
}
