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
  GHOST_BLINKY_LEFT_EYES("GhostRedLeft.png"), GHOST_BLINKY_RIGHT_EYES("GhostRedRight.png"),
  GHOST_CLYDE_LEFT_EYES("GhostOrangeLeft.png"), GHOST_CLYDE_RIGHT_EYES("GhostOrangeRight.png"),
  GHOST_INKY_LEFT_EYES("GhostBlueLeft.png"), GHOST_INKY_RIGHT_EYES("GhostBlueRight.png"),
  GHOST_PINKY_LEFT_EYES("GhostPinkLeft.png"), GHOST_PINKY_RIGHT_EYES("GhostPinkRight.png"),
  GHOST_SCARED_LEFT_EYES("GhostScared1.png"), GHOST_SCARED_RIGHT_EYES("GhostScared2.png"),
  LADYBUG_DEFAULT("Ladybug1.gif"), LADYBUG_DOWN_2("Ladybug2down.gif"),
  LADYBUG_DOWN_3("Ladybug3down.gif"), LADYBUG_DOWN_4("Ladybug4down.gif"),
  LADYBUG_LEFT_2("Ladybug2left.gif"), LADYBUG_LEFT_3("Ladybug3left.gif"),
  LADYBUG_LEFT_4("Ladybug4left.gif"), LADYBUG_RIGHT_2("Ladybug2right.gif"),
  LADYBUG_RIGHT_3("Ladybug3right.gif"), LADYBUG_RIGHT_4("Ladybug4right.gif"),
  LADYBUG_UP_10("Ladybug10up.gif"), LADYBUG_UP_11("Ladybug11up.gif"),
  LADYBUG_UP_2("Ladybug2up.gif"), LADYBUG_UP_3("Ladybug3up.gif"), LADYBUG_UP_4("Ladybug4up.gif"),
  LADYBUG_UP_5("Ladybug5up.gif"), LADYBUG_UP_6("Ladybug6up.gif"), LADYBUG_UP_7("Ladybug7up.gif"),
  LADYBUG_UP_8("Ladybug8up.gif"), LADYBUG_UP_9("Ladybug9up.gif"), ONLY_EYES_LEFT_EYES("Eyes2.gif"),
  ONLY_EYES_RIGHT_EYES("Eyes1.gif");

  private Image img = null;

  /**
   * Constructeur
   *
   * @param fileName
   */
  private PicturesEnum(String fileName) {
    img = new ImageIcon(PicturesEnum.class.getClassLoader().getResource("images/" + fileName))
        .getImage();
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
