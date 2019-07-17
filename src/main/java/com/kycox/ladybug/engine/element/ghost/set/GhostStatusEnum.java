package com.kycox.ladybug.engine.element.ghost.set;

import java.util.function.Predicate;

import com.kycox.ladybug.engine.element.ghost.Ghost;

/**
 * Enum�ration des �tats des fant�mes
 *
 */
public enum GhostStatusEnum {
  DYING, FLASH, NORMAL, REGENERATING, SCARED;

  /**
   * Predicate
   * 
   * @return
   */
  public static Predicate<Ghost> isDying() {
    return g -> g.getStatus().equals(GhostStatusEnum.DYING);
  }

  // FIXME : to remove
  public static Predicate<Ghost> isFlashing() {
    return g -> g.getStatus().equals(GhostStatusEnum.FLASH);
  }

  public static Predicate<Ghost> isNormal() {
    return g -> g.getStatus().equals(GhostStatusEnum.NORMAL);
  }

  public static Predicate<Ghost> isRegenerating() {
    return g -> g.getStatus().equals(GhostStatusEnum.REGENERATING);
  }

  // FIXME : to remove
  public static Predicate<Ghost> isScared() {
    return g -> g.getStatus().equals(GhostStatusEnum.SCARED);
  }
}
