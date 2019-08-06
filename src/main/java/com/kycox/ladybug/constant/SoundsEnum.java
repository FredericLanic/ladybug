package com.kycox.ladybug.constant;

import java.net.URL;

/**
 * Enumération des sons à écouter dans le jeu
 *
 */
public enum SoundsEnum {
  GHOST_REGENERATE(256, "ghost_regenerate.wav"),
  GHOST_SURVIVOR(128, "ghost_survivor.wav"),
  PACMAN_BEGINNING(1, "pacman_beginning.wav"),
  PACMAN_CHOMP(2, "pacman_chomp.wav"),
  PACMAN_EAT_FRUIT(8, "pacman_eatfruit.wav"),
  PACMAN_EAT_GHOST(16, "pacman_eatghost.wav"),
  PACMAN_EXTRA_PAC(32, "pacman_extrapac.wav"),
  PACMAN_INTER_MISSION(64, "pacman_intermission.wav"),
  PACMAN_IS_DYING(4, "pacman_death.wav");

  private int index = 0;

  private URL url   = null;

  /**
   * Constructeur
   * 
   * @param fileName
   */
  private SoundsEnum(int index, String fileName) {
    this.index = index;
    this.url = SoundsEnum.class.getClassLoader().getResource("sound/wav/" + fileName);
  }

  /**
   * Retourne l'index du son
   * 
   * @return
   */
  public int getIndex() {
    return index;
  }

  /**
   * Retourne le chemin du fichier son à émettre
   * 
   * @return
   */
  public URL getUrl() {
    return url;
  }

}
