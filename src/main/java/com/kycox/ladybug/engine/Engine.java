package com.kycox.ladybug.engine;

import com.kycox.ladybug.engine.controler.KeyGameController;
import com.kycox.ladybug.engine.model.GameModel;
import com.kycox.ladybug.engine.view.GameView;

public class Engine {
  private KeyGameController pacmanController;
  private GameModel     pacmanModel;
  private GameView      pacmanView;

  public Engine() {
    // MODEL
    pacmanModel = new GameModel();

    // PAD CONTROLLER
//    PadController pacmanController = new PadController(pacmanModel);
    // Seulement gestion du clavier : on verra pour la manette après
    pacmanController = new KeyGameController(pacmanModel);

    // VIEW
    pacmanView = new GameView();
    // On relie le modèle à la view : la view connait le modèle, le modèle ne
    // connait pas la view
    pacmanView.setPacmanModel(pacmanModel);
    // On applique le contrôleur à la vue
    pacmanView.setController(pacmanController);
  }

  /**
   * Retourne la view
   * 
   * @return
   */
  public GameView getPacmanView() {
    return pacmanView;
  }
}
