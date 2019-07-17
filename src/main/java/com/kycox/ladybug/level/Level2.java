package com.kycox.ladybug.level;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Level2 implements ILevel {

  // FIXME : il y a des méga points qui trainent ....
  private static final int levelDATA[]      = { 19, 26, 26, 22, 9, 12, 19, 26, 22, 9, 12, 19, 26, 26, 22, 21, 11, 14,
      17, 26, 26, 20, 15, 17, 26, 26, 20, 11, 14, 21, 17, 26, 26, 20, 11, 6, 17, 26, 20, 3, 14, 17, 26, 26, 20, 21, 3,
      6, 25, 22, 5, 21, 7, 21, 5, 19, 28, 3, 6, 21, 21, 9, 8, 14, 21, 13, 21, 5, 21, 13, 21, 11, 8, 12, 21, 25, 18, 26,
      18, 24, 18, 28, 5, 25, 18, 24, 18, 26, 18, 28, 6, 21, 7, 21, 7, 21, 11, 8, 14, 21, 7, 21, 7, 21, 03, 4, 21, 5, 21,
      5, 21, 11, 10, 14, 21, 5, 21, 5, 21, 1, 12, 21, 13, 21, 13, 21, 11, 10, 14, 21, 13, 21, 13, 21, 9, 19, 24, 26, 24,
      26, 16, 26, 18, 26, 16, 26, 24, 26, 24, 22, 21, 3, 2, 2, 6, 21, 15, 21, 15, 21, 3, 2, 2, 06, 21, 21, 9, 8, 8, 4,
      17, 26, 8, 26, 20, 1, 8, 8, 12, 21, 17, 26, 26, 22, 13, 21, 11, 2, 14, 21, 13, 19, 26, 26, 20, 21, 11, 14, 17, 26,
      24, 22, 13, 19, 24, 26, 20, 11, 14, 21, 25, 26, 26, 28, 3, 6, 25, 26, 28, 3, 6, 25, 26, 26, 28 };

  private int              nbrBlocksPerLine = 15;

  private int              nbrLines         = 15;

  @Override
  public Point getGhostRegenerateBlockPoint() {
    return new Point(2, 2);
  }

  @Override
  public Point getInitPacmanBlockPos() {
    return new Point(7, 11);
  }

  @Override
  public List<ScreenBlock> getLstBlocks() {
    List<ScreenBlock> lstBlocks = new ArrayList<>();

    for (int i = 0; i < levelDATA.length; i++) {
      int x = i % nbrBlocksPerLine;
      int y = i / nbrBlocksPerLine;

      ScreenBlock block = new ScreenBlock(levelDATA[i]);
      block.setCoordinate(new Point(x, y));
      lstBlocks.add(block);
    }

    return lstBlocks;
  }

  @Override
  public int[] getMapLevel() {
    return levelDATA.clone();
  }

  @Override
  public int getNbrBlocksByLine() {
    return nbrBlocksPerLine;
  }

  @Override
  public int getNbrLines() {
    return nbrLines;
  }

  @Override
  public int getNbrMegaPoints() {
    return 2;
  }

}
