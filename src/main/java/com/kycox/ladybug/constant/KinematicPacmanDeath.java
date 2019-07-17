package com.kycox.ladybug.constant;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Cin�matique pour afficher / entendre lors de la mort de Pacman
 * 
 */
public class KinematicPacmanDeath {
  // bip de la cin�matique
  private int                bip         = 0;

// Liste des images des images � afficher lors de la cin�matique
  private List<PicturesEnum> lstPictures = new ArrayList<>(
      Arrays.asList(PicturesEnum.PACMAN_DEFAULT, PicturesEnum.PACMAN_UP_2, PicturesEnum.PACMAN_UP_3,
          PicturesEnum.PACMAN_UP_4, PicturesEnum.PACMAN_UP_5, PicturesEnum.PACMAN_UP_6, PicturesEnum.PACMAN_UP_7,
          PicturesEnum.PACMAN_UP_8, PicturesEnum.PACMAN_UP_9, PicturesEnum.PACMAN_UP_10, PicturesEnum.PACMAN_UP_11));

  // private GameSounds gameSounds;
  private long               timeInMMs   = 0;

  /**
   * Contructeur priv�
   */
  public KinematicPacmanDeath(long timeInMMs) {
    this.timeInMMs = timeInMMs;
  }

  /**
   * Retourne le bip de la cin�matique
   */
  public int getBip() {
    return bip;
  }

  /**
   * Retourne l'image � afficher quand Pacman meurt.
   * 
   * @return
   */
  public Image getImage() {
    // �chantillonnage en fonction du timer du mod�le
    long nbrBips = timeInMMs / Constants.PACE;
    // R�cup�ration du nombre d'images
    int nbrImages = lstPictures.size();

    // Calcul du nombre de bip du timer du mod�le par images
    long nbrBitPerImage = nbrBips / nbrImages;
    // Calcule de l'index de l'image � afficher
    int numImage = (int) (bip / nbrBitPerImage);

    // si le calcul est long, il se peut que l'index d�passe... on le r�ajuste
    if (numImage >= nbrImages)
      numImage = nbrImages - 1;

    return lstPictures.get(numImage).getImg();
  }

  /**
   * Incr�mentation du Bip; utilis� par le mod�le
   */
  public void incrementBip() {
    bip++;
  }

  /**
   * Initialise le bip de la cin�matique
   */
  public void initBip() {
    bip = 0;
  }

  /**
   * Test la fin de la musique de la mort de Pacman; utilis� par le mod�le
   * 
   * @return
   */
  public boolean isEnd() {
    return bip * Constants.PACE >= timeInMMs;
  }
}
