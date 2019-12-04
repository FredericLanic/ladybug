package com.kycox.ladybug.engine;

import com.kycox.ladybug.engine.controler.KeyGameController;
import com.kycox.ladybug.engine.model.GameModel;
import com.kycox.ladybug.engine.view.GameView;

public class Engine {
  private GameModel         gameModel;
  private GameView          gameView;
  private KeyGameController keyGameController;

  public Engine() {
    // MODEL
    gameModel = new GameModel();

    // PAD CONTROLLER
//    PadController padController = new PadController(gameModel);
    // Seulement gestion du clavier : on verra pour la manette après
    keyGameController = new KeyGameController(gameModel);

    // VIEW
    gameView = new GameView();
    // On relie le modèle à la view : la view connait le modèle, le modèle ne
    // connait pas la view
    gameView.setGameModel(gameModel);
    // On applique le contrôleur à la vue
    gameView.setController(keyGameController);
  }

  /**
   * Retourne la view
   *
   * @return
   */
  public GameView getGameView() {
    return gameView;
  }
}
