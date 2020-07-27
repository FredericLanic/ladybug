/**
  Copyright (C) 2019  Frédéric Lanic frederic.lanic@outlook.fr

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.kycox.ladybug.view.body;

import java.awt.Image;

import com.kycox.ladybug.body.ghost.Ghost;
import com.kycox.ladybug.constant.PicturesEnum;

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
