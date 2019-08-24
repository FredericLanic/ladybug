package com.kycox.ladybug.level;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public abstract class Level implements ILevel {

  // Nombre de blocks par ligne
  protected int nbrBlocksPerLine;

  // Nombre de lignes
  protected int nbrLines;

  // Map
  protected int levelDATA[];

  @Override
  public int getNbrBlocksByLine() {
    return nbrBlocksPerLine;
  }

  @Override
  public int getNbrLines() {
    return nbrLines;
  }

  @Override
  public int[] getMapLevel() {
    return levelDATA.clone();
  }

  @Override
  public List<ScreenBlock> getLstBlocks() {
    List<ScreenBlock> lstBlocks = new ArrayList<>();

    for (int i = 0; i < levelDATA.length; i++) {
      int         x     = i % nbrBlocksPerLine;
      int         y     = i / nbrBlocksPerLine;

      ScreenBlock block = new ScreenBlock(levelDATA[i]);
      block.setCoordinate(new Point(x, y));
      lstBlocks.add(block);
    }

    return lstBlocks;
  }
}
