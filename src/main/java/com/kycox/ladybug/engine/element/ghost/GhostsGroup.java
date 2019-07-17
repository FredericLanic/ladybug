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
 * Gestion des actions sur l'ensemble des fantômes
 * 
 * Singleton
 *
 */
public class GhostsGroup {

  private List<Ghost> lstGhosts = null;

  /**
   * Constructeur : les 4 fantômes
   */
  public GhostsGroup(int numLevel, ScreenData screenData) {
    BlinkyIncrementSpeed blinkyIncrementSpeed = new BlinkyIncrementSpeed(numLevel, screenData);

    // Création de Blink
    Blinky blinky = new Blinky(numLevel);
    // Affectation de l'objet de gestion de sa vitesse incrémentale
    blinky.setBlinkyIncrementSpeed(blinkyIncrementSpeed);

    // Affectation des fantômes dans la liste
    lstGhosts = new ArrayList<>();
    lstGhosts.add(blinky);
    lstGhosts.add(new Clyde(numLevel));
    lstGhosts.add(new Inky(numLevel));
    lstGhosts.add(new Pinky(numLevel));
  }

  /**
   * Retourne le fantôme qui est géré par un utilisateur
   * 
   * ou null si aucun fantôme n'est géré par un utilisateur
   */
  public Ghost getGhostNotComputed() {
    Optional<Ghost> ghost = lstGhosts.stream().filter(g -> !g.isComputed()).findFirst();
    if (ghost.isPresent())
      return ghost.get();
    return null;
  }

  /**
   * Retourne la liste des fantômes; uniquement pour la View (en théorie)
   * 
   * @return
   */
  public List<Ghost> getLstGhosts() {
    return lstGhosts;
  }

  /**
   * Initialise le nombre de vie des fantômes
   * 
   * @param numLeftLives
   */
  public void initLifeLeft(int numLeftLives) {
    lstGhosts.stream().forEach(g -> g.setLifesLeft(numLeftLives));
  }

  /**
   * Initialise les positions des fantômes
   */
  public void initPositions(ScreenData screenData) {
    lstGhosts.stream().forEach(g -> g.setPosition(Utils.convertPointToGraphicUnit(screenData.getRandomPosOnAPoint())));
  }

  /**
   * Initialise les vitesses des fantômes
   * 
   * @param numLevel
   */
  public void initSpeeds(int numLevel) {
    lstGhosts.stream().forEach(g -> g.getInitSpeed(numLevel));
  }

  /**
   * Retourne vrai si le fantôme n'a plus de vie
   */
  public boolean isDeadKeyGhost() {
    long nbrDeadKeyGhosts = lstGhosts.stream().filter(g -> !g.isComputed()).filter(g -> (g.getLifesLeft() <= 0))
        .count();
    return (nbrDeadKeyGhosts >= 1);
  }

  /**
   * Déplacement des fantômes
   * 
   * @param numLevel
   */
  public void moveGhosts(ScreenData screenData, Pacman pacman, Point ghostRequest) {
    // Déplacement des fantômes gérés par l'ordinateur
    lstGhosts.stream().filter(Ghost::isComputed)
        .forEach(g -> g.moveGhostByComputer(Utils.convertPointToBlockUnit(pacman.getPosition()), screenData));

    // Déplacement des fantômes gérés par l'humain
    lstGhosts.stream().filter(g -> !g.isComputed())
        .forEach(g -> g.moveGhostByUser(Utils.convertPointToBlockUnit(pacman.getPosition()), screenData, ghostRequest));
  }

  /**
   * Vérication des fantômes
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
   * Informe tous les fantômes (non mangés) qu'ils ont peur ou pas
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
   * Tous les fantômes doivent clignotter
   */
  public void setGhostFlashActive() {
    lstGhosts.stream().filter(GhostStatusEnum.isScared()).forEach(g -> g.setStatus(GhostStatusEnum.FLASH));
  }

  /**
   * Status des fantômes de REGENERATING à NORMAL
   */
  public void setGhostStatusAfterRegeneration() {
    lstGhosts.stream().filter(g -> GhostStatusEnum.isRegenerating().test(g))
        .forEach(g -> g.setStatus(GhostStatusEnum.NORMAL));
  }

  /**
   * Affecte la vitesse de chaque fantôme au cours du niveau en cours
   */
  public void setSpeedDuringGame() {
    lstGhosts.stream().forEach(Ghost::setSpeedDuringGame);
  }

  /**
   * Initialise les fantômes lors du début de niveau
   * 
   * @param numLevel
   */
  public void setStartLevel(int numLevel, ScreenData screenData) {
    setStatus(GhostStatusEnum.NORMAL);
    initSpeeds(numLevel);
    initPositions(screenData);
  }

  /**
   * Affecte le même status à tous les fanômes
   * 
   * @param status
   */
  public void setStatus(GhostStatusEnum status) {
    lstGhosts.stream().forEach(g -> g.setStatus(status));
  }
}
