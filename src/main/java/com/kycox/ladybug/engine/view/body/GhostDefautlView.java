package com.kycox.ladybug.engine.view.body;

import java.awt.Image;

import com.kycox.ladybug.engine.element.ghost.Ghost;

public class GhostDefautlView {

  private static GhostDefautlView ghostDefautlView = new GhostDefautlView();

  public static GhostDefautlView getInstance() {
    return ghostDefautlView;
  }

  /**
   * Constructeur priv� pour assurer le singleton
   */

  private GhostDefautlView() {
  }

  public Image getImage(Ghost ghost) {

    Image ghostImg;

    if (ghost.getDirection().x > 0) {
      // image g�n�rique du fant�me qui a peut : les yeux � droite
      ghostImg = ghost.getGhostSettings().getGhostRightEyesImg();
    } else {
      // image g�n�rique du fant�me qui a peut : les yeux � gauche
      ghostImg = ghost.getGhostSettings().getGhostLeftEyesImg();
    }

    return ghostImg;
  }
}
