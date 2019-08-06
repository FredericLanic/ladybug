package com.kycox.ladybug.constant;

import java.awt.Image;

import javax.swing.ImageIcon;

/**
 * Enumération des images à afficher dans le jeu
 * 
 * @author kycox
 *
 */
public enum PicturesEnum {
  GHOST_BLINKY_LEFT_EYES("GhostRedLeft.gif"),
  GHOST_BLINKY_RIGHT_EYES("GhostRedRight.gif"),
  GHOST_CLYDE_LEFT_EYES("GhostOrangeLeft.gif"),
  GHOST_CLYDE_RIGHT_EYES("GhostOrangeRight.gif"),
  GHOST_INKY_LEFT_EYES("GhostBlueLeft.gif"),
  GHOST_INKY_RIGHT_EYES("GhostBlueRight.gif"),
  GHOST_PINKY_LEFT_EYES("GhostPinkLeft.gif"),
  GHOST_PINKY_RIGHT_EYES("GhostPinkRight.gif"),
  GHOST_SCARED_LEFT_EYES("GhostScared2.gif"),
  GHOST_SCARED_RIGHT_EYES("GhostScared1.gif"),
  ONLY_EYES_LEFT_EYES("Eyes2.gif"),
  ONLY_EYES_RIGHT_EYES("Eyes1.gif"),
  PACMAN_DEFAULT("PacMan1.gif"),
  PACMAN_DOWN_2("PacMan2down.gif"),
  PACMAN_DOWN_3("PacMan3down.gif"),
  PACMAN_DOWN_4("PacMan4down.gif"),
  PACMAN_LEFT_2("PacMan2left.gif"),
  PACMAN_LEFT_3("PacMan3left.gif"),
  PACMAN_LEFT_4("PacMan4left.gif"),
  PACMAN_RIGHT_2("PacMan2right.gif"),
  PACMAN_RIGHT_3("PacMan3right.gif"),
  PACMAN_RIGHT_4("PacMan4right.gif"),
  PACMAN_UP_10("PacMan10up.gif"),
  PACMAN_UP_11("PacMan11up.gif"),
  PACMAN_UP_2("PacMan2up.gif"),
  PACMAN_UP_3("PacMan3up.gif"),
  PACMAN_UP_4("PacMan4up.gif"),
  PACMAN_UP_5("PacMan5up.gif"),
  PACMAN_UP_6("PacMan6up.gif"),
  PACMAN_UP_7("PacMan7up.gif"),
  PACMAN_UP_8("PacMan8up.gif"),
  PACMAN_UP_9("PacMan9up.gif");

  private Image img = null;

  /**
   * Constructeur
   * 
   * @param fileName
   */
  private PicturesEnum(String fileName) {
    img = new ImageIcon(PicturesEnum.class.getClassLoader().getResource("images/" + fileName)).getImage();
  }

  /**
   * Retourne la valeur de l'image
   * 
   * @return
   */
  public Image getImg() {
    return img;
  }
}
