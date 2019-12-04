package com.kycox.ladybug.engine.element.ghost.action;

import com.kycox.ladybug.engine.element.ghost.Ghost;

public class GhostActions {

  private boolean eaten       = false;

  private boolean hasEatenLadybug   = false;

  private Ghost   ghost;

  private boolean regenerated = false;

  public Ghost getGhost() {
    return ghost;
  }

  public boolean isEaten() {
    return eaten;
  }

  public boolean hasEatenLadybug() {
    return hasEatenLadybug;
  }

  public boolean isRegenerated() {
    return regenerated;
  }

  public void setEaten(boolean eaten) {
    this.eaten = eaten;
  }

  public void setHasEatenLadybug(boolean eatLadybug) {
    this.hasEatenLadybug = eatLadybug;
  }

  public void setGhost(Ghost ghost) {
    this.ghost = ghost;
  }

  public void setRegenerated(boolean regenerated) {
    this.regenerated = regenerated;
  }

}
