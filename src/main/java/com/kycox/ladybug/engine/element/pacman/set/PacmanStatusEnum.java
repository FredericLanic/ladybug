package com.kycox.ladybug.engine.element.pacman.set;

import java.util.function.Predicate;

import com.kycox.ladybug.engine.element.pacman.Pacman;

/**
 * Enumération des états de pacman
 *
 * 
 */
public enum PacmanStatusEnum {
  DEAD, DYING, NORMAL;

  public static Predicate<Pacman> isDead() {
    return p -> p.getStatus().equals(PacmanStatusEnum.DEAD);
  }

  public static Predicate<Pacman> isDying() {
    return p -> p.getStatus().equals(PacmanStatusEnum.DYING);
  }

  public static Predicate<Pacman> isNormal() {
    return p -> p.getStatus().equals(PacmanStatusEnum.NORMAL);
  }
}
