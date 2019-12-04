package com.kycox.ladybug.constant;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Cinématique pour afficher / entendre lors de la mort de Ladybug
 *
 */
public class KinematicLadybugDeath {
  // bip de la cinématique
  private int                bip         = 0;

  // Liste des images des images à afficher lors de la cinématique
  private List<PicturesEnum> lstPictures = new ArrayList<>(
      Arrays.asList(PicturesEnum.LADYBUG_DEFAULT, PicturesEnum.LADYBUG_UP_2,
          PicturesEnum.LADYBUG_UP_3, PicturesEnum.LADYBUG_UP_4, PicturesEnum.LADYBUG_UP_5,
          PicturesEnum.LADYBUG_UP_6, PicturesEnum.LADYBUG_UP_7, PicturesEnum.LADYBUG_UP_8,
          PicturesEnum.LADYBUG_UP_9, PicturesEnum.LADYBUG_UP_10, PicturesEnum.LADYBUG_UP_11));

  private long               timeInMMs   = 0;

  /**
   * Contructeur privé
   */
  public KinematicLadybugDeath(long timeInMMs) {
    this.timeInMMs = timeInMMs;
  }

  /**
   * Retourne le bip de la cinématique
   */
  public int getBip() {
    return bip;
  }

  /**
   * Retourne l'image à afficher quand Ladybug meurt.
   *
   * @return
   */
  public Image getImage() {
    // �chantillonnage en fonction du timer du modèle
    long nbrBips        = timeInMMs / Constants.PACE;
    // R�cup�ration du nombre d'images
    int  nbrImages      = lstPictures.size();

    // Calcul du nombre de bip du timer du modèle par images
    long nbrBitPerImage = nbrBips / nbrImages;
    // Calcule de l'index de l'image à afficher
    int  numImage       = (int) (bip / nbrBitPerImage);

    // si le calcul est long, il se peut que l'index dépasse... on le rajuste
    if (numImage >= nbrImages)
      numImage = nbrImages - 1;

    return lstPictures.get(numImage).getImg();
  }

  /**
   * Incr�mentation du Bip; utilisé par le modèle
   */
  public void incrementBip() {
    bip++;
  }

  /**
   * Initialise le bip de la cinématique
   */
  public void initBip() {
    bip = 0;
  }

  /**
   * Test la fin de la musique de la mort de Ladybug; utilisé par le modèle
   *
   * @return
   */
  public boolean isEnd() {
    return bip * Constants.PACE >= timeInMMs;
  }
}
