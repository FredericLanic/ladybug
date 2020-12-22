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
package com.kycox.game.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Observable;
import java.util.Observer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.kycox.game.constant.Constants;
import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.contract.IGameModelForGameView;
import com.kycox.game.controller.KeyGameController;
import com.kycox.game.score.IncrementScore;
import com.kycox.game.view.conf.ConfJDialog;
import com.kycox.game.view.ghost.GhostView;
import com.kycox.game.view.ladybug.LadybugView;
import com.kycox.game.view.map.ScreenBlockView;

/**
 * Vue du jeu MVC
 *
 * @author kycox
 *
 */
@Named("GameView")
public class GameView extends JPanel implements Observer {
	private static final long				serialVersionUID = 1L;
	private ConfJDialog						confJDialog;
	@Inject
	private KeyGameController				gameController;
	private transient IGameModelForGameView	gameModel;
	@Inject
	private transient GhostView				ghostView;
	private boolean							hasBeenDrawOnce	 = false;
	@Inject
	private transient LadybugView			ladybugView;
	private JFrame							mainFrame		 = (JFrame) SwingUtilities.getRoot(this);
	private final Font						smallFont		 = new Font("CrackMan", Font.BOLD, 14);

	@PostConstruct
	public void init() {
		setFocusable(true);
		setBackground(Color.black);
		confJDialog = new ConfJDialog(mainFrame);
		confJDialog.setVisible(false);
		addKeyListener(gameController); // key listener pour les touches
	}

	@Override
	public void paintComponent(Graphics g) {
		if (gameModel != null) {
			super.paintComponent(g);
			draw(g);
		}
	}

	@Override
	public void update(Observable gameModel, Object used) {
		this.gameModel = (IGameModelForGameView) gameModel;
		repaint();
	}

	private void draw(Graphics g) {
		listenStartLevelJingle();
		Graphics2D g2d = (Graphics2D) g;
		drawMaze(g2d);
		if (gameModel.getCurrentGameStatus().isToConfiguration()) {
			confJDialog.setVisible(true);
			drawGhosts(g2d);
			// FIXME : Ici c'est la vue qui modifie le status du jeu; c'est mal; trouver une
			// autre solution
			gameModel.getCurrentGameStatus().setNoGame();
		} else if (gameModel.getCurrentGameStatus().isNoGame()) {
			drawGhosts(g2d);
			showIntroScreen(g2d);
		} else if (LadybugStatus.DYING.equals(gameModel.getLadybug().getStatus())) {
			drawGhosts(g2d);
			drawLadybugDying(g2d);
		} else if (!LadybugStatus.DEAD.equals(gameModel.getLadybug().getStatus())) {
			// jeu en cours
			drawLadybug(g2d);
			drawGhosts(g2d);
			drawScoresIncrement(g2d);
		}
	}

	private void drawGhosts(Graphics2D g2d) {
		gameModel.getGroupGhosts().getLstGhosts().stream()
		        .forEach(g -> g2d.drawImage(ghostView.getImage(g), g.getPosition().x + 1, g.getPosition().y + 1, this));
	}

	private void drawLadybug(Graphics2D g2d) {
		g2d.drawImage(ladybugView.getImage(gameModel.getLadybug().getViewDirection()),
		        gameModel.getLadybug().getPosition().x + 1, gameModel.getLadybug().getPosition().y + 1, this);
	}

	private void drawLadybugDying(Graphics2D g2d) {
		g2d.drawImage(gameModel.getLadybugDeathView().getImage(), gameModel.getLadybug().getPosition().x + 1,
		        gameModel.getLadybug().getPosition().y + 1, this);
	}

	private void drawMaze(Graphics2D g2d) {
		for (int y = 0; y < gameModel.getScreenData().getScreenHeight(); y += Constants.BLOCK_SIZE) {
			for (int x = 0; x < gameModel.getScreenData().getCurrentLevel().getNbrBlocksByLine()
			        * Constants.BLOCK_SIZE; x += Constants.BLOCK_SIZE) {
				// Affichage du screenBlock dans la Vue
				ScreenBlockView.display(g2d, gameModel.getScreenData(), x, y);
			}
		}
	}

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
			gameModel.getCurrentGameStatus().setBeginingLevel();
		}
	}

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
