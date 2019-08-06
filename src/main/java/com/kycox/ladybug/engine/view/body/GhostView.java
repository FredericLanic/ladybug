package com.kycox.ladybug.engine.view.body;

import java.awt.Image;

import com.kycox.ladybug.engine.element.ghost.Ghost;

public class GhostView {

  public Image getImage(Ghost ghost) {

    /**
     * Affiche les images du fantômes "vivants"
     * 
     * @param ghost
     * @return
     */
    Image ghostImg;

    switch (ghost.getStatus()) {
    case FLASH:
      ghostImg = GhostFlashView.getInstance().getImage(ghost);
      break;
    case SCARED:
      ghostImg = GhostScaredView.getInstance().getImage(ghost);
      break;
    case DYING:
      ghostImg = GhostDyingView.getInstance().getImage();
      break;
    default:
      ghostImg = GhostDefautlView.getInstance().getImage(ghost);
    }
    return ghostImg;
  }

}
