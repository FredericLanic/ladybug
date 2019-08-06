package com.kycox.ladybug.level;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public final class Level1 implements ILevel {

//   Représentation du level initial 
//   0000001 : barre à gauche     1 
//   0000010 : barre en haut      2 
//   0000100 : barre à droite     4 
//   0001000 : barre en bas       8
//   0010000 : point             16
//   0100000 : superpower        32
//   1000000 : survivor ghost    64
//  10000000 : double traits    128

  private static final int levelDATA[]      = { 19, 26, 26, 26, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22, 21, 128,
      128, 128, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 17, 26, 10, 26, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
      21, 128, 128, 128, 17, 16, 16, 24, 16, 16, 16, 16, 16, 16, 20, 17, 18, 18, 18, 16, 16, 20, 128, 17, 16, 16, 16,
      16, 16, 20, 17, 16, 16, 16, 16, 16, 20, 128, 17, 16, 16, 16, 16, 24, 20, 25, 16, 16, 16, 24, 24, 28, 128, 25, 24,
      24, 16, 20, 128, 21, 129, 17, 16, 20, 128, 128, 128, 128, 128, 128, 128, 17, 20, 128, 21, 129, 17, 16, 16, 18, 18,
      22, 128, 19, 18, 18, 16, 20, 128, 21, 129, 17, 16, 16, 16, 16, 20, 128, 17, 16, 16, 16, 20, 128, 21, 129, 17, 16,
      16, 16, 16, 20, 128, 17, 16, 16, 16, 20, 128, 21, 129, 17, 16, 16, 16, 16, 16, 18, 16, 16, 16, 16, 20, 128, 21,
      129, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 128, 21, 129, 25, 24, 24, 24, 24, 24, 24, 24, 24, 16, 16, 16,
      18, 20, 137, 136, 136, 136, 136, 136, 136, 136, 136, 136, 25, 24, 24, 24, 28 };
  private int              nbrBlocksPerLine = 15;

  private int              nbrLines         = 15;

// Simplification du niveau pour les tests :
//	private int levelDATA[] = {
//			3, 10, 10, 10, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 6,
//			5, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4,
//			5, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4,
//			5, 0, 0, 0, 1, 0, 0, 8, 0, 0, 0, 0, 0, 0, 4,
//			1, 2, 2, 2, 0, 0, 4, 0, 1, 0, 0, 0, 0, 0, 4,
//			1, 0, 0, 0, 0, 0, 4, 0, 1, 0, 0, 0, 0, 8, 4,
//			9, 0, 0, 0, 8, 8, 12, 0, 9, 8, 8, 0, 4, 0, 5,
//			1, 1, 0, 4, 0, 0, 0, 0, 0, 0, 0, 1, 4, 0, 5,
//			1, 1, 0, 0, 2, 2, 6, 0, 3, 2, 2, 0, 4, 0, 5,
//			1, 1, 0, 0, 0, 0, 4, 0, 1, 0, 0, 0, 4, 0, 5,
//			1, 1, 0, 0, 0, 0, 4, 0, 1, 0, 0, 0, 4, 0, 5,
//			1, 1, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 4, 0, 5,
//			1, 1, 0, 0, 0, 0, 16, 0, 0, 0, 0, 0, 4, 0, 5,
//			1, 9, 8, 8, 8, 8, 8, 8, 8, 8, 0, 0, 0, 2, 4,
//			9, 8, 8, 8, 8, 8, 8, 8, 8, 8, 9, 8, 8, 8, 12
//	};

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
