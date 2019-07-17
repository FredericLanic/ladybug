package com.kycox.ladybug.engine.view.body;

import java.awt.Image;

import javax.swing.Timer;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.engine.element.ghost.Ghost;
import com.kycox.ladybug.engine.timer.TimerView;

public class GhostFlashView extends TimerView {

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
