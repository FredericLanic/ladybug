package com.kycox.ladybug.engine.view.body;

import java.awt.Image;
import java.awt.Point;

import javax.swing.Timer;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.constant.PicturesEnum;
import com.kycox.ladybug.engine.element.pacman.Pacman;
import com.kycox.ladybug.engine.timer.TimerView;

public class PacmanView extends TimerView {
  // Suite de body image
  private BodyImg bodyDownCurrent;
  private BodyImg bodyLeftCurrent;
  private BodyImg bodyRightCurrent;

  private BodyImg bodyUpCurrent;

  // liste d'images pacman regarde en bas
  private BodyImg pacmanDown1        = new BodyImg(PicturesEnum.PACMAN_DEFAULT.getImg());

  private BodyImg pacmanDown2        = new BodyImg(PicturesEnum.PACMAN_DOWN_2.getImg());
  private BodyImg pacmanDown3        = new BodyImg(PicturesEnum.PACMAN_DOWN_3.getImg());

  private BodyImg pacmanDown4        = new BodyImg(PicturesEnum.PACMAN_DOWN_4.getImg());
  private BodyImg pacmanDown5        = new BodyImg(PicturesEnum.PACMAN_DOWN_3.getImg());
  private BodyImg pacmanDown6        = new BodyImg(PicturesEnum.PACMAN_DOWN_2.getImg());
  // liste d'images pacman regarde à gauche
  private BodyImg pacmanLeft1        = new BodyImg(PicturesEnum.PACMAN_DEFAULT.getImg());
  private BodyImg pacmanLeft2        = new BodyImg(PicturesEnum.PACMAN_LEFT_2.getImg());
  private BodyImg pacmanLeft3        = new BodyImg(PicturesEnum.PACMAN_LEFT_3.getImg());

  private BodyImg pacmanLeft4        = new BodyImg(PicturesEnum.PACMAN_LEFT_4.getImg());
  private BodyImg pacmanLeft5        = new BodyImg(PicturesEnum.PACMAN_LEFT_3.getImg());
  private BodyImg pacmanLeft6        = new BodyImg(PicturesEnum.PACMAN_LEFT_2.getImg());
  // liste d'images pacman regarde à doite
  private BodyImg pacmanRight1       = new BodyImg(PicturesEnum.PACMAN_DEFAULT.getImg());
  private BodyImg pacmanRight2       = new BodyImg(PicturesEnum.PACMAN_RIGHT_2.getImg());
  private BodyImg pacmanRight3       = new BodyImg(PicturesEnum.PACMAN_RIGHT_3.getImg());

  private BodyImg pacmanRight4       = new BodyImg(PicturesEnum.PACMAN_RIGHT_4.getImg());
  private BodyImg pacmanRight5       = new BodyImg(PicturesEnum.PACMAN_RIGHT_3.getImg());
  private BodyImg pacmanRight6       = new BodyImg(PicturesEnum.PACMAN_RIGHT_2.getImg());
  // liste d'images pacman regarde en haut
  private BodyImg pacmanUp1          = new BodyImg(PicturesEnum.PACMAN_DEFAULT.getImg());
  private BodyImg pacmanUp2          = new BodyImg(PicturesEnum.PACMAN_UP_2.getImg());
  private BodyImg pacmanUp3          = new BodyImg(PicturesEnum.PACMAN_UP_3.getImg());

  private BodyImg pacmanUp4          = new BodyImg(PicturesEnum.PACMAN_UP_4.getImg());
  private BodyImg pacmanUp5          = new BodyImg(PicturesEnum.PACMAN_UP_3.getImg());
  private BodyImg pacmanUp6          = new BodyImg(PicturesEnum.PACMAN_UP_2.getImg());

  // période en mms
  private int     period             = Constants.PACE * 4;

  /** Timer de l'affichage */
  private Timer   timer;

  // View par défaut à la construction
  private Point   viewDirectionPoint = Constants.POINT_ZERO;

  public PacmanView() {
    pacmanLeft1.setNext(pacmanLeft2);
    pacmanLeft2.setNext(pacmanLeft3);
    pacmanLeft3.setNext(pacmanLeft4);
    pacmanLeft4.setNext(pacmanLeft5);
    pacmanLeft5.setNext(pacmanLeft6);
    pacmanLeft6.setNext(pacmanLeft1);

    pacmanRight1.setNext(pacmanRight2);
    pacmanRight2.setNext(pacmanRight3);
    pacmanRight3.setNext(pacmanRight4);
    pacmanRight4.setNext(pacmanRight5);
    pacmanRight5.setNext(pacmanRight6);
    pacmanRight6.setNext(pacmanRight1);

    pacmanUp1.setNext(pacmanUp2);
    pacmanUp2.setNext(pacmanUp3);
    pacmanUp3.setNext(pacmanUp4);
    pacmanUp4.setNext(pacmanUp5);
    pacmanUp5.setNext(pacmanUp6);
    pacmanUp6.setNext(pacmanUp1);

    pacmanDown1.setNext(pacmanDown2);
    pacmanDown2.setNext(pacmanDown3);
    pacmanDown3.setNext(pacmanDown4);
    pacmanDown4.setNext(pacmanDown5);
    pacmanDown5.setNext(pacmanDown6);
    pacmanDown5.setNext(pacmanDown1);

    bodyDownCurrent = pacmanDown1;
    bodyLeftCurrent = pacmanLeft1;
    bodyRightCurrent = pacmanRight1;
    bodyUpCurrent = pacmanUp1;

    timer = createTimer(period);
    timer.start();
  }

  /**
   * Change la vue de Pacman
   */
  @Override
  public void doAction() {
    if (viewDirectionPoint == Constants.POINT_UP) {
      bodyUpCurrent = bodyUpCurrent.getNext();
    }

    if (viewDirectionPoint == Constants.POINT_RIGHT) {
      bodyRightCurrent = bodyRightCurrent.getNext();
    }

    if (viewDirectionPoint == Constants.POINT_LEFT) {
      bodyLeftCurrent = bodyLeftCurrent.getNext();
    }

    bodyDownCurrent = bodyDownCurrent.getNext();
  }

  /**
   * Retourne l'image de pacman
   * 
   * @param viewDirectionPoint
   * @return
   */
  public Image getNextImage(Pacman pacman) {
    this.viewDirectionPoint = pacman.getViewDirection();

    if (viewDirectionPoint == Constants.POINT_UP) {
      return bodyUpCurrent.getImg();
    }

    if (viewDirectionPoint == Constants.POINT_RIGHT) {
      return bodyRightCurrent.getImg();
    }

    if (viewDirectionPoint == Constants.POINT_LEFT) {
      return bodyLeftCurrent.getImg();
    }

    return bodyDownCurrent.getImg();
  }

}
