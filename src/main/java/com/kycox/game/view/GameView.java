/**
  Copyright (C) 2021  Frédéric Lanic frederic.lanic@outlook.fr

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
import java.awt.Image;
import java.awt.Point;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kycox.game.body.ghost.Ghost;
import com.kycox.game.constant.Constants;
import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.contract.IDoActionAfterTimer;
import com.kycox.game.contract.IGameModelForGameView;
import com.kycox.game.controller.KeyGameController;
import com.kycox.game.font.PacFont;
import com.kycox.game.score.Message;
import com.kycox.game.timer.WaitAndDoActionAfterTimer;
import com.kycox.game.tools.Utils;
import com.kycox.game.view.conf.ConfJDialog;
import com.kycox.game.view.ghost.GhostView;
import com.kycox.game.view.ladybug.LadybugCommun;
import com.kycox.game.view.ladybug.LadybugDyingView;
import com.kycox.game.view.ladybug.LadybugView;
import com.kycox.game.view.map.ScreenBlockView;

import lombok.Setter;

/**
 * Vue du jeu MVC
 *
 * @author kycox
 *
 */
@Named("GameView")
public class GameView extends JPanel implements Observer, IDoActionAfterTimer {
	private static final Log	  logger		   = LogFactory.getLog(GameView.class);
	private static final long	  serialVersionUID = 1L;
	private ConfJDialog			  confJDialog;
	private final Font			  defaultFont	   = PacFont.PACFONT.getDefaultFont();
	@Setter
	private long				  durationLadybugNewLife;
	@Inject
	private KeyGameController	  gameController;
	private IGameModelForGameView gameModel;
	@Inject
	private GhostView			  ghostView;
	@Inject
	private LadybugDyingView	  ladybugDyingView;
	@Inject
	private LadybugView			  ladybugView;
	private JFrame				  mainFrame		   = (JFrame) SwingUtilities.getRoot(this);
	// todo : sécuriser le timer en mode deux joueurs
	private WaitAndDoActionAfterTimer newLiveTimer;
	private final Font			 scoreFont = new Font("CrackMan", Font.BOLD, 14);

	@Override
	public void doActionAfterTimer(int nbrAction) {
		switch (nbrAction) {
			case 0 : ScreenBlockView.setBlueLadybug(Constants.BLUE_LADYBUG);
				break;
			default : logger.debug("no number " + nbrAction + " action");
		}
	}

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
		Graphics2D g2d = (Graphics2D) g;
		drawMaze(g2d);
		if (gameModel.getLadybug().isNewLife()) {
			ScreenBlockView.setBlueLadybug(Constants.COLOR_EXTRA_PAC_LADYBUG);
			newLiveTimer = new WaitAndDoActionAfterTimer();
			newLiveTimer.launch(durationLadybugNewLife, this, 0);
		}
		if (gameModel.getCurrentGameStatus().isToConfiguration()) {
			confJDialog.setVisible(true);
			drawGhosts(g2d);
			// FIXME : Ici c'est la vue qui modifie le status du jeu; c'est mal; trouver une autre solution
			gameModel.getCurrentGameStatus().setGamePresentation();
		} else if (gameModel.getCurrentGameStatus().isProgramStarting()) {
			drawOneCenterTextLine(g2d, "WELCOME TO lADYBUG");	
			drawPresentationGhosts(g2d);
		} else if (gameModel.getCurrentGameStatus().isLevelStarting()) {
			drawLadybug(g2d, ladybugView);
			drawGhosts(g2d);			
			String text = "lEVEL " + Utils.integerToRoman(gameModel.getCurrentGameStatus().getNumLevel()).toLowerCase();
			drawOneCenterTextLine(g2d, text);
		} else if (gameModel.getCurrentGameStatus().isGamePresentation()) {
			drawGhosts(g2d);
			showIntroScreen(g2d);
		} else if (gameModel.getCurrentGameStatus().isGameEnding() || gameModel.getCurrentGameStatus().isGameEnd()) {
			drawGhosts(g2d);
			drawOneCenterTextLine(g2d, "gAME oVER");			
		} else if (gameModel.getCurrentGameStatus().isLevelEnding()) {
			drawOneCenterTextLine(g2d, "nEXT lEVEL");
		} else if (LadybugStatus.DYING.equals(gameModel.getLadybug().getStatus())) {
			ladybugDyingView.inProgress();
			drawGhosts(g2d);
			drawLadybug(g2d, ladybugDyingView);
		} else if (LadybugStatus.DEAD.equals(gameModel.getLadybug().getStatus())) {
			ladybugDyingView.init();
		} else if (!LadybugStatus.DEAD.equals(gameModel.getLadybug().getStatus())) {
			drawLadybug(g2d, ladybugView);
			drawGhosts(g2d);
			drawScoresIncrement(g2d);
		} else {
			logger.info("NO DISPLAY FOR STATUS " + gameModel.getCurrentGameStatus());
		}
	}

	private void drawGhosts(Graphics2D g2d) {
		gameModel.getGroupGhosts().getLstGhosts().stream()
		        .forEach(g -> g2d.drawImage(ghostView.getImage(g), g.getPosition().x + 1, g.getPosition().y + 1, this));
	}

	/*
	 * FIXME : utiliser les streams comme dans drawGhosts(Graphics2D g2d)
	 */
	private void drawPresentationGhosts(Graphics2D g2d) {		
		int			x	 = gameModel.getScreenData().getScreenWidth() / 2 - (7 * Constants.BLOCK_SIZE) / 2;
		int			y	 = gameModel.getScreenData().getScreenHeight() / 2;	
		
		List<Ghost> ghosts = gameModel.getGroupGhosts().getLstGhosts().stream().collect(Collectors.toList());
	    for (Ghost ghost : ghosts) {	    	
	    	g2d.drawImage(ghostView.getImage(ghost), x, y, this);
	    	x += 2 * Constants.BLOCK_SIZE;
	    }		
	}

	private void drawLadybug(Graphics2D g2d, LadybugCommun ladybugCommon) {
		Point viewDirection	= gameModel.getLadybug().getViewDirection();
		Point getPosition	= gameModel.getLadybug().getPosition();
		Image image			= ladybugCommon.getImage(viewDirection);
		g2d.drawImage(image, getPosition.x + 1, getPosition.y + 1, this);
	}
	
	private void drawOneCenterTextLine(Graphics2D g2d, String text) {
		int			x	 = gameModel.getScreenData().getScreenWidth();
		int			y	 = gameModel.getScreenData().getScreenHeight();		
		FontMetrics	metr = this.getFontMetrics(defaultFont);
		g2d.setColor(Color.white);
		g2d.setFont(defaultFont);
		g2d.drawString(text, (x - metr.stringWidth(text)) / 2, y / 2 - Constants.BLOCK_SIZE);
	}	

	private void drawMaze(Graphics2D g2d) {
		for (int y = 0; y < gameModel.getScreenData().getScreenHeight(); y += Constants.BLOCK_SIZE) {
			for (int x = 0; x < gameModel.getScreenData().getCurrentLevel().getNbrBlocksByLine()
			        * Constants.BLOCK_SIZE; x += Constants.BLOCK_SIZE) {
				ScreenBlockView.display(g2d, gameModel.getScreenData(), x, y);
			}
		}
	}

	private void drawScoresIncrement(Graphics2D g2d) {
		g2d.setColor(Color.WHITE);
		g2d.setFont(scoreFont);
		FontMetrics	metr = this.getFontMetrics(scoreFont);
		int			x;
		int			y;
		// Affichage des scores incréments
		for (Message scorePoint : gameModel.getGroupMessages().getMessages()) {
			x = scorePoint.getPosition().x + Constants.BLOCK_SIZE / 2 - metr.stringWidth(scorePoint.getValue()) / 2;
			y = scorePoint.getPosition().y + Constants.BLOCK_SIZE / 2;
			g2d.drawString(scorePoint.getValue() + " pt", x, y);
		}
	}

	private void showIntroScreen(Graphics2D g2d) {
		int			x			  = gameModel.getScreenData().getScreenWidth();
		int			y			  = gameModel.getScreenData().getScreenHeight();
		String		startMessage  = "PRESS s TO sTART";
		String		configMessage = "OR c TO cONFIG";
		FontMetrics	metr		  = this.getFontMetrics(defaultFont);
		g2d.setColor(Color.white);
		g2d.setFont(defaultFont);
		g2d.drawString(startMessage, (x - metr.stringWidth(startMessage)) / 2, y / 2 - Constants.BLOCK_SIZE);
		g2d.drawString(configMessage, (x - metr.stringWidth(configMessage)) / 2, y / 2 + Constants.BLOCK_SIZE);
	}
}
