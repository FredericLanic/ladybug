package com.kycox.ladybug.engine.model;

import static com.kycox.ladybug.constant.Constants.PACE;

import java.awt.Point;
import java.awt.event.ActionListener;
import java.util.Observable;

import javax.swing.Timer;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.constant.KinematicPacmanDeath;
import com.kycox.ladybug.constant.SoundsEnum;
import com.kycox.ladybug.constant.SuperPowerTimerEnum;
import com.kycox.ladybug.engine.element.ghost.GhostsGroup;
import com.kycox.ladybug.engine.element.ghost.action.AllGhostsActions;
import com.kycox.ladybug.engine.element.ghost.set.GhostStatusEnum;
import com.kycox.ladybug.engine.element.pacman.Pacman;
import com.kycox.ladybug.engine.element.pacman.action.PacmanActions;
import com.kycox.ladybug.engine.element.pacman.set.PacmanStatusEnum;
import com.kycox.ladybug.engine.timer.SuperPowerTimer;
import com.kycox.ladybug.level.ScreenData;
import com.kycox.ladybug.score.GameScore;
import com.kycox.ladybug.score.GroupIncrementScores;
import com.kycox.ladybug.sound.GameSounds;
import com.kycox.ladybug.tools.map.CheckLevelMap;

/**
 * Mod�le du jeu MVN : c'est le mod�le qui � le timer du jeu (coeur du jeu)
 *
 */

@SuppressWarnings("deprecation")
public class GameModel extends Observable {
  private boolean              beginNewLevel        = false;

  /** Objet Score du jeu */
  private GameScore            gameScore            = new GameScore();

  private GameSounds           gameSounds           = new GameSounds();

  private GameStatus           gameStatus           = new GameStatus();

  /** Coeur du jeu */
  private final Timer          gameTimer            = createTimer();

  private Point                ghostRequest         = Constants.POINT_ZERO;

  private GhostsGroup          groupGhosts          = null;

  private GroupIncrementScores groupIncrementScores = new GroupIncrementScores();

  private KinematicPacmanDeath kinematicPacmanDeath = new KinematicPacmanDeath(
      gameSounds.getMicrosecondLengthPacmanDeath());

  /** Score de l'ancienne partie */
  private int                  oldScore             = -1;

  private Pacman               pacman               = new Pacman();

  private ScreenData           screenData           = new ScreenData();

  /** Timer pour le super power de Pacman */
  private SuperPowerTimer      superPowerTimer      = new SuperPowerTimer(1);

  /**
   * Constructeur
   */
  public GameModel() {
    super();
    initGame();
    startGameTimer();
  }

  /**
   * V�rifier si le labyrinthe est termin�
   */
  private void checkMaze() {
    // Niveau termin�
    if (screenData.getNbrBlocksWithPoint() == 0) {
      gameScore.addScore(Constants.SCORE_END_LEVEL);
      initLevel();
    }
  }

  /**
   * Param�trage des fant�mes dans le niveau actuel
   * 
   * Initialisation des directions et de la vue de pacman
   * 
   * suite de l'initialisation du niveau ou quand pacman meurt
   */
  private void continueLevel() {
    // arret de tous les sons avant de continuer
    gameSounds.stopAllSounds();

    // initialise Pacman pour le niveau
    pacman.setStartLevel(gameStatus.getNumLevel(), screenData);

    // initialise les fant�mes
    groupGhosts.setStartLevel(gameStatus.getNumLevel(), screenData);
  }

  /**
   * Cr�ation du timer du jeu (coeur du jeu)
   * 
   * @return
   */
  private Timer createTimer() {
    ActionListener action = event -> {
      setChanged();
      // initialisation du con
      gameSounds.init();

      if (gameStatus.isInGame() && PacmanStatusEnum.isDead().test(pacman)) {
        pacmanIsDead();
      } else if (gameStatus.isInGame() && PacmanStatusEnum.isDying().test(pacman)) {
        pacmanIsDying();
      } else if (gameStatus.isInGame() && groupGhosts.isDeadKeyGhost()) {
        gameStatus.setStopGame();
        System.out.println("Le fant�me a perdu");
      } else {
// D�placement de Pacman et r�cup�ration de ses actions
        PacmanActions pacmanActions = pacman.movePacman(screenData);

// D�placement des fant�mes
        groupGhosts.moveGhosts(screenData, pacman, ghostRequest);

// D�tections des actions des fant�mes
        AllGhostsActions allGhostsActions = groupGhosts.setAllGhostsActions(pacman);

// GESTION DES INCREMENTS SCORES
        pacmanActions.addIncrementScores(groupIncrementScores);
        allGhostsActions.addIncrementScores(groupIncrementScores);
        groupIncrementScores.removeIfDying();

// GESTION DE L'ETAT DES FANT�MES
        // Etats des fant�mes
        allGhostsActions.setGhostSettingAfterPacmanContact(gameStatus.getNumLevel());
        // Etat des fant�mes de REGENERATING � NORMAL
        groupGhosts.setGhostStatusAfterRegeneration();
        // modifier la vitesse des fant�me en cours de partie
        groupGhosts.setSpeedDuringGame();

// GESTION DE LA MORT DE PACMAN 
        // modification de l'�tat de Pacman en fonction des actions d�tect�es des
        // fant�mes
        if (allGhostsActions.eatPacman()) {
          pacman.setStatus(PacmanStatusEnum.DYING);
          allGhostsActions.addNewLifeToKeyGhost();
        }

// GESTION DU SUPER POWER
        // mise � jour des status des fant�mes en fonction du timer
        // Peut �tre le d�placer de l� ??
        if (superPowerTimer.getStatus() == SuperPowerTimerEnum.STOPPING) {
          groupGhosts.setGhostFlashActive();
        } else if ((superPowerTimer.getStatus() == SuperPowerTimerEnum.STOP)) {
          groupGhosts.setFear(false);
        }
        // Active le timer du super power si Pacman a mang� un m�ga point
        // Peut �tre le d�placer de l� ??
        if (pacmanActions.hasEatenAMegaPoint()) {
          runSuperPowerTimer();
          groupGhosts.setFear(true);
        }

// SCORE
        // Ajoute du score
        gameScore.setScore(allGhostsActions, pacmanActions);
        // ajout d'une vie suppl�mentaire si besoin
        if (gameScore.getIncrementScore() >= Constants.NEW_LIFE_BY_SCORE) {
          gameScore.initIncrementScore();
          pacman.addNewLife();
          // Ajout du son Extra pac
          gameSounds.addSounds(SoundsEnum.PACMAN_EXTRA_PAC.getIndex());
        }

// SCREENDATA 
        // Mise � jour du ScreenData
        screenData.updateScreenBlock(pacmanActions);
        // v�rification de la fin du tableau
        checkMaze();

// SONDS
        // affecte les sons
        gameSounds.setSound(groupGhosts, allGhostsActions, pacman, pacmanActions, kinematicPacmanDeath);
      }
      // notifie la view qu'il y a eu du changement
      notifyObservers();
    };

    // Cr�ation d'un timer qui g�n�re un tic
    return new Timer(PACE, action);
  }

  /**
   * V�rifie si le timer qui rythme le jeu est en cours
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

  public KinematicPacmanDeath getKinematicPacmanDeath() {
    return kinematicPacmanDeath;
  }

  /**
   * Retourne le score de la partie pr�c�dente
   */
  public int getOldScore() {
    return oldScore;
  }

  public Pacman getPacman() {
    return pacman;
  }

  public ScreenData getScreenData() {
    return screenData;
  }

  /**
   * Initialise les variables au lancement du programme
   */
  public void initGame() {

    int levelPresentation = 3;

    // initialisation du num�ro du niveau; incr�ment� dans initLevel
    gameStatus.setNumLevel(0);
    // utilis� juste pour l'affichage de la fen�tre d'initialisation
    screenData.setLevelMap(1, gameStatus.isInGame());
    // V�rifie le niveau et rajoute des bordures quand il le faut
    CheckLevelMap checkData = new CheckLevelMap();
    checkData.check(screenData);
    // 3 vies par d�faut
    pacman.setLifesLeft(3);
    // initialise les score
    gameScore.init();
    // Initialise le groupe de fant�me
    groupGhosts = new GhostsGroup(levelPresentation, screenData);
    // mise � la vitesse du niveau 3 pour la pr�sentation
    groupGhosts.initSpeeds(levelPresentation);
    // initialise les positions des fant�mes
    groupGhosts.initPositions(screenData);
    // initialise les fant�mes pour la pr�sentation
    groupGhosts.setStatus(GhostStatusEnum.NORMAL);
    // initialise les vies de fant�mes
    groupGhosts.initLifeLeft(3);

    // Pacman n'est pas en vie lors de l'initialisation du jeu
    pacman.setStatus(PacmanStatusEnum.DEAD);
  }

  /**
   * Initialise le niveau en fonction du niveau pr�c�dent
   */
  public void initLevel() {
    // suppression des composants techniques du niveau pr�c�dent
    removePreviousTasksLevel();
    // incr�mente le num�ro du niveau
    gameStatus.addNumLevel();
    // recopie les param�tres du niveau dans les donn�es flottantes du niveau
    screenData.setLevelMap(gameStatus.getNumLevel(), gameStatus.isInGame());
    // initialisation du super power
    groupGhosts.setFear(false);
    // d�but du level : utile pour le son du jingle
    setBeginNewLevel(true);
    // initialisation de la cin�matique de la mort de pacman
    kinematicPacmanDeath.initBip();

//    // on met un fant�me jouable par les touches
//    GhostsSettingsEnum.CLYDE.setComputed(false);
//    ghostRequest = Constants.POINT_ZERO;

    // on continue le level
    continueLevel();
  }

  /**
   * V�rifie c'est le jeu est au d�but du niveau Utilis� pour palier au soucis du
   * jingle du d�but
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
   * Pacman a rencontr� un fant�me !! Le jeu est termin� si toutes les vies de
   * pacman ont �t� utilis�es.
   */
  private void pacmanIsDead() {
    pacman.minusLifesLeft();
    // test fin du jeu
    if (pacman.getLifesLeft() == 0) {
      oldScore = gameScore.getScore();
      gameStatus.setStopGame();
      // a mettre ailleurs :
      initGame();
      System.out.println("Pacman a perdu");
    }
    continueLevel();
  }

  /**
   * Cin�matique de la mort de Pacman
   */
  private void pacmanIsDying() {
    // d�placement des fant�mes
    groupGhosts.moveGhosts(screenData, pacman, ghostRequest);

    // Incr�mentation du bip pour la cin�matique
    kinematicPacmanDeath.incrementBip();

    // Fin de la cin�matique de la mort de Pacman
    if (kinematicPacmanDeath.isEnd()) {
      pacman.setStatus(PacmanStatusEnum.DEAD);
      kinematicPacmanDeath.initBip();
    }
  }

  /**
   * Suppression des t�ches du niveau qui est termin� (succ�s, �chec ou pas de
   * niveau).
   */
  private void removePreviousTasksLevel() {
    // arr�t des timers super power
    superPowerTimer.forcedStop();
  }

  /**
   * Lancement du timer pour le super power de Pacman
   */
  private void runSuperPowerTimer() {
    // on force l'arr�t des timers qui ont pu �tre d�marr�s dans la partie
    // pr�c�dente
    superPowerTimer.forcedStop();
    // on lance le timer
    // FIXME MAGIC NUMBER !! : 15 seconds
    // Il serait bien de d'apporter une notion du temps en fonction du niveau
    superPowerTimer.launch(15);
  }

  /**
   * Affecte le fait que c'est le d�but du niveau. C'est un contournement pour
   * �couter le Jingle de d�but
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
   * Arr�t du timer qui rythme le jeu
   */
  public void stopGameTimer() {
    gameTimer.stop();
  }
}
