package com.kycox.ladybug.engine.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.constant.PicturesEnum;
import com.kycox.ladybug.engine.controler.KeyGameController;
import com.kycox.ladybug.engine.element.ghost.Ghost;
import com.kycox.ladybug.engine.element.pacman.set.PacmanStatusEnum;
import com.kycox.ladybug.engine.model.GameModel;
import com.kycox.ladybug.engine.view.body.GhostView;
import com.kycox.ladybug.engine.view.body.PacmanView;
import com.kycox.ladybug.engine.view.conf.ConfJDialog;
import com.kycox.ladybug.engine.view.map.ScreenBlockView;
import com.kycox.ladybug.score.IncrementScore;

/**
 * Vue du jeu MVC
 *
 * @author kycox
 *
 */
@SuppressWarnings("deprecation")
public class GameView extends JPanel implements Observer {
  /**
   * Default serialVersionUID
   */
  private static final long    serialVersionUID = 1L;
  private ConfJDialog          confJDialog;

  private transient GhostView  ghostView        = new GhostView();

  private boolean              hasBeenDrawOnce  = false;

  /**
   * R�cup�ration de la JFrame parent
   */
  private JFrame               mainFrame        = (JFrame) SwingUtilities.getRoot(this);

  // Données transitées par le pattern Observer
  private transient GameModel  pacmanModel;

  private transient PacmanView pacmanView       = new PacmanView();

  // font par défaut
  private final Font           smallFont        = new Font("Helvetica", Font.BOLD, 14);

  /**
   * Constructor
   */
  public GameView() {
    super();
    setFocusable(true);
    setBackground(Color.black);

    confJDialog = new ConfJDialog(mainFrame);
  }

//  @Override
//  public void addNotify() {
//    super.addNotify();
//  }

  /**
   * Dessine le composant
   */
  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    doDrawing(g);
  }

  /**
   * Applique le contrôleur
   *
   * @param pacmanController
   */
  public void setController(KeyGameController pacmanController) {
    addKeyListener(pacmanController); // key listener pour les touches
  }

  /**
   * Affecte le modèle Pacman
   *
   * @param pacmanModel
   */
  public void setPacmanModel(GameModel pacmanModel) {
    if (this.pacmanModel != null) {
      this.pacmanModel.deleteObserver(this);
    }
    this.pacmanModel = pacmanModel;
    if (this.pacmanModel != null) {
      this.pacmanModel.addObserver(this);
    }
  }

  /**
   * Récupère le PacmanModel envoyé par le modèle
   *
   * @param pacmanModel
   */
  @Override
  public void update(Observable pacmanModel, Object used) {
    this.pacmanModel = (GameModel) pacmanModel;
    repaint();
  }

  /**
   * Lancement du dessin
   *
   * @param g
   */
  private void doDrawing(Graphics g) {
    listenStartLevelJingle();

    Graphics2D g2d = (Graphics2D) g;

//    Dimension  d   = new Dimension(15 * Constants.BLOCK_SIZE, 15 * Constants.BLOCK_SIZE);
//    g2d.setColor(Color.black);
//    g2d.fillRect(0, 0, d.width, d.height);

    // Affichage du tableau de niveau et du score
    drawMaze(g2d);
    drawScoreAndLevel(g2d);

    if (pacmanModel.getGameStatus().isToConfiguration()) {
      confJDialog.setVisible(true);
      // FIXME : Ici c'est la vue qui modifie le status du jeu; c'est mal; trouver une
      // autre solution
      pacmanModel.getGameStatus().setStopGame();
    } else if (!pacmanModel.getGameStatus().isInGame()) {
      // Arrêt de tous les sons
      // FIXME : à mettre ailleurs
      pacmanModel.getGameSounds().stopAllSounds();
      drawGhosts(g2d);
      showIntroScreen(g2d);
    } else if (PacmanStatusEnum.isDying().test(pacmanModel.getPacman())) {
      drawGhosts(g2d);
      drawPacmanDying(g2d);
      // aller ! on écoute les sons sélectionnés
      // FIXME : à mettre ailleurs
      pacmanModel.getGameSounds().playSound();
    } else if (!PacmanStatusEnum.isDead().test(pacmanModel.getPacman())) {
      // jeu en cours
      drawPacman(g2d);
      drawGhosts(g2d);
      drawScoresIncrement(g2d);
      // aller ! on écoute les sons sélectionnés
      // FIXME : à mettre ailleurs
      pacmanModel.getGameSounds().playSound();
    }
  }

  /**
   * Affiche les fantômes "vivants"
   *
   * @param g2d
   */
  private void drawGhosts(Graphics2D g2d) {
    pacmanModel.getGroupGhosts().getLstGhosts().stream().forEach(g -> g2d
        .drawImage(ghostView.getImage(g), g.getPosition().x + 1, g.getPosition().y + 1, this));
  }

  /**
   * Affichage du labyrinthe
   *
   * @param g2d
   */
  private void drawMaze(Graphics2D g2d) {
    for (int y = 0; y < pacmanModel.getScreenData().getScreenHeight(); y += Constants.BLOCK_SIZE) {
      for (int x = 0; x < pacmanModel.getScreenData().getNbrBlocksPerLine()
          * Constants.BLOCK_SIZE; x += Constants.BLOCK_SIZE) {
        // Récupère le screenBlock dédié pour la vue
//        ScreenBlock screenBlock = pacmanModel.getScreenData()
//            .getViewBlock(Utils.convertPointToBlockUnit(new Point(x, y)));
        // Affichage du screenBlock dans la Vue
        ScreenBlockView.display(g2d, pacmanModel.getScreenData(), x, y);
      }
    }
  }

  /**
   * Affichage de pacman selon sa direction
   *
   * @param g2d
   */
  private void drawPacman(Graphics2D g2d) {
    g2d.drawImage(pacmanView.getNextImage(pacmanModel.getPacman()),
        pacmanModel.getPacman().getPosition().x + 1, pacmanModel.getPacman().getPosition().y + 1,
        this);
  }

  /**
   * Affichage de parman qui meurt
   *
   * @param g2d
   */
  private void drawPacmanDying(Graphics2D g2d) {
    g2d.drawImage(pacmanModel.getKinematicPacmanDeath().getImage(),
        pacmanModel.getPacman().getPosition().x + 1, pacmanModel.getPacman().getPosition().y + 1,
        this);
  }

  /**
   * Dessine le nombre de pacman encore disponible et le score.
   *
   * @param g
   */
  private void drawScoreAndLevel(Graphics2D g) {
    // parfois le mod�le ne se charge pas tout de suite
    if (pacmanModel == null)
      return;

    int    x = pacmanModel.getScreenData().getScreenWidth();
    int    y = pacmanModel.getScreenData().getScreenHeight();

    int    i;
    String s = "";

    g.setFont(smallFont);
    g.setColor(new Color(96, 128, 255));
    if (pacmanModel.getGameStatus().isInGame()) {
      s = "Level " + pacmanModel.getGameStatus().getNumLevel() + " - Score: "
          + pacmanModel.getGameScore().getScore();
      g.drawString(s, x / 2 + 26, y + 16);

      // Affichage des vies de pacman
      if (pacmanModel.getPacman().getLifesLeft() < 5) {
        for (i = 0; i < pacmanModel.getPacman().getLifesLeft(); i++) {
          g.drawImage(PicturesEnum.PACMAN_LEFT_3.getImg(), i * 28 + 8, y + 1, this);
        }
      } else {
        g.drawImage(PicturesEnum.PACMAN_LEFT_3.getImg(), 8, y + 1, this);
        g.setColor(Color.YELLOW);
        g.setFont(smallFont);
        g.drawString("x" + pacmanModel.getPacman().getLifesLeft(), 34, y + 20);
      }

      // affichage des vies du fantômes
      Ghost ghostNotComputed = pacmanModel.getGroupGhosts().getGhostNotComputed();
      if (ghostNotComputed != null && ghostNotComputed.getLifesLeft() < 5) {
        for (i = 0; i < ghostNotComputed.getLifesLeft(); i++) {
          g.drawImage(ghostNotComputed.getGhostSettings().getGhostLeftEyesImg(), i * 28 + 8, y + 20,
              this);
        }
      } else if (ghostNotComputed != null) {
        g.drawImage(ghostNotComputed.getGhostSettings().getGhostLeftEyesImg(), 8, y + 20, this);
        g.setColor(Color.GRAY);
        g.setFont(smallFont);
        g.drawString("x" + ghostNotComputed.getLifesLeft(), 34, y + 38);
      }
    } else {
      int nbrPlayers = pacmanModel.nbrNbrPlayers();
      s = "Game conf : " + nbrPlayers + " player" + (nbrPlayers > 1 ? "s" : "");
      g.drawString(s, x / 2 + 26, y + 16);
    }
  }

  /**
   * Draw score increment
   *
   * @param g2d
   */
  private void drawScoresIncrement(Graphics2D g2d) {
    g2d.setColor(Color.WHITE);
    g2d.setFont(smallFont);

    FontMetrics metr = this.getFontMetrics(smallFont);
    int         x;
    int         y;

    // Affichage des scores incréments
    for (IncrementScore scorePoint : pacmanModel.getGroupIncrementScores()
        .getLstScoresIncrement()) {
      x = scorePoint.getPosition().x + Constants.BLOCK_SIZE / 2
          - metr.stringWidth(scorePoint.getValue()) / 2;
      y = scorePoint.getPosition().y + Constants.BLOCK_SIZE / 2;
      g2d.drawString(scorePoint.getValue() + " pt", x, y);
    }
  }

  /**
   * Ecoute le jingle de début
   *
   * C'est un contournement !!! Il faut que le niveau s'affiche pour pouvoir entendre le jingle.
   *
   * Du coup, le jingle se lancera au deuxième affichage de la map du niveau.
   *
   * Lorsque qu'on écoutera le jingle, le jeu est frisé.
   */
  private void listenStartLevelJingle() {
    // Utile pour entendre le jingle du début :
    // Le mod�le annonce le début du niveau. La vue dans ce cas là, n'a pas encore
    // pu afficher le niveau à l'écran.
    // Elle doit faire un affichage et ensuite pouvoir lancer le jingle.
    if (pacmanModel.isBeginNewLevel()) {
      pacmanModel.setBeginNewLevel(false);
      hasBeenDrawOnce = true;
    } else if (hasBeenDrawOnce) {
      hasBeenDrawOnce = false;
      pacmanModel.getGameSounds().init();
      // stop timer
      pacmanModel.stopGameTimer();
      pacmanModel.getGameSounds().playStartJingle();
      // retstart timer
      pacmanModel.startGameTimer();
    }
  }

  /**
   * Affiche la fenêtre d'introduction
   *
   * @param g2d
   */
  private void showIntroScreen(Graphics2D g2d) {
    int    x            = pacmanModel.getScreenData().getScreenWidth();
    int    y            = pacmanModel.getScreenData().getScreenHeight();
    int    addXGameOver = pacmanModel.getOldScore() != -1 ? 30 : 0;

    String gameOver     = "Game Over, try again...";
    String yourOldScore = "Your Score : " + pacmanModel.getOldScore();
    String s            = "Press s to start, c to configurate";

    g2d.setColor(new Color(0, 32, 48));
    g2d.fillRect(50, x / 2 - 30, y - 100, 50 + addXGameOver);
    g2d.setColor(Color.white);
    g2d.drawRect(50, x / 2 - 30, y - 100, 50 + addXGameOver);

    FontMetrics metr = this.getFontMetrics(smallFont);

    g2d.setColor(Color.white);
    g2d.setFont(smallFont);

    if (pacmanModel.getOldScore() > 0) {
      // Affichage de "Game Over"
      g2d.drawString(gameOver, (x - metr.stringWidth(gameOver)) / 2, y / 2);
      // Affichage du score
      g2d.drawString(yourOldScore, (x - metr.stringWidth(yourOldScore)) / 2,
          y / 2 + addXGameOver / 2);
    }
    g2d.drawString(s, (x - metr.stringWidth(s)) / 2, y / 2 + addXGameOver);
  }
}
