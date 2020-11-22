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
package com.kycox.ladybug.view;

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

import com.kycox.ladybug.body.ghost.Ghost;
import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.constant.PicturesEnum;
import com.kycox.ladybug.constant.ladybug.LadybugStatusEnum;
import com.kycox.ladybug.contract.IGameModelForGameView;
import com.kycox.ladybug.controler.KeyGameController;
import com.kycox.ladybug.score.IncrementScore;
import com.kycox.ladybug.view.body.GhostView;
import com.kycox.ladybug.view.body.LadybugView;
import com.kycox.ladybug.view.conf.ConfJDialog;
import com.kycox.ladybug.view.map.ScreenBlockView;

import lombok.Setter;

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
	private static final long serialVersionUID = 1L;
	private ConfJDialog		  confJDialog;
	@Setter
	private KeyGameController gameController;
	// Données transitées par le pattern Observer
	private transient IGameModelForGameView	gameModel;
	@Setter
	private transient GhostView			ghostView;
	private boolean						hasBeenDrawOnce	= false;
	@Setter
	private transient LadybugView		ladybugView;
	/**
	 * R�cup�ration de la JFrame parent
	 */
	private JFrame						mainFrame		= (JFrame) SwingUtilities.getRoot(this);
	// font par défaut
	private final Font smallFont = new Font("CrackMan", Font.BOLD, 14);

	public void init() {
		setFocusable(true);
		setBackground(Color.black);
		confJDialog = new ConfJDialog(mainFrame);
		confJDialog.setVisible(false);
		addKeyListener(gameController); // key listener pour les touches
	}

	/**
	 * Dessine le composant
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		doDrawing(g);
	}

	/**
	 * Récupère le gameModel envoyé par le modèle
	 *
	 * @param gameModel
	 */
	@Override
	public void update(Observable gameModel, Object used) {
		this.gameModel = (IGameModelForGameView) gameModel;
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
		// Affichage du tableau de niveau et du score
		drawMaze(g2d);
		drawScoreAndLevel(g2d);
		if (gameModel.getGameStatus().isToConfiguration()) {
			confJDialog.setVisible(true);
			drawGhosts(g2d);
			// FIXME : Ici c'est la vue qui modifie le status du jeu; c'est mal; trouver une
			// autre solution
			gameModel.getGameStatus().setNoGame();
		} else if (gameModel.getGameStatus().isNoGame()) {
			drawGhosts(g2d);
			showIntroScreen(g2d);
		} else if (LadybugStatusEnum.isDying().test(gameModel.getLadybug())) {
			drawGhosts(g2d);
			drawLadybugDying(g2d);
		} else if (!LadybugStatusEnum.isDead().test(gameModel.getLadybug())) {
			// jeu en cours
			drawLadybug(g2d);
			drawGhosts(g2d);
			drawScoresIncrement(g2d);
		}
	}

	/**
	 * Affiche les fantômes "vivants"
	 *
	 * @param g2d
	 */
	private void drawGhosts(Graphics2D g2d) {
		gameModel.getGroupGhosts().getLstGhosts().stream()
		        .forEach(g -> g2d.drawImage(ghostView.getImage(g), g.getPosition().x + 1, g.getPosition().y + 1, this));
	}

	/**
	 * Affichage de ladybug selon sa direction
	 *
	 * @param g2d
	 */
	private void drawLadybug(Graphics2D g2d) {
		g2d.drawImage(ladybugView.getNextImage(gameModel.getLadybug()), gameModel.getLadybug().getPosition().x + 1,
		        gameModel.getLadybug().getPosition().y + 1, this);
	}

	/**
	 * Affichage de parman qui meurt
	 *
	 * @param g2d
	 */
	private void drawLadybugDying(Graphics2D g2d) {
		g2d.drawImage(gameModel.getKinematicLadybugDeath().getImage(), gameModel.getLadybug().getPosition().x + 1,
		        gameModel.getLadybug().getPosition().y + 1, this);
	}

	/**
	 * Affichage du labyrinthe
	 *
	 * @param g2d
	 */
	private void drawMaze(Graphics2D g2d) {
		for (int y = 0; y < gameModel.getScreenData().getScreenHeight(); y += Constants.BLOCK_SIZE) {
			for (int x = 0; x < gameModel.getScreenData().getCurrentLevel().getNbrBlocksByLine()
			        * Constants.BLOCK_SIZE; x += Constants.BLOCK_SIZE) {
				// Affichage du screenBlock dans la Vue
				ScreenBlockView.display(g2d, gameModel.getScreenData(), x, y);
			}
		}
	}

	/**
	 * Dessine le nombre de ladybug encore disponible et le score.
	 *
	 * @param g
	 */
	private void drawScoreAndLevel(Graphics2D g) {
		// parfois le modèle ne se charge pas tout de suite
		if (gameModel == null)
			return;
		int	   x = gameModel.getScreenData().getScreenWidth();
		int	   y = gameModel.getScreenData().getScreenHeight();
		int	   i;
		String s = "";
		g.setFont(smallFont);
		g.setColor(new Color(96, 128, 255));
		if (gameModel.getGameStatus().isInGame()) {
			s = "Level " + gameModel.getGameStatus().getNumLevel() + " - Score: " + gameModel.getGameScore().getScore();
			g.drawString(s, x / 2 + 26, y + 16);
			// Affichage des vies de ladybug
			if (gameModel.getLadybug().getLeftLifes() < 1) {
				for (i = 0; i < gameModel.getLadybug().getLeftLifes(); i++) {
					g.drawImage(PicturesEnum.LADYBUG_LEFT_3.getImg(), i * 28 + 8, y + 1, this);
				}
			} else {
				g.drawImage(PicturesEnum.LADYBUG_LEFT_3.getImg(), 8, y + 1, this);
				g.setColor(Color.YELLOW);
				g.setFont(smallFont);
				g.drawString("x " + gameModel.getLadybug().getLeftLifes(),
				        Constants.BLOCK_SIZE + Constants.BLOCK_SIZE / 3, y + Constants.BLOCK_SIZE / 2);
			}
			// affichage des vies du fantômes
			Ghost ghostNotComputed = gameModel.getGroupGhosts().getGhostNotComputed();
			if (ghostNotComputed != null && ghostNotComputed.getLeftLifes() < 5) {
				for (i = 0; i < ghostNotComputed.getLeftLifes(); i++) {
					g.drawImage(ghostNotComputed.getImages().getGhostLeftEyesImg(), i * 28 + 8, y + 20, this);
				}
			} else if (ghostNotComputed != null) {
				g.drawImage(ghostNotComputed.getImages().getGhostLeftEyesImg(), 8, y + 20, this);
				g.setColor(Color.GRAY);
				g.setFont(smallFont);
				g.drawString("x" + ghostNotComputed.getLeftLifes(), 34, y + 38);
			}
		} else {
			int nbrPlayers = gameModel.getNbrPlayers();
			s = "Game config : " + nbrPlayers + " player" + (nbrPlayers > 1 ? "s" : "");
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
		FontMetrics	metr = this.getFontMetrics(smallFont);
		int			x;
		int			y;
		// Affichage des scores incréments
		for (IncrementScore scorePoint : gameModel.getGroupIncrementScores().getLstIncrementScore()) {
			x = scorePoint.getPosition().x + Constants.BLOCK_SIZE / 2 - metr.stringWidth(scorePoint.getValue()) / 2;
			y = scorePoint.getPosition().y + Constants.BLOCK_SIZE / 2;
			g2d.drawString(scorePoint.getValue() + " pt", x, y);
		}
	}

	/**
	 * Ecoute le jingle de début
	 *
	 * C'est un contournement !!! Il faut que le niveau s'affiche pour pouvoir
	 * entendre le jingle.
	 *
	 * Du coup, le jingle se lancera au deuxième affichage de la map du niveau.
	 *
	 * Lorsque qu'on écoutera le jingle, le jeu est frisé.
	 */
	private void listenStartLevelJingle() {
		// Utile pour entendre le jingle du début :
		// Le modèle annonce le début du niveau. La vue dans ce cas là, n'a pas encore
		// pu afficher le niveau à l'écran.
		// Elle doit faire un affichage et ensuite pouvoir lancer le jingle.
		if (gameModel.isBeginNewLevel()) {
			gameModel.setBeginNewLevel(false);
			hasBeenDrawOnce = true;
		} else if (hasBeenDrawOnce) {
			hasBeenDrawOnce = false;
			// FIXME : changement du status du jeu à partir de la vue : c'est le mal !!
			gameModel.getGameStatus().setBeginingLevel();
		}
	}

	/**
	 * Affiche la fenêtre d'introduction
	 *
	 * @param g2d
	 */
	private void showIntroScreen(Graphics2D g2d) {
		int	   x			= gameModel.getScreenData().getScreenWidth();
		int	   y			= gameModel.getScreenData().getScreenHeight();
		int	   addXGameOver	= gameModel.getGameScore().getOldScore() != -1 ? 30 : 0;
		String gameOver		= "Game Over, try again...";
		String yourOldScore	= "Your Score : " + gameModel.getGameScore().getOldScore();
		String s			= "Press s to start, c to configurate";
		g2d.setColor(new Color(0, 32, 48));
		g2d.fillRect(50, x / 2 - 30, y - 100, 50 + addXGameOver);
		g2d.setColor(Color.white);
		g2d.drawRect(50, x / 2 - 30, y - 100, 50 + addXGameOver);
		FontMetrics metr = this.getFontMetrics(smallFont);
		g2d.setColor(Color.white);
		g2d.setFont(smallFont);
		if (gameModel.getGameScore().getOldScore() > 0) {
			// Affichage de "Game Over"
			g2d.drawString(gameOver, (x - metr.stringWidth(gameOver)) / 2, y / 2);
			// Affichage du score
			g2d.drawString(yourOldScore, (x - metr.stringWidth(yourOldScore)) / 2, y / 2 + addXGameOver / 2);
		}
		g2d.drawString(s, (x - metr.stringWidth(s)) / 2, y / 2 + addXGameOver);
	}
}
