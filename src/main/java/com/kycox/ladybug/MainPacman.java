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
public class MainPacman {

  /** Lancement du jeu */
  public static void main(String[] args) {
    new MainPacman().launchTheGame();
  }

  Engine    engine;
  MainFrame mainFrame;

  public void launchTheGame() {
    Version javaVersion = Runtime.version();

    System.out.println("******************************");
    System.out.println("*  LadyBug v0.0.1 SnapShot   *");
    System.out.println("*  for Java 12 by �kycox     *");
    if (javaVersion.feature() == 12) {
      System.out.println("******************************");
      // Création du moteur Pacman
      engine = new Engine();

      // Création de la vue
      SwingUtilities.invokeLater(() -> {

        // Création du JPanel de la View du jeu
        GameView gameView = engine.getPacmanView();
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
