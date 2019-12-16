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
package com.kycox.ladybug;

import java.lang.Runtime.Version;

import javax.swing.SwingUtilities;

import com.kycox.ladybug.engine.Engine;
import com.kycox.ladybug.engine.view.GameView;

/**
 * Classe principale du jeu
 *
 * C'est elle à lancer :)
 */
public class MainLadybug {

  /** Lancement du jeu */
  public static void main(String[] args) {
    new MainLadybug().launchTheGame();
  }

  Engine    engine;
  MainFrame mainFrame;

  public void launchTheGame() {
    Version javaVersion = Runtime.version();

    System.out.println("******************************");
    System.out.println("*  LadyBug v0.0.1 SnapShot   *");
    System.out.println("*  for Java 12 by kycox      *");
    if (javaVersion.feature() == 12) {
      System.out.println("******************************");
      // Création du moteur du jeu
      engine = new Engine();

      // Création de la vue
      SwingUtilities.invokeLater(() -> {

        // Création du JPanel de la View du jeu
        GameView gameView = engine.getGameView();
        mainFrame = new MainFrame();
        mainFrame.addGameView(gameView);

        // Affichage de la MainFrame
        mainFrame.setVisible(true);
      });

    } else {
      System.out.println("*   Please Check your JVM    *");
      System.out.println("******************************");
    }
  }
}
