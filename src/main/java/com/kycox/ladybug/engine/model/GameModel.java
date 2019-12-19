/**
  Copyright (C) 2019  Frédéric Lanic frederic.lanic@outlook.fr

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.kycox.ladybug.engine.model;

import static com.kycox.ladybug.constant.Constants.PACE;

import java.awt.Point;
import java.awt.event.ActionListener;
import java.util.Observable;

import javax.swing.Timer;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.constant.KinematicLadybugDeath;
import com.kycox.ladybug.constant.SoundsEnum;
import com.kycox.ladybug.constant.SuperPowerTimerEnum;
import com.kycox.ladybug.engine.element.ghost.GhostsGroup;
import com.kycox.ladybug.engine.element.ghost.action.AllGhostsActions;
import com.kycox.ladybug.engine.element.ghost.set.GhostStatusEnum;
import com.kycox.ladybug.engine.element.ladybug.Ladybug;
import com.kycox.ladybug.engine.element.ladybug.action.LadybugActions;
import com.kycox.ladybug.engine.element.ladybug.set.LadybugStatusEnum;
import com.kycox.ladybug.engine.timer.SuperPowerTimer;
import com.kycox.ladybug.level.ScreenData;
import com.kycox.ladybug.score.GameScore;
import com.kycox.ladybug.score.GroupIncrementScores;
import com.kycox.ladybug.sound.GameSounds;
import com.kycox.ladybug.tools.map.CheckLevelMap;

/**
 * Modèle du jeu MVC : c'est le modèle qui contient le timer du jeu (coeur du jeu)
 *
 */

@SuppressWarnings("deprecation")
public class GameModel extends Observable {
  private boolean               beginNewLevel         = false;

  /** Objet Score du jeu */
  private GameScore             gameScore             = new GameScore();

  private GameSounds            gameSounds            = new GameSounds();

  private GameStatus            gameStatus            = new GameStatus();

  /** Coeur du jeu */
  private final Timer           gameTimer             = createTimer();

  private Point                 ghostRequest          = Constants.POINT_ZERO;

  private GhostsGroup           groupGhosts           = null;

  private GroupIncrementScores  groupIncrementScores  = new GroupIncrementScores();

  private KinematicLadybugDeath kinematicLadybugDeath = new KinematicLadybugDeath(
      gameSounds.getMicrosecondLengthLadybugDeath());

  private Ladybug               ladybug               = new Ladybug();

  /** Score de l'ancienne partie */
  private int                   oldScore              = -1;

  private ScreenData            screenData            = new ScreenData();

  /** Timer pour le super power de ladybug */
  private SuperPowerTimer       superPowerTimer       = new SuperPowerTimer(1);

  /**
   * Constructeur
   */
  public GameModel() {
    super();
    initGame();
    startGameTimer();
  }

  /**
   * Vérifie si le timer qui rythme le jeu est en cours
   *
   * @return
   */
  public boolean gameTimerIsRunning() {
    return gameTimer.isRunning();
  }

  public GameScore getGameScore() {
    return gameScore;
  }

  public GameSounds getGameSounds() {
    return gameSounds;
  }

  public GameStatus getGameStatus() {
    return gameStatus;
  }

  public GhostsGroup getGroupGhosts() {
    return groupGhosts;
  }

  public GroupIncrementScores getGroupIncrementScores() {
    return groupIncrementScores;
  }

  public KinematicLadybugDeath getKinematicLadybugDeath() {
    return kinematicLadybugDeath;
  }

  public Ladybug getLadybug() {
    return ladybug;
  }

  /**
   * Retourne le score de la partie précédente
   */
  public int getOldScore() {
    return oldScore;
  }

  public ScreenData getScreenData() {
    return screenData;
  }

  /**
   * Initialise les variables au lancement du programme
   */
  public void initGame() {

    // initialisation du numéro du niveau; incrémenté dans initLevel
    gameStatus.setNumLevel(0);
    // utilisé juste pour l'affichage de la fenêtre d'initialisation
    screenData.setLevelMap(1, gameStatus.isInGame());
    // Vérifie le niveau et rajoute des bordures quand il le faut
    CheckLevelMap checkData = new CheckLevelMap();
    checkData.check(screenData);
    // 3 vies par défaut
    ladybug.setLifesLeft(3);
    // ladybug n'est pas en vie lors de l'initialisation du jeu
    ladybug.setStatus(LadybugStatusEnum.DEAD);
    // initialise les score
    gameScore.init();
    // Initialise le groupe de fantôme
    groupGhosts = new GhostsGroup(Constants.PRESENTATION_LEVEL, screenData);
    // mise de la vitesse du niveau 3 pour la présentation
    groupGhosts.initSpeeds(Constants.PRESENTATION_LEVEL);
    // initialise les positions des fantômes
    groupGhosts.initPositions(screenData);
    // initialise les fantômes pour la présentation
    groupGhosts.setStatus(GhostStatusEnum.NORMAL);
    // initialise les vies de fantômes
    groupGhosts.initLifeLeft(3);
  }

  /**
   * Initialise le niveau en fonction du niveau précédent
   */
  public void initLevel() {
    // suppression des composants techniques du niveau précédent
    removePreviousTasksLevel();
    // incrémente le numéro du niveau
    gameStatus.addNumLevel();
    // recopie les paramètres du niveau dans les données flottantes du niveau
    screenData.setLevelMap(gameStatus.getNumLevel(), gameStatus.isInGame());
    // initialisation du super power
    groupGhosts.setFear(false);
    // début du level : utile pour le son du jingle
    setBeginNewLevel(true);
    // initialisation de la cinématique de la mort de ladybug
    kinematicLadybugDeath.initBip();
    // on continue le level
    continueLevel();
  }

  /**
   * Vérifie c'est le jeu est au début du niveau Utilisé pour palier au soucis du jingle du début
   *
   * @return
   */
  public boolean isBeginNewLevel() {
    return beginNewLevel;
  }

  /**
   * Retourne le nombre de joueurs : 1 ou 2
   *
   * @return
   */
  public int nbrNbrPlayers() {
    if (groupGhosts.getGhostNotComputed() == null) {
      return 1;
    }
    return 2;
  }

  /**
   * Affecte le fait que c'est le début du niveau. C'est un contournement pour écouter le Jingle de
   * début
   *
   * @param beginNewLevel
   */
  public void setBeginNewLevel(boolean beginNewLevel) {
    this.beginNewLevel = beginNewLevel;
  }

  public void setGhostRequest(Point ghostRequest) {
    this.ghostRequest = ghostRequest;
  }

  /**
   * Sauvegarde de l'ancien score
   *
   * @param oldScore
   */
  public void setOldScore(int oldScore) {
    this.oldScore = oldScore;
  }

  /**
   * Lancement du timer qui rythme le jeu
   */
  public void startGameTimer() {
    gameTimer.start();
  }

  /**
   * Arrêt du timer qui rythme le jeu
   */
  public void stopGameTimer() {
    gameTimer.stop();
  }

  /**
   * Vérifier si le labyrinthe est terminé
   */
  private void checkMaze() {
    // Niveau terminé
    if (screenData.getNbrBlocksWithPoint() == 0) {
      gameScore.addScore(Constants.SCORE_END_LEVEL);
      initLevel();
    }
  }

  /**
   * Paramêtrage des fantômes dans le niveau actuel
   *
   * Initialisation des directions et de la vue de ladybug
   *
   * suite de l'initialisation du niveau ou quand ladybug meurt
   */
  private void continueLevel() {
    // arrêt de tous les sons avant de continuer
    gameSounds.stopAllSounds();

    // initialise ladybug pour le niveau
    ladybug.setStartLevel(gameStatus.getNumLevel(), screenData);

    // initialise les fantômes
    groupGhosts.setStartLevel(gameStatus.getNumLevel(), screenData);
  }

  /**
   * Création du timer du jeu (coeur du jeu)
   *
   * @return
   */
  private Timer createTimer() {
    ActionListener action = event -> {

      // initialisation du son
      gameSounds.init();

      if (gameStatus.isInGame() && LadybugStatusEnum.isDead().test(ladybug)) {
        ladybugIsDead();
      } else if (gameStatus.isInGame() && LadybugStatusEnum.isDying().test(ladybug)) {
        ladybugIsDying();
      } else if (gameStatus.isInGame() && groupGhosts.isDeadKeyGhost()) {
        gameStatus.setStopGame();
        System.out.println("Le fantôme a perdu");
      } else {

        // Etat des fantômes de REGENERATING à NORMAL
        groupGhosts.setGhostStatusAfterRegeneration();

// Déplacement de ladybug et récupération de ses actions
        LadybugActions ladybugActions = ladybug.moveLadybug(screenData);

// Déplacement des fantômes
        groupGhosts.moveGhosts(screenData, ladybug, ghostRequest);

// Détections des actions des fantômes
        AllGhostsActions allGhostsActions = groupGhosts.setAllGhostsActions(ladybug);

// GESTION DES INCREMENTS SCORES
        ladybugActions.addIncrementScores(groupIncrementScores);
        allGhostsActions.addIncrementScores(groupIncrementScores);
        groupIncrementScores.removeIfDying();

// GESTION DE L'ETAT DES FANTOMES
        // Etats des fantômes
        allGhostsActions.setGhostSettingAfterLadybugContact(gameStatus.getNumLevel());

        // modifier la vitesse des fantôme en cours de partie
        groupGhosts.setSpeedDuringGame(gameStatus.getNumLevel());

// GESTION DE LA MORT DE LADYBUG
        // modification de l'état de ladybug en fonction des actions détectées des
        // fantômes
        if (allGhostsActions.eatLadybug()) {
          ladybug.setStatus(LadybugStatusEnum.DYING);
          allGhostsActions.addNewLifeToKeyGhost();
        }

// GESTION DU SUPER POWER
        // mise à jour des status des fantômes en fonction du timer
        // Peut être le déplacer de là ??
        if (superPowerTimer.getStatus() == SuperPowerTimerEnum.STOPPING) {
          groupGhosts.setGhostFlashActive();
        } else if ((superPowerTimer.getStatus() == SuperPowerTimerEnum.STOP)) {
          groupGhosts.setFear(false);
        }
        // Active le timer du super power si ladybug a mangé un méga point
        // Peut être le déplacer de là ??
        if (ladybugActions.hasEatenAMegaPoint()) {
          runSuperPowerTimer();
          groupGhosts.setFear(true);
        }

// SCORE
        // Ajoute du score
        gameScore.setScore(allGhostsActions, ladybugActions);
        // ajout d'une vie supplémentaire si besoin
        if (gameScore.getIncrementScore() >= Constants.NEW_LIFE_BY_SCORE) {
          gameScore.initIncrementScore();
          ladybug.addNewLife();
          // Ajout du son Extra pac
          gameSounds.addSounds(SoundsEnum.LADYBUG_EXTRA_PAC.getIndex());
        }

// SCREENDATA
        // Mise à jour du ScreenData
        screenData.updateScreenBlock(ladybugActions);
        // vérification de la fin du tableau
        checkMaze();

// SONDS
        // affecte les sons
        gameSounds.setSound(groupGhosts, allGhostsActions, ladybug, ladybugActions,
            kinematicLadybugDeath);
      }
      // notifie la view qu'il y a eu du changement
      setChanged();
      notifyObservers();
    };

    // Création d'un timer qui génère un tic
    return new Timer(PACE, action);
  }

  /**
   * Ladybug a rencontré un fantôme !! Le jeu est terminé si toutes les vies de ladybug ont été
   * utilisées.
   */
  private void ladybugIsDead() {
    ladybug.minusLifesLeft();
    // test fin du jeu
    if (ladybug.getLifesLeft() == 0) {
      oldScore = gameScore.getScore();
      gameStatus.setStopGame();
      // à mettre ailleurs :
      initGame();
      System.out.println("Ladybug a perdu");
    } else {
      continueLevel();
    }
  }

  /**
   * Cinématique de la mort de ladybug
   */
  private void ladybugIsDying() {
    // déplacement des fantômes
    groupGhosts.moveGhosts(screenData, ladybug, ghostRequest);

    // Incrémentation du bip pour la cinématique
    kinematicLadybugDeath.incrementBip();

    // Fin de la cinématique de la mort de ladybug
    if (kinematicLadybugDeath.isEnd()) {
      ladybug.setStatus(LadybugStatusEnum.DEAD);
      kinematicLadybugDeath.initBip();
    }
  }

  /**
   * Suppression des tâches du niveau qui est terminé (succès, échec ou pas de niveau).
   */
  private void removePreviousTasksLevel() {
    // arrêt des timers super power
    superPowerTimer.forcedStop();
  }

  /**
   * Lancement du timer pour le super power de ladybug
   */
  private void runSuperPowerTimer() {
    // on force l'arrêt des timers qui ont pu être démarrés dans la partie
    // précédente
    superPowerTimer.forcedStop();
    // on lance le timer
    // FIXME MAGIC NUMBER !! : 15 seconds
    // Il serait bien de d'apporter une notion du temps en fonction du niveau
    superPowerTimer.launch(15);
  }
}
