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
