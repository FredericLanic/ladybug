package com.kycox.ladybug;

import javax.swing.JFrame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kycox.ladybug.engine.view.conf.ConfJDialog;

/**
 * Frame principale du jeu.
 * 
 * FIXME : peut être utiliser une autre techno que swing
 *
 */
public class MainFrame extends JFrame {

  // Trace < Debug < info < Warn < Error < Fatal
  final static Logger       logger           = LogManager.getLogger(MainFrame.class);

  private static final long serialVersionUID = 1L;

  /**
   * Constructeur
   * 
   * @param pacmanController
   */
  public MainFrame() {
    init();
    new ConfJDialog(this);
  }

  /**
   * Initialisation de la fenêtre
   */
  private void init() {
    logger.trace("Entering application");
    setTitle("LadyBug");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(380, 440);
    setLocationRelativeTo(null);
  }
}