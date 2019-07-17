package com.kycox.ladybug.level;

import java.awt.Point;
import java.util.List;

public interface ILevel {
  public Point getGhostRegenerateBlockPoint();

  public Point getInitPacmanBlockPos();

  public List<ScreenBlock> getLstBlocks();

  public int[] getMapLevel();

  public int getNbrBlocksByLine();

  public int getNbrLines();

  public int getNbrMegaPoints();
}