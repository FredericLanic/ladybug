package com.kycox.ladybug.engine.element.ghost.action;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.score.GroupIncrementScores;

public class AllGhostsActions {
  private List<GhostActions> lstGhostActions = new ArrayList<>();

  public void addGhostAction(GhostActions ghostActions) {
    lstGhostActions.add(ghostActions);
  }

  /**
   * Cr�ation des Increments Score en fonction des fant�mes mang�s
   */
  public void addIncrementScores(GroupIncrementScores groupIncrementScores) {
    lstGhostActions.stream().filter(GhostActions::isEaten).forEach(ga -> groupIncrementScores
        .add((Point) ga.getGhost().getPosition().clone(), Integer.toString(Constants.SCORE_EATEN_GHOST)));
  }

  public void addNewLifeToKeyGhost() {
    lstGhostActions.stream().filter(GhostActions::isEatPacman).filter(ga -> !ga.getGhost().isComputed())
        .forEach(g -> g.getGhost().addNewLife());
  }

  /**
   * a mang� Pacman ?
   * 
   * @return
   */
  public boolean eatPacman() {
    return lstGhostActions.stream().filter(GhostActions::isEatPacman).count() > 0;
  }

  /**
   * Retourne le nombres de fant�mes mang�s
   * 
   * @return
   */
  public int getNbrEatenGhost() {
    return (int) lstGhostActions.stream().filter(GhostActions::isEaten).count();
  }

  // � mettre ailleurs ?
  /**
   * Si le fant�me a �t� mang�, ses param�tres changent
   * 
   * (voir Ghost.setSettingJustAfterBeEaten())
   */
  public void setGhostSettingAfterPacmanContact(int numLevel) {
    // Mis � jour du statut
    lstGhostActions.stream().filter(GhostActions::isEaten)
        .forEach(ga -> ga.getGhost().setSettingJustAfterBeEaten(numLevel));

    lstGhostActions.stream().filter(GhostActions::isEaten).forEach(ga -> ga.getGhost().minusLifesLeft());
  }
}
