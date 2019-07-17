package com.kycox.ladybug.score;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class GroupIncrementScores {
//  private static GroupIncrementScores groupScoreIncrement = new GroupIncrementScores();
//
//  /**
//   * Retourne le singleton
//   * 
//   * @return
//   */
//  public static GroupIncrementScores getInstance() {
//    return groupScoreIncrement;
//  }

  /**
   * Liste des score incréments
   */
  private List<IncrementScore> lstScoreIncrements;

  /**
   * Constructor privé
   */
  public GroupIncrementScores() {
    lstScoreIncrements = new ArrayList<>();
  }

  /**
   * Ajout d'un score incrément
   * 
   * @param position
   * @param value
   */
  public void add(Point position, String value) {
    lstScoreIncrements.add(new IncrementScore(position, value));
  }

  /**
   * Retourne tous les score incrémentés uniquement pour la View
   * 
   * @return
   */
  public List<IncrementScore> getLstScoresIncrement() {
    return lstScoreIncrements;

  }

  /**
   * Suppression des score incrément qui sont en train de mourir
   */
  public void removeIfDying() {
    lstScoreIncrements.removeIf(IncrementScore::isDying);
  }
}
