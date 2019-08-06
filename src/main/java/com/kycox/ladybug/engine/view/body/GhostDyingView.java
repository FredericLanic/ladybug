package com.kycox.ladybug.engine.view.body;

import java.awt.Image;

import javax.swing.Timer;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.constant.PicturesEnum;
import com.kycox.ladybug.engine.timer.TimerView;

public class GhostDyingView extends TimerView {

  private static GhostDyingView ghostEatenlView = new GhostDyingView();

  public static GhostDyingView getInstance() {
    return ghostEatenlView;
  }

  private BodyImg currentImg;

  private BodyImg leftEyes  = new BodyImg(PicturesEnum.ONLY_EYES_LEFT_EYES.getImg());

  // period en mms
  private int     period    = 4 * Constants.PACE;

  private BodyImg rightEyes = new BodyImg(PicturesEnum.ONLY_EYES_RIGHT_EYES.getImg());

  // Timer de l'affichage
  private Timer   timer;

  /**
   * Constructeur privé pour assurer le singleton
   */
  private GhostDyingView() {
    currentImg = leftEyes;

    leftEyes.setNext(rightEyes);
    rightEyes.setNext(leftEyes);

    timer = createTimer(period);
    timer.start();
  }

  @Override
  public void doAction() {
    currentImg = currentImg.getNext();
  }

  /**
   * Retourne l'image à afficher
   * 
   * @param ghost
   * @return
   */
  public Image getImage() {
    return currentImg.getImg();
  }
}
