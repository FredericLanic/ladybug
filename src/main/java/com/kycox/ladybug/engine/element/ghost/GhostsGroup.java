package com.kycox.ladybug.engine.element.ghost;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.kycox.ladybug.engine.element.ghost.action.AllGhostsActions;
import com.kycox.ladybug.engine.element.ghost.set.GhostStatusEnum;
import com.kycox.ladybug.engine.element.pacman.Pacman;
import com.kycox.ladybug.formules.BlinkyIncrementSpeed;
import com.kycox.ladybug.level.ScreenData;
import com.kycox.ladybug.tools.Utils;

/**
 * Gestion des actions sur l'ensemble des fant�mes
 * 
 * Singleton
 *
 */
public class GhostsGroup {

  private List<Ghost> lstGhosts = null;

  /**
   * Constructeur : les 4 fant�mes
   */
  public GhostsGroup(int numLevel, ScreenData screenData) {
    BlinkyIncrementSpeed blinkyIncrementSpeed = new BlinkyIncrementSpeed(numLevel, screenData);

    // Cr�ation de Blink
    Blinky blinky = new Blinky(numLevel);
    // Affectation de l'objet de gestion de sa vitesse incr�mentale
    blinky.setBlinkyIncrementSpeed(blinkyIncrementSpeed);

    // Affectation des fant�mes dans la liste
    lstGhosts = new ArrayList<>();
    lstGhosts.add(blinky);
    lstGhosts.add(new Clyde(numLevel));
    lstGhosts.add(new Inky(numLevel));
    lstGhosts.add(new Pinky(numLevel));
  }

  /**
   * Retourne le fant�me qui est g�r� par un utilisateur
   * 
   * ou null si aucun fant�me n'est g�r� par un utilisateur
   */
  public Ghost getGhostNotComputed() {
    Optional<Ghost> ghost = lstGhosts.stream().filter(g -> !g.isComputed()).findFirst();
    if (ghost.isPresent())
      return ghost.get();
    return null;
  }

  /**
   * Retourne la liste des fant�mes; uniquement pour la View (en th�orie)
   * 
   * @return
   */
  public List<Ghost> getLstGhosts() {
    return lstGhosts;
  }

  /**
   * Initialise le nombre de vie des fant�mes
   * 
   * @param numLeftLives
   */
  public void initLifeLeft(int numLeftLives) {
    lstGhosts.stream().forEach(g -> g.setLifesLeft(numLeftLives));
  }

  /**
   * Initialise les positions des fant�mes
   */
  public void initPositions(ScreenData screenData) {
    lstGhosts.stream().forEach(g -> g.setPosition(Utils.convertPointToGraphicUnit(screenData.getRandomPosOnAPoint())));
  }

  /**
   * Initialise les vitesses des fant�mes
   * 
   * @param numLevel
   */
  public void initSpeeds(int numLevel) {
    lstGhosts.stream().forEach(g -> g.getInitSpeed(numLevel));
  }

  /**
   * Retourne vrai si le fant�me n'a plus de vie
   */
  public boolean isDeadKeyGhost() {
    long nbrDeadKeyGhosts = lstGhosts.stream().filter(g -> !g.isComputed()).filter(g -> (g.getLifesLeft() <= 0))
        .count();
    return (nbrDeadKeyGhosts >= 1);
  }

  /**
   * D�placement des fant�mes
   * 
   * @param numLevel
   */
  public void moveGhosts(ScreenData screenData, Pacman pacman, Point ghostRequest) {
    // D�placement des fant�mes g�r�s par l'ordinateur
    lstGhosts.stream().filter(Ghost::isComputed)
        .forEach(g -> g.moveGhostByComputer(Utils.convertPointToBlockUnit(pacman.getPosition()), screenData));

    // D�placement des fant�mes g�r�s par l'humain
    lstGhosts.stream().filter(g -> !g.isComputed())
        .forEach(g -> g.moveGhostByUser(Utils.convertPointToBlockUnit(pacman.getPosition()), screenData, ghostRequest));
  }

  /**
   * V�rication des fant�mes
   * 
   * @param inGame
   * @param numLevel
   */
  public AllGhostsActions setAllGhostsActions(Pacman pacman) {
    AllGhostsActions ghostsActions = new AllGhostsActions();
    lstGhosts.stream().forEach(g -> ghostsActions.addGhostAction(g.setGhostActions(pacman)));
    return ghostsActions;
  }

  /**
   * Informe tous les fant�mes (non mang�s) qu'ils ont peur ou pas
   * 
   * @param active
   */
  public void setFear(boolean fear) {
    if (fear) {
      lstGhosts.stream().filter(g -> !g.getStatus().equals(GhostStatusEnum.DYING))
          .forEach(g -> g.setStatus(GhostStatusEnum.SCARED));
    } else {
      lstGhosts.stream().filter(g -> !g.getStatus().equals(GhostStatusEnum.DYING))
          .forEach(g -> g.setStatus(GhostStatusEnum.NORMAL));
    }
  }

  /**
   * Tous les fant�mes doivent clignotter
   */
  public void setGhostFlashActive() {
    lstGhosts.stream().filter(GhostStatusEnum.isScared()).forEach(g -> g.setStatus(GhostStatusEnum.FLASH));
  }

  /**
   * Status des fant�mes de REGENERATING � NORMAL
   */
  public void setGhostStatusAfterRegeneration() {
    lstGhosts.stream().filter(g -> GhostStatusEnum.isRegenerating().test(g))
        .forEach(g -> g.setStatus(GhostStatusEnum.NORMAL));
  }

  /**
   * Affecte la vitesse de chaque fant�me au cours du niveau en cours
   */
  public void setSpeedDuringGame() {
    lstGhosts.stream().forEach(Ghost::setSpeedDuringGame);
  }

  /**
   * Initialise les fant�mes lors du d�but de niveau
   * 
   * @param numLevel
   */
  public void setStartLevel(int numLevel, ScreenData screenData) {
    setStatus(GhostStatusEnum.NORMAL);
    initSpeeds(numLevel);
    initPositions(screenData);
  }

  /**
   * Affecte le m�me status � tous les fan�mes
   * 
   * @param status
   */
  public void setStatus(GhostStatusEnum status) {
    lstGhosts.stream().forEach(g -> g.setStatus(status));
  }
}
