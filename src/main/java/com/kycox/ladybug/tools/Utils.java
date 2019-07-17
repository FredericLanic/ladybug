package com.kycox.ladybug.tools;

import java.awt.Point;

import com.kycox.ladybug.constant.Constants;

public final class Utils {

  /**
   * Converti un point du graphique en un block
   * 
   * @param point
   * @return
   */
  public static Point convertPointToBlockUnit(Point point) {
    return new Point(point.x / Constants.BLOCK_SIZE, point.y / Constants.BLOCK_SIZE);
  }

  /**
   * Converti un block en un point du graphique
   * 
   * @param point
   * @return
   */
  public static Point convertPointToGraphicUnit(Point point) {
    return new Point(point.x * Constants.BLOCK_SIZE, point.y * Constants.BLOCK_SIZE);
  }

  /**
   * Constructeur privé
   */
  private Utils() {
  }

}
