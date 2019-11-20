package com.kycox.ladybug.engine.element.ghost.set;

import java.awt.Image;
import java.util.Arrays;
import java.util.List;

import com.kycox.ladybug.constant.PicturesEnum;
import com.kycox.ladybug.engine.element.ghost.behavious.GhostBehaviousEnum;

/**
 * Setting spécifique des fantômes : Blinky, Clyde, Inky, Pinky
 *
 * @author kycox
 *
 */
public enum GhostsSettingsEnum {
  // cf : https://fr.wikipedia.org/wiki/Pac-Man
  // Blinky (AGGRESIVE - rouge) attaque directement Pac Man. Il suit Pac-Man comme
  // son ombre.
  // Pinky (SMART - rose) a tendance à se mettre en embuscade. Elle vise l'endroit
  // où va se trouver Pac-Man.
  // Inky (CAPRICIOUS - bleu) est capricieux. De temps en temps, il part dans la
  // direction opposée de Pac-Man.
  // Clyde (INDIFFERENT - orange) feint l'indifférence. De temps en temps, il
  // choisit une direction au hasard (qui peut être celle de Pac-Man).
  BLINKY(PicturesEnum.GHOST_BLINKY_RIGHT_EYES, PicturesEnum.GHOST_BLINKY_LEFT_EYES,
      GhostBehaviousEnum.AGGRESSIVE, true),
  CLYDE(PicturesEnum.GHOST_CLYDE_RIGHT_EYES, PicturesEnum.GHOST_CLYDE_LEFT_EYES,
      GhostBehaviousEnum.STUPID, true),
  INKY(PicturesEnum.GHOST_INKY_RIGHT_EYES, PicturesEnum.GHOST_INKY_LEFT_EYES,
      GhostBehaviousEnum.STUPID, true),
  PINKY(PicturesEnum.GHOST_PINKY_RIGHT_EYES, PicturesEnum.GHOST_PINKY_LEFT_EYES,
      GhostBehaviousEnum.SMART, true);

  /**
   * Initialise la configuration isComputed pour tous les settings des fantômes
   *
   * @param isComputed
   */
  public static void initIsComputed(boolean isComputed) {
    List<GhostsSettingsEnum> lstEnum = Arrays.asList(GhostsSettingsEnum.class.getEnumConstants());
    lstEnum.stream().forEach(e -> e.isComputed = isComputed);
  }

  private GhostBehaviousEnum behavious;

  private boolean            isComputed;

  private PicturesEnum       leftEyesImg;

  private PicturesEnum       rightEyesImg;

  /**
   * Constructeur
   *
   * @param ghostBehavior
   * @param leftEyesImg
   * @param rightEyesImg
   */
  private GhostsSettingsEnum(PicturesEnum leftEyesImg, PicturesEnum rightEyesImg,
      GhostBehaviousEnum behavious, boolean isComputed) {
    this.leftEyesImg = leftEyesImg;
    this.rightEyesImg = rightEyesImg;
    this.behavious = behavious;
    this.isComputed = isComputed;
  }

  /**
   * Retourne le comportement du fantôme
   *
   * @return
   */
  public GhostBehaviousEnum getBehavious() {
    return behavious;
  }

  /**
   * Retourne l'image du fantômes les yeux à gauche
   *
   * @return
   */
  public Image getGhostLeftEyesImg() {
    return leftEyesImg.getImg();
  }

  /**
   * Retourne l'image du fantômes les yeux à droite
   *
   * @return
   */
  public Image getGhostRightEyesImg() {
    return rightEyesImg.getImg();
  }

  public boolean isComputed() {
    return isComputed;
  }

  public void setComputed(boolean isComputed) {
    this.isComputed = isComputed;
  }

}
