package com.kycox.ladybug.engine.view.body;

import java.awt.Image;

import com.kycox.ladybug.constant.PicturesEnum;
import com.kycox.ladybug.engine.element.ghost.Ghost;

public class GhostScaredView {

  private static GhostScaredView ghostScaredView = new GhostScaredView();

  public static GhostScaredView getInstance() {
    return ghostScaredView;
  }

  /**
   * Constructeur privé pour assurer le singleton
   */

  private GhostScaredView() {
  }

  public Image getImage(Ghost ghost) {

    Image ghostImg;

    if (ghost.getDirection().x > 0) {
      // image générique du fantôme qui a peut : les yeux à droite
      ghostImg = PicturesEnum.GHOST_SCARED_RIGHT_EYES.getImg();
    } else {
      // image générique du fantôme qui a peut : les yeux é gauche
      ghostImg = PicturesEnum.GHOST_SCARED_LEFT_EYES.getImg();
    }

    return ghostImg;
  }
}
