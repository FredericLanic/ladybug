package com.kycox.ladybug.score;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.engine.element.ghost.action.AllGhostsActions;
import com.kycox.ladybug.engine.element.ladybug.action.LadybugActions;

/**
 * Score du jeu
 *
 */
public class GameScore {
  private int incrementScore;

  private int score;

  /**
   * Ajout du score et du score incrémental
   * 
   * @param score
   */
  public void addScore(int score) {
    this.score += score;
    this.incrementScore += score;
  }

  /**
   * Retourne le score incrémental
   * 
   * @return
   */
  public int getIncrementScore() {
    return incrementScore;
  }

  /**
   * Retourne le score
   * 
   * @return
   */
  public int getScore() {
    return score;
  }

  /**
   * Initialise le score et le score incrémental
   */
  public void init() {
    incrementScore = 0;
    score = 0;
  }

  /**
   * Initialise le score incrémental
   */
  public void initIncrementScore() {
    incrementScore = 0;
  }

  /**
   * Adjust the score number
   */
  public void setScore(AllGhostsActions allGhostsActions, LadybugActions ladybugActions) {
    if (ladybugActions.hasEatenAMegaPoint())
      addScore(Constants.SCORE_MEGA_POINT);
    if (ladybugActions.hasEatenAPoint())
      addScore(Constants.SCORE_SIMPLE_POINT);

    addScore(allGhostsActions.getNbrEatenGhost() * Constants.SCORE_EATEN_GHOST);
  }
}
