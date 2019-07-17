package com.kycox.ladybug.engine.view.body;

import java.awt.Image;

import com.kycox.ladybug.engine.element.ghost.Ghost;

public class GhostDefautlView {

  private static GhostDefautlView ghostDefautlView = new GhostDefautlView();

  public static GhostDefautlView getInstance() {
    return ghostDefautlView;
  }

  /**
   * Constructeur privé pour assurer le singleton
   */

  private GhostDefautlView() {
  }

  public Image getImage(Ghost ghost) {

    Image ghostImg;

    if (ghost.getDirection().x > 0) {
      // image générique du fantôme qui a peut : les yeux à droite
      ghostImg = ghost.getGhostSettings().getGhostRightEyesImg();
    } else {
      // image générique du fantôme qui a peut : les yeux à gauche
      ghostImg = ghost.getGhostSettings().getGhostLeftEyesImg();
    }

    return ghostImg;
  }
}
