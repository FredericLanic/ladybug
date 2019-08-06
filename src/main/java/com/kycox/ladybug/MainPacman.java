package com.kycox.ladybug;

import java.awt.BorderLayout;
import java.lang.Runtime.Version;

import javax.swing.SwingUtilities;

import com.kycox.ladybug.engine.Engine;

/**
 * Classe principale du jeu
 * 
 * C'est elle à lancer :)
 */
public class MainPacman {

  /** Lancement du jeu */
  public static void main(String[] args) {
    new MainPacman().lannchTheGame();
  }

  Engine    engine;
  MainFrame mainFrame;

  public void lannchTheGame() {
    Version javaVersion = Runtime.version();

    System.out.println("***************************");
    System.out.println("*  LadyBug Survivor  v1.5 *");
    System.out.println("*  for Java 12 by �kycox  *");
    if (javaVersion.feature() == 12) {
      System.out.println("***************************");
      // Création du moteur Pacman
      engine = new Engine();

      // Création de la vue
      SwingUtilities.invokeLater(() -> {
        mainFrame = new MainFrame();

        mainFrame.setLayout(new BorderLayout());

        mainFrame.add("Center", engine.getPacmanView());
        // Affichage de la MainFrame
        mainFrame.setVisible(true);
      });

    } else {
      System.out.println("* Please Check your JVM   *");
      System.out.println("***************************");
    }
  }
}
