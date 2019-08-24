package com.kycox.ladybug.level;

import java.awt.Point;

public final class Level1 extends Level {

  // Représentation du level initial
  // 0000001 : barre à gauche 1
  // 0000010 : barre en haut 2
  // 0000100 : barre à droite 4
  // 0001000 : barre en bas 8
  // 0010000 : point 16
  // 0100000 : superpower 32
  // 1000000 : survivor ghost 64
  // 10000000 : double traits 128

  public Level1() {
    // nombre de blocks par ligne
    nbrBlocksPerLine = 15;
    // nombre de lignes
    nbrLines = 15;

    levelDATA = new int[] { 19, 26, 26, 26, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22, 21, 128,
        128, 128, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 17, 26, 10, 26, 16, 16, 16, 16, 16,
        16, 16, 16, 16, 16, 20, 21, 128, 128, 128, 17, 16, 16, 24, 16, 16, 16, 16, 16, 16, 20, 17,
        18, 18, 18, 16, 16, 20, 128, 17, 16, 16, 16, 16, 16, 20, 17, 16, 16, 16, 16, 16, 20, 128,
        17, 16, 16, 16, 16, 24, 20, 25, 16, 16, 16, 24, 24, 28, 128, 25, 24, 24, 16, 20, 128, 21,
        129, 17, 16, 20, 128, 128, 128, 128, 128, 128, 128, 17, 20, 128, 21, 129, 17, 16, 16, 18,
        18, 22, 128, 19, 18, 18, 16, 20, 128, 21, 129, 17, 16, 16, 16, 16, 20, 128, 17, 16, 16, 16,
        20, 128, 21, 129, 17, 16, 16, 16, 16, 20, 128, 17, 16, 16, 16, 20, 128, 21, 129, 17, 16, 16,
        16, 16, 16, 18, 16, 16, 16, 16, 20, 128, 21, 129, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16,
        16, 20, 128, 21, 129, 25, 24, 24, 24, 24, 24, 24, 24, 24, 16, 16, 16, 18, 20, 137, 136, 136,
        136, 136, 136, 136, 136, 136, 136, 25, 24, 24, 24, 28 };
  }

  @Override
  public Point getGhostRegenerateBlockPoint() {
    return new Point(2, 2);
  }

  @Override
  public Point getInitPacmanBlockPos() {
    return new Point(7, 11);
  }

  @Override
  public int getNbrMegaPoints() {
    return 2;
  }

}
