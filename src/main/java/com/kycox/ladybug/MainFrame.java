package com.kycox.ladybug;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.engine.view.GameView;
import com.kycox.ladybug.engine.view.conf.ConfJDialog;

/**
 * Frame principale du jeu.
 *
 * FIXME : peut être utiliser une autre techno que swing
 *
 */
public class MainFrame extends JFrame {

  private static final long serialVersionUID = 1L;

  // Trace < Debug < info < Warn < Error < Fatal
//  final static Logger       logger           = LogManager.getLogger(MainFrame.class);

  private Toolkit           defaultToolKit   = Toolkit.getDefaultToolkit();

  private double            edge             = 15 * Constants.BLOCK_SIZE;

  private double            rightLeftWidth   = (defaultToolKit.getScreenSize().getWidth()
      - 15 * Constants.BLOCK_SIZE) / 2;

  private double            topBottomHeight  = (defaultToolKit.getScreenSize().getHeight()
      - 16 * Constants.BLOCK_SIZE) / 2;

  /**
   * Constructeur
   */
  public MainFrame() {
    init();
    new ConfJDialog(this);
  }

  /**
   * Création des JPanel de la JFrame
   *
   * @param gameView
   */
  public void addGameView(GameView gameView) {
    // organisation des panels

    Dimension dimension = new Dimension();
    dimension.setSize(edge, edge);
    gameView.setPreferredSize(dimension);
    gameView.setBackground(Color.BLACK);
    add(gameView, BorderLayout.CENTER);

    // @FIXME : un peu de refacto !!
    JPanel jPanel = new JPanel();
    dimension = new Dimension();
    dimension.setSize(defaultToolKit.getScreenSize().getWidth(), topBottomHeight);
    jPanel.setBackground(Color.BLACK);
    jPanel.setPreferredSize(dimension);
    add(jPanel, BorderLayout.PAGE_START);

    jPanel = new JPanel();
    dimension = new Dimension();
    dimension.setSize(rightLeftWidth, edge);
    jPanel.setBackground(Color.BLACK);
    jPanel.setPreferredSize(dimension);
    add(jPanel, BorderLayout.LINE_START);

    jPanel = new JPanel();
    dimension = new Dimension();
    dimension.setSize(defaultToolKit.getScreenSize().getWidth(), topBottomHeight);
    jPanel.setBackground(Color.BLACK);
    jPanel.setPreferredSize(dimension);
    add(jPanel, BorderLayout.PAGE_END);

    jPanel = new JPanel();
    dimension = new Dimension();
    dimension.setSize(rightLeftWidth, edge);
    jPanel.setBackground(Color.BLACK);
    jPanel.setPreferredSize(dimension);
    add(jPanel, BorderLayout.LINE_END);
  }

  /**
   * Initialisation de la fenêtre
   */
  private void init() {
//    logger.trace("Entering application");
    setTitle("LadyBug");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setExtendedState(JFrame.MAXIMIZED_BOTH);
    setUndecorated(true);
  }

}