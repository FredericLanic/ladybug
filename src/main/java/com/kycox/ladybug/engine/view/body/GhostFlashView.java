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
package com.kycox.ladybug.engine.view.body;

import java.awt.Image;

import javax.swing.Timer;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.engine.element.ghost.Ghost;
import com.kycox.ladybug.engine.timer.TimerView;

public class GhostFlashView implements TimerView {

  private static GhostFlashView ghostFlashView = new GhostFlashView();

  public static GhostFlashView getInstance() {
    return ghostFlashView;
  }

  /**
   * Période de clignottement
   */
  private int     period = Constants.PACE * 4;

  private boolean showScared;

  /**
   * Timer du clignottement
   */
  private Timer   timer;

  /**
   * Constructeur privé pour assurer le singleton
   */
  private GhostFlashView() {
    timer = createTimer(period);
    timer.start();
  }

  @Override
  public void doAction() {
    showScared = !showScared;
  }

  /**
   * Retourne le fantôme qui clignotte
   *
   * @param ghost
   * @return
   */
  public Image getImage(Ghost ghost) {
    if (showScared)
      return GhostScaredView.getInstance().getImage(ghost);
    else
      return GhostDefautlView.getInstance().getImage(ghost);
  }
}
