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
   * Liste des score incr�ments
   */
  private List<IncrementScore> lstScoreIncrements;

  /**
   * Constructor priv�
   */
  public GroupIncrementScores() {
    lstScoreIncrements = new ArrayList<>();
  }

  /**
   * Ajout d'un score incr�ment
   * 
   * @param position
   * @param value
   */
  public void add(Point position, String value) {
    lstScoreIncrements.add(new IncrementScore(position, value));
  }

  /**
   * Retourne tous les score incr�ment�s uniquement pour la View
   * 
   * @return
   */
  public List<IncrementScore> getLstScoresIncrement() {
    return lstScoreIncrements;

  }

  /**
   * Suppression des score incr�ment qui sont en train de mourir
   */
  public void removeIfDying() {
    lstScoreIncrements.removeIf(IncrementScore::isDying);
  }
}
