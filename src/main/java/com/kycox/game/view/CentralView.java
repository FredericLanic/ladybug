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
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import com.kycox.game.contract.DoActionAfterTimer;
import com.kycox.game.contract.GameModelForViews;
import com.kycox.game.controller.KeyGameController;
import com.kycox.game.font.GameFont;
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
@Named("CentralView")
public class CentralView extends JPanel implements Observer, DoActionAfterTimer {
	private static final Log logger = LogFactory.getLog(CentralView.class);
	private static final long serialVersionUID = 1L;
	private ConfJDialog confJDialog;
	private final Font defaultFont = GameFont.PACFONT.getDefaultFont();
	@Setter
	private long durationLadybugNewLife;
	@Inject
	private KeyGameController gameController;
	private GameModelForViews gameModel;
	@Inject
	private GhostView ghostView;
	@Inject
	private LadybugDyingView ladybugDyingView;
	@Inject
	private LadybugView ladybugView;
	private JFrame mainFrame = (JFrame) SwingUtilities.getRoot(this);
	private final Font scoreFont = new Font("CrackMan", Font.BOLD, 14);
	@Inject
	private ScreenBlockView screenBlockView;

	@Override
	public void doActionAfterTimer(int nbrAction) {
		switch (nbrAction) {
			case 0:
				screenBlockView.setColorMaze(Constants.BLUE_LADYBUG);
				break;
			default:
				logger.debug("no number " + nbrAction + " action");
		}
	}

	/**
	 * FIXME : même remarque que dans le modèle du jeu; appliquer un design pattern;
	 * ça devient un peu dificile à lire
	 *
	 * @param g
	 */
	private void draw(Graphics g) {
		var g2d = (Graphics2D) g;
		drawMaze(g2d);
		if (gameModel.getLadybug().isNewLife()) {
			screenBlockView.setColorMaze(Constants.COLOR_EXTRA_PAC_LADYBUG);
			var newLiveTimer = new WaitAndDoActionAfterTimer();
			newLiveTimer.launch(durationLadybugNewLife, this, 0);
		}
		if (gameModel.getCurrentProgramStatus().isToConfiguration()) {
			confJDialog.setVisible(true);
			drawGhosts(g2d);
			// FIXME : Ici c'est la vue qui modifie le status du jeu; c'est mal; trouver une
			// autre solution
			gameModel.getCurrentProgramStatus().setProgramPresentation();
		} else if (gameModel.getCurrentProgramStatus().isProgramStarting()) {
			drawOneCenterTextLine(g2d, "wELCOME TO lADYBUG");
			drawPresentationGhosts(g2d);
		} else if (gameModel.getCurrentProgramStatus().isGameStarting()) {
			drawThreeCenterTextLines(g2d, "eNJOY", "yOUR GAME", "gET READY");
		} else if (gameModel.getCurrentProgramStatus().isLevelStarting()) {
			drawLadybug(g2d, ladybugView);
			drawGhosts(g2d);
			var text = "lEVEL " + Utils.integerToRoman(gameModel.getCurrentProgramStatus().getNumLevel()).toLowerCase();
			drawOneCenterTextLine(g2d, text);
		} else if (gameModel.getCurrentProgramStatus().isProgramPresentation()) {
			drawGhosts(g2d);
			if (gameModel.isShowHelp()) {
				drawSevenCenterTextLines(g2d, "hELP", "FII: sOUND ON/OFF", "FIII: lADYBUG sKIN", "FIV: gHOST hEADBAND",
				        "FV: gHOST hAT", "ARROWS: lADYBUG mOVE", "ZQSD: gHOST mOVE");
			} else {
				drawThreeCenterTextLines(g2d, "PRESS s TO sTART", "c TO cONFIG", "OR fI FOR hELP");
			}
		} else if (gameModel.getCurrentProgramStatus().isGameEnding()
		        || gameModel.getCurrentProgramStatus().isGameEnd()) {
			drawGhosts(g2d);
			var levelText = "eND lEVEL "
			        + Utils.integerToRoman(gameModel.getCurrentProgramStatus().getNumLevel()).toLowerCase();

			drawTwoCenterTextLines(g2d, "gAME oVER", levelText);
		} else if (gameModel.getCurrentProgramStatus().isLevelEnding()) {
			drawTwoCenterTextLines(g2d, "nEXT LEVEL", "gET READY");
		} else if (gameModel.getLadybug().getStatus() == LadybugStatus.DYING) {
			ladybugDyingView.inProgress();
			drawGhosts(g2d);
			drawLadybug(g2d, ladybugDyingView);
		} else if (gameModel.getLadybug().getStatus() == LadybugStatus.DEAD) {
			ladybugDyingView.init();
		} else if (gameModel.getLadybug().getStatus() != LadybugStatus.DEAD) {
			drawLadybug(g2d, ladybugView);
			drawGhosts(g2d);
			drawScoresIncrement(g2d);
		} else {
			var msg = "NO DISPLAY FOR STATUS " + gameModel.getCurrentProgramStatus();
			drawOneCenterTextLine(g2d, msg);
			logger.error(msg);
		}
	}

	private void drawGhosts(Graphics2D g2d) {
		var ladybugPosition = gameModel.getLadybug().getPosition();
		gameModel.getGroupGhosts().getLstGhosts().stream()
		        .filter(g -> g.getPosition().distance(ladybugPosition) <= 5.5 * Constants.BLOCK_SIZE
		                || !gameModel.getCurrentProgramStatus().isInGame()
		                || !gameModel.getScreenData().isLitLampMode())
		        .forEach(g -> g2d.drawImage(ghostView.getImage(g), g.getPosition().x + 1, g.getPosition().y + 1, this));
	}

	private void drawLadybug(Graphics2D g2d, LadybugCommun ladybugCommon) {
		var viewDirection = gameModel.getLadybug().getViewDirection();
		var getPosition = gameModel.getLadybug().getPosition();
		var image = ladybugCommon.getImage(viewDirection);
		g2d.drawImage(image, getPosition.x + 1, getPosition.y + 1, this);
	}

	private void drawMaze(Graphics2D g2d) {
		for (var y = 0; y < gameModel.getScreenData().getScreenHeight(); y += Constants.BLOCK_SIZE) {
			for (var x = 0; x < gameModel.getScreenData().getCurrentLevel().getNbrBlocksByLine()
			        * Constants.BLOCK_SIZE; x += Constants.BLOCK_SIZE) {

				var ladybugPosition = gameModel.getLadybug().getPosition();
				var positionScreenBlock = new Point(x, y);

				if (((ladybugPosition.distance(positionScreenBlock) <= 3.5 * Constants.BLOCK_SIZE)
				        || !gameModel.getCurrentProgramStatus().isInGame())
				        || !gameModel.getScreenData().isLitLampMode()) {
					screenBlockView.display(g2d, gameModel.getScreenData(), x, y);
				}

			}
		}
	}

	private void drawOneCenterTextLine(Graphics2D g2d, String text) {
		var x = gameModel.getScreenData().getScreenWidth();
		var y = gameModel.getScreenData().getScreenHeight();
		var metr = getFontMetrics(defaultFont);
		g2d.setColor(Color.white);
		g2d.setFont(defaultFont);
		g2d.drawString(text, (x - metr.stringWidth(text)) / 2, y / 2 - Constants.BLOCK_SIZE);
	}

	/*
	 * FIXME : utiliser les streams comme dans drawGhosts(Graphics2D g2d)
	 */
	private void drawPresentationGhosts(Graphics2D g2d) {
		var x = gameModel.getScreenData().getScreenWidth() / 2 - (7 * Constants.BLOCK_SIZE) / 2;
		var y = gameModel.getScreenData().getScreenHeight() / 2;
		List<Ghost> ghosts = gameModel.getGroupGhosts().getLstGhosts().stream().collect(Collectors.toList());
		for (Ghost ghost : ghosts) {
			g2d.drawImage(ghostView.getImage(ghost), x, y, this);
			x += 2 * Constants.BLOCK_SIZE;
		}
	}

	private void drawScoresIncrement(Graphics2D g2d) {
		g2d.setColor(Color.WHITE);
		g2d.setFont(scoreFont);
		var metr = getFontMetrics(scoreFont);
		int x;
		int y;
		// Affichage des scores incréments
		for (Message message : gameModel.getGroupMessages().getMessages()) {
			x = message.getPosition().x + Constants.BLOCK_SIZE / 2 - metr.stringWidth(message.getValue()) / 2;
			y = message.getPosition().y + Constants.BLOCK_SIZE / 2;
			g2d.drawString(message.getValue() + message.getMessageType().getEndMessage(), x, y);
		}
	}

	private void drawSevenCenterTextLines(Graphics2D g2d, String line1, String line2, String line3, String line4,
	        String line5, String line6, String line7) {
		var x = gameModel.getScreenData().getScreenWidth();
		var y = gameModel.getScreenData().getScreenHeight();
		var metr = getFontMetrics(defaultFont);
		g2d.setColor(Color.white);
		g2d.setFont(defaultFont);
		g2d.drawString(line1, (x - metr.stringWidth(line1)) / 2, y / 2 - 6 * Constants.BLOCK_SIZE);
		g2d.drawString(line2, (x - metr.stringWidth(line2)) / 2, y / 2 - 4 * Constants.BLOCK_SIZE);
		g2d.drawString(line3, (x - metr.stringWidth(line3)) / 2, y / 2 - 2 * Constants.BLOCK_SIZE);
		g2d.drawString(line4, (x - metr.stringWidth(line4)) / 2, y / 2);
		g2d.drawString(line5, (x - metr.stringWidth(line5)) / 2, y / 2 + 2 * Constants.BLOCK_SIZE);
		g2d.drawString(line6, (x - metr.stringWidth(line6)) / 2, y / 2 + 4 * Constants.BLOCK_SIZE);
		g2d.drawString(line7, (x - metr.stringWidth(line7)) / 2, y / 2 + 6 * Constants.BLOCK_SIZE);
	}

	private void drawThreeCenterTextLines(Graphics2D g2d, String line1, String line2, String line3) {
		var x = gameModel.getScreenData().getScreenWidth();
		var y = gameModel.getScreenData().getScreenHeight();
		var metr = getFontMetrics(defaultFont);
		g2d.setColor(Color.white);
		g2d.setFont(defaultFont);
		g2d.drawString(line1, (x - metr.stringWidth(line1)) / 2, y / 2 - 2 * Constants.BLOCK_SIZE);
		g2d.drawString(line2, (x - metr.stringWidth(line2)) / 2, y / 2);
		g2d.drawString(line3, (x - metr.stringWidth(line3)) / 2, y / 2 + 2 * Constants.BLOCK_SIZE);
	}

	private void drawTwoCenterTextLines(Graphics2D g2d, String line1, String line2) {
		var x = gameModel.getScreenData().getScreenWidth();
		var y = gameModel.getScreenData().getScreenHeight();
		var metr = getFontMetrics(defaultFont);
		g2d.setColor(Color.white);
		g2d.setFont(defaultFont);
		g2d.drawString(line1, (x - metr.stringWidth(line1)) / 2, y / 2 - Constants.BLOCK_SIZE);
		g2d.drawString(line2, (x - metr.stringWidth(line2)) / 2, y / 2 + Constants.BLOCK_SIZE);
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
		this.gameModel = (GameModelForViews) gameModel;
		repaint();
	}
}
