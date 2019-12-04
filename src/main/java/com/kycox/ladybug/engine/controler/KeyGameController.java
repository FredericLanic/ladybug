package com.kycox.ladybug.engine.controler;

import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.engine.model.GameModel;
import com.kycox.ladybug.engine.view.GameView;

/**
 * Contr�leur du jeu : MVC
 *
 */
public class KeyGameController extends KeyAdapter {

  // voir https://github.com/marcelschoen/gamepad4j pour brancher une
  // manette usb pour le jeu
  // Utiliser plutôt Jamepad qui me semble facilement utilisable;

  /**
   * La vue
   */
  private GameView    gameView;

  /**
   * Le mod�le
   */
  protected GameModel gameModel;

  /**
   * Constructeur
   *
   * @param gameModel
   */
  public KeyGameController(GameModel gameModel) {
    this.gameModel = gameModel;
  }

  /**
   * Retourne la vue
   *
   * @return
   */
  public GameView getGameView() {
    return gameView;
  }

  /**
   * Action sur les touches Gestion des touches pressées
   */
  @Override
  public void keyPressed(KeyEvent e) {
    int keyCode = e.getKeyCode();

    if (keyCode == KeyEvent.VK_ESCAPE) {
      System.exit(0);
    }

    if (gameModel.getGameStatus().isInGame()) {
      // Gestion des touches durant une partie
      manageKeysInGame(keyCode);
    } else {
      manageKeysPresentation(keyCode);
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
  }

  /**
   * Gestion des touches in game
   *
   * @param keyCode
   */
  private void manageKeysInGame(int keyCode) {
    switch (keyCode) {
    // Mouvement L
    case KeyEvent.VK_LEFT:
      ladybugMove(Constants.POINT_LEFT);
      break;
    case KeyEvent.VK_RIGHT:
      ladybugMove(Constants.POINT_RIGHT);
      break;
    case KeyEvent.VK_UP:
      ladybugMove(Constants.POINT_UP);
      break;
    case KeyEvent.VK_DOWN:
      ladybugMove(Constants.POINT_DOWN);
      break;
    // Mouvement du fant�me (joueur 2)
    case KeyEvent.VK_Z:
      ghostMove(Constants.POINT_UP);
      break;
    case KeyEvent.VK_S:
      ghostMove(Constants.POINT_DOWN);
      break;
    case KeyEvent.VK_Q:
      ghostMove(Constants.POINT_LEFT);
      break;
    case KeyEvent.VK_D:
      ghostMove(Constants.POINT_RIGHT);
      break;
    // Son
    case KeyEvent.VK_F2:
      startStopSound();
      break;
    // Arret de la partie
    case KeyEvent.VK_ESCAPE:
      stopGame();
      break;
    // Partie en pause
    case KeyEvent.VK_PAUSE:
      gameInPause();
      break;
    // Default
    default:
      break;
    }
  }

  /**
   * Gestion des touches durant la pr�sentation
   *
   * @param keyCode
   */
  private void manageKeysPresentation(int keyCode) {
    // Gestion des touches durant la présentation
    switch (keyCode) {
    // Start Game
    case KeyEvent.VK_S: // 's' ou 'S'
      startGame();
      break;
    // Son
    case KeyEvent.VK_F2:
      startStopSound();
      break;
    // Configuration du jeu : mode 1 ou 2 joueur, choix du fant�me pour le joueur 2
    case KeyEvent.VK_C:
      setConfig();
      break;
    // Defaut
    default:
      break;
    }
  }

  /**
   * Jeu en pause
   */
  void gameInPause() {
    if (gameModel.gameTimerIsRunning())
      gameModel.stopGameTimer();
    else
      gameModel.startGameTimer();
  }

  /**
   * Déplacement de blinky
   *
   * @param direction
   */
  void ghostMove(Point direction) {
    gameModel.setGhostRequest(direction);
  }

  /**
   * Ladybug is moving
   *
   * @param direction
   */
  void ladybugMove(Point direction) {
    gameModel.getLadybug().setMovingRequete(direction);
  }

  /**
   * Jeu en mode configuration
   */
  void setConfig() {
    gameModel.getGameStatus().setToConfigutation();
  }

  /**
   * D�but du jeu
   */
  void startGame() {
    gameModel.getGameStatus().setInGame();
    gameModel.initGame();
    gameModel.initLevel();
  }

  /**
   * Start / Stop les sons
   */
  void startStopSound() {
    gameModel.getGameSounds().startStop();
  }

  /**
   * Arret forc� du jeu
   */
  void stopGame() {
    if (gameModel.gameTimerIsRunning()) {
      gameModel.setOldScore(-1);
      gameModel.getGameStatus().setStopGame();
      gameModel.initGame();
    }
  }
}