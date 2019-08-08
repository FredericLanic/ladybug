package com.kycox.ladybug.level;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Levels {
  private List<ILevel> lstLevel = new ArrayList<>();

  private Random       random   = new Random();

  /**
   * Constructeur : ajoute les différents niveaux du jeu
   */
  public Levels() {
    /*
     * Ici on peut ajouter d'autres niveaux
     */
//    lstLevel.add(new Level1());
    lstLevel.add(new Level2());
  }

  /**
   * Retourne le niveau demand�
   * 
   * @param i : le numéro du niveau demandé
   */
  public ILevel getLevel(int i) {
    int num = i;
    if (num > lstLevel.size())
      num = 1 + random.nextInt(lstLevel.size());

    return lstLevel.get(num - 1);
  }
}
