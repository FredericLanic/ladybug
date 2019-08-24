package com.kycox.ladybug.level;

import java.awt.Point;

public class Level3 extends Level {

  public Level3() {
    // nombre de blocks par ligne
    nbrBlocksPerLine = 15;
    // nombre de lignes
    nbrLines = 15;

    levelDATA = new int[] { 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 6, 1, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 4, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 1, 0, 0, 0, 0, 0, 0, 16, 0,
        0, 0, 0, 0, 0, 4, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 4, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 4, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 4, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 1, 4, 139, 138, 142, 1, 0, 0, 0, 0, 0,
        0, 0, 0, 4, 1, 8, 2, 10, 10, 8, 0, 0, 0, 0, 0, 0, 0, 0, 4, 5, 143, 5, 139, 138, 142, 0, 0,
        0, 0, 0, 0, 0, 0, 4, 9, 10, 8, 10, 10, 10, 8, 8, 8, 8, 8, 8, 8, 8, 12 };
  }

  @Override
  public Point getGhostRegenerateBlockPoint() {
    return new Point(2, 3);
  }

  @Override
  public Point getInitPacmanBlockPos() {
    return new Point(2, 3);
  }

  @Override
  public int getNbrMegaPoints() {
    return 2;
  }

}
