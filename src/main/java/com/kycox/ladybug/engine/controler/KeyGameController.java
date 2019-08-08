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
   * Le mod�le
   */
  protected GameModel pacmanModel;

  /**
   * La vue
   */
  private GameView    pacmanView;

  /**
   * Constructeur
   * 
   * @param pacmanModel
   */
  public KeyGameController(GameModel pacmanModel) {
    this.pacmanModel = pacmanModel;
  }

  /**
   * Jeu en pause
   */
  void gameInPause() {
    if (pacmanModel.gameTimerIsRunning())
      pacmanModel.stopGameTimer();
    else
      pacmanModel.startGameTimer();
  }

  /**
   * Retourne la vue
   * 
   * @return
   */
  public GameView getPacmanView() {
    return pacmanView;
  }

  /**
   * Déplacement de blinky
   * 
   * @param direction
   */
  void ghostMove(Point direction) {
    pacmanModel.setGhostRequest(direction);
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

    if (pacmanModel.getGameStatus().isInGame()) {
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
    // Mouvement Pacman
    case KeyEvent.VK_LEFT:
      pacmanMove(Constants.POINT_LEFT);
      break;
    case KeyEvent.VK_RIGHT:
      pacmanMove(Constants.POINT_RIGHT);
      break;
    case KeyEvent.VK_UP:
      pacmanMove(Constants.POINT_UP);
      break;
    case KeyEvent.VK_DOWN:
      pacmanMove(Constants.POINT_DOWN);
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
    // Gestion des touches durant la pr�sentation
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
   * Pacman is moving
   * 
   * @param direction
   */
  void pacmanMove(Point direction) {
    pacmanModel.getPacman().setMovingRequete(direction);
  }

  /**
   * Jeu en mode configuration
   */
  void setConfig() {
    pacmanModel.getGameStatus().setToConfigutation();
  }

  /**
   * D�but du jeu
   */
  void startGame() {
    pacmanModel.getGameStatus().setInGame();
    pacmanModel.initGame();
    pacmanModel.initLevel();
  }

  /**
   * Start / Stop les sons
   */
  void startStopSound() {
    pacmanModel.getGameSounds().startStop();
  }

  /**
   * Arret forc� du jeu
   */
  void stopGame() {
    if (pacmanModel.gameTimerIsRunning()) {
      pacmanModel.setOldScore(-1);
      pacmanModel.getGameStatus().setStopGame();
      pacmanModel.initGame();
    }
  }
}