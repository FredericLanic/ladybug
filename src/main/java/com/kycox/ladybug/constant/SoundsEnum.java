package com.kycox.ladybug.constant;

import java.net.URL;

/**
 * Enumération des sons à écouter dans le jeu
 *
 */
public enum SoundsEnum {
  GHOST_EATEN(512, "ghost_eaten.wav"), GHOST_REGENERATE(256, "ghost_regenerate.wav"),
  GHOST_SURVIVOR(128, "ghost_survivor.wav"), LADYBUG_BEGINNING(1, "ladybug_beginning.wav"),
  LADYBUG_CHOMP(2, "ladybug_chomp.wav"), LADYBUG_EAT_FRUIT(8, "ladybug_eatfruit.wav"),
  LADYBUG_EAT_GHOST(16, "ladybug_eatghost.wav"), LADYBUG_EXTRA_PAC(32, "ladybug_extralife.wav"),
  LADYBUG_INTER_MISSION(64, "ladybug_intermission.wav"), LADYBUG_IS_DYING(4, "ladybug_death.wav"),
  LADYBUG_SIREN(1024, "ladybug_siren.wav");

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
