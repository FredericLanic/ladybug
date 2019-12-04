package com.kycox.ladybug.engine.element.ladybug.set;

import java.util.function.Predicate;

import com.kycox.ladybug.engine.element.ladybug.Ladybug;

/**
 * Enumération des états de ladybug
 *
 *
 */
public enum LadybugStatusEnum {
  DEAD, DYING, NORMAL;

  public static Predicate<Ladybug> isDead() {
    return p -> p.getStatus().equals(LadybugStatusEnum.DEAD);
  }

  public static Predicate<Ladybug> isDying() {
    return p -> p.getStatus().equals(LadybugStatusEnum.DYING);
  }

  public static Predicate<Ladybug> isNormal() {
    return p -> p.getStatus().equals(LadybugStatusEnum.NORMAL);
  }
}
