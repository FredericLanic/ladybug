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

import com.kycox.game.body.ghost.Ghost;
import com.kycox.game.constant.Constants;
import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.contract.DoActionAfterTimer;
import com.kycox.game.contract.GameModelForViews;
import com.kycox.game.controller.key.KeyGameController;
import com.kycox.game.font.GameFont;
import com.kycox.game.score.Message;
import com.kycox.game.timer.WaitAndDoActionAfterTimer;
import com.kycox.game.tools.Utils;
import com.kycox.game.view.ghost.GhostView;
import com.kycox.game.view.ladybug.LadybugCommun;
import com.kycox.game.view.ladybug.LadybugDyingView;
import com.kycox.game.view.ladybug.LadybugView;
import com.kycox.game.view.map.ScreenBlockView;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;
import java.util.Observable;
import java.util.Observer;

@Component
public class CentralView extends JPanel implements Observer, DoActionAfterTimer {
	private static final Log logger = LogFactory.getLog(CentralView.class);
	@Serial
	private static final long serialVersionUID = 1L;
	private final Font defaultFont = GameFont.PACFONT.getDefaultFont();
	private final Font scoreFont = new Font("CrackMan", Font.BOLD, 14);
	@Setter
	private long durationLadybugNewLife;
	private transient GameModelForViews gameModelForViews;
	private final transient GhostView ghostView;
	private final transient KeyGameController keyGameController;
	private final transient LadybugDyingView ladybugDyingView;
	private final transient LadybugView ladybugView;
	private final transient ScreenBlockView screenBlockView;

	public CentralView(GhostView ghostView, KeyGameController keyGameController, LadybugDyingView ladybugDyingView, LadybugView ladybugView, ScreenBlockView screenBlockView)  {
		this.ghostView = ghostView;
		this.keyGameController = keyGameController;
		this.ladybugDyingView = ladybugDyingView;
		this.ladybugView = ladybugView;
		this.screenBlockView = screenBlockView;
	}

	@Override
	public void doActionAfterTimer(int nbrAction) {
		switch (nbrAction) {
			case 0 -> screenBlockView.setColorMaze(Constants.BLUE_LADYBUG);
			default -> logger.debug("no number " + nbrAction + " action");
		}
	}

	/**
	 * FIXME : même remarque que dans le modèle du jeu; appliquer un design pattern
	 * strategy; ça devient un peu dificile à lire
	 */
	private void draw(Graphics g) {
		var g2d = (Graphics2D) g;
		drawMaze(g2d);
		if (gameModelForViews.getLadybug().isNewLife()) {
			screenBlockView.setColorMaze(Constants.COLOR_EXTRA_PAC_LADYBUG);
			var newLiveTimer = new WaitAndDoActionAfterTimer();
			newLiveTimer.launch(durationLadybugNewLife, this, 0);
		}

		if (gameModelForViews.getCurrentProgramStatus().isProgramStarting()) {
			drawOneCenterTextLine(g2d, "wELCOME TO lADYBUG");
			drawPresentationGhosts(g2d);
		} else if (gameModelForViews.getCurrentProgramStatus().isGameAskForceEndGame()) {
			drawLadybug(g2d, ladybugView);
			drawGhosts(g2d);
			drawTwoCenterTextLines(g2d, "eXIT gAME", "yES - nO");
		} else if (gameModelForViews.getCurrentProgramStatus().isProgramAskKeepPreviousGameLevel()) {
			drawGhosts(g2d);
			drawThreeCenterTextLines(g2d, "cONTINUE", "pREVIOUS GAME", "yES - nO");
		} else if (gameModelForViews.getCurrentProgramStatus().isGameStarting()) {
			drawOneCenterTextLine(g2d, "gET READY");
		} else if (gameModelForViews.getCurrentProgramStatus().isLevelStarting()) {
			drawLadybug(g2d, ladybugView);
			drawGhosts(g2d);
			var text = "lEVEL "
			        + Utils.integerToRoman(gameModelForViews.getCurrentProgramStatus().getNumLevel()).toLowerCase();
			drawOneCenterTextLine(g2d, text);
		} else if (gameModelForViews.getCurrentProgramStatus().isProgramPresentation()) {
			drawGhosts(g2d);
			if (gameModelForViews.isShowHelpForKeys()) {
				drawEightCenterTextLines(g2d, "hELP", "FII: sOUND ON OFF", "FIII: lADYBUG sKIN", "FIV: gHOST hEADBAND",
				        "FV: gHOST hAT", "ARROWS: lADYBUG mOVE", "ZQSD: gHOST mOVE", "i OR ii: pLAYERS");
			} else if (gameModelForViews.isShowHelpForXboxes()) {
				drawEightCenterTextLines(g2d, "hELP", "x UP: sOUND ON OFF", "x DOWN: lADYBUG sKIN",
				        "x RIGHT: gHOST h'BAND", "x LEFT: gHOST hAT", "STICK i: lADYBUG mOVE", "STICK ii: gHOST mOVE",
				        "x OR y: pLAYERS");
			} else if (gameModelForViews.isAtLeastOneXboxOneConnected()) {
				drawTwoCenterTextLines(g2d, "PRESS s OR a TO sTART", "fI OR vIEW FOR hELP");
			} else {
				drawTwoCenterTextLines(g2d, "PRESS s TO sTART", "fI FOR hELP");
			}
		} else if (gameModelForViews.getCurrentProgramStatus().isGameEnding()
		        || gameModelForViews.getCurrentProgramStatus().isGameEnd()) {
			drawGhosts(g2d);
			var levelText = "eND lEVEL "
			        + Utils.integerToRoman(gameModelForViews.getCurrentProgramStatus().getNumLevel()).toLowerCase();

			drawTwoCenterTextLines(g2d, "gAME oVER", levelText);
		} else if (gameModelForViews.getCurrentProgramStatus().isLevelEnding()) {
			drawTwoCenterTextLines(g2d, "nEXT LEVEL", "gET READY");
		} else if (gameModelForViews.getLadybug().getStatus() == LadybugStatus.DYING) {
			ladybugDyingView.inProgress();
			drawGhosts(g2d);
			drawLadybug(g2d, ladybugDyingView);
		} else if (gameModelForViews.getLadybug().getStatus() == LadybugStatus.DEAD) {
			ladybugDyingView.init();
		} else if (gameModelForViews.getLadybug().getStatus() != LadybugStatus.DEAD) {
			drawLadybug(g2d, ladybugView);
			drawGhosts(g2d);
			drawScoresIncrement(g2d);
		} else {
			var msg = "NO DISPLAY FOR STATUS " + gameModelForViews.getCurrentProgramStatus();
			drawOneCenterTextLine(g2d, msg);
			logger.error(msg);
		}
	}

	private void drawEightCenterTextLines(Graphics2D g2d, String line1, String line2, String line3, String line4,
	        String line5, String line6, String line7, String line8) {
		var x = gameModelForViews.getScreenData().getScreenWidth();
		var y = gameModelForViews.getScreenData().getScreenHeight();
		var metr = getFontMetrics(defaultFont);
		g2d.setColor(Color.white);
		g2d.setFont(defaultFont);
		g2d.drawString(line1, (x - metr.stringWidth(line1)) / 2, y / 2 - (int) (6.5 * Constants.BLOCK_SIZE));
		g2d.drawString(line2, (x - metr.stringWidth(line2)) / 2, y / 2 - (int) (4.65 * Constants.BLOCK_SIZE));
		g2d.drawString(line3, (x - metr.stringWidth(line3)) / 2, y / 2 - (int) (2.78 * Constants.BLOCK_SIZE));
		g2d.drawString(line4, (x - metr.stringWidth(line4)) / 2, y / 2 - (int) (0.93 * Constants.BLOCK_SIZE));
		g2d.drawString(line5, (x - metr.stringWidth(line5)) / 2, y / 2 + (int) (0.93 * Constants.BLOCK_SIZE));
		g2d.drawString(line6, (x - metr.stringWidth(line6)) / 2, y / 2 + (int) (2.78 * Constants.BLOCK_SIZE));
		g2d.drawString(line7, (x - metr.stringWidth(line7)) / 2, y / 2 + (int) (4.65 * Constants.BLOCK_SIZE));
		g2d.drawString(line8, (x - metr.stringWidth(line8)) / 2, y / 2 + (int) (6.5 * Constants.BLOCK_SIZE));
	}

	private void drawGhosts(Graphics2D g2d) {
		var ladybugPosition = gameModelForViews.getLadybug().getPosition();
		gameModelForViews.getGroupGhosts()
				.getGhosts()
				.stream()
		        .filter(ghost -> ghost.getPosition().distance(ladybugPosition) <= 4 * Constants.BLOCK_SIZE || !ghost.isComputed()
		                || gameModelForViews.getGroupGhosts().getGhosts().stream().anyMatch(gc -> !gc.isComputed() && ghost.getPosition().distance(gc.getPosition()) <= 4 * Constants.BLOCK_SIZE)
		                || !gameModelForViews.getCurrentProgramStatus().isInGame()
		                || !gameModelForViews.getScreenData().isLitLampMode())
		        .forEach(g -> g2d.drawImage(ghostView.getImage(g), g.getPosition().x + 1, g.getPosition().y + 1, this));

		if (gameModelForViews.isDebugMode()) {
			g2d.setColor(Color.RED);
			gameModelForViews.getGroupGhosts()
					.getGhosts()
					.stream()
					.filter(ghost -> ghost.getMaximumAttackDist() > 0)
					.forEach(ghost -> g2d.drawOval(ghost.getPosition().x + 1 - ghost.getMaximumAttackDist() + Constants.BLOCK_SIZE / 2
							, ghost.getPosition().y + 1 - ghost.getMaximumAttackDist() + Constants.BLOCK_SIZE / 2
							, ghost.getMaximumAttackDist() * 2
							, ghost.getMaximumAttackDist() * 2));
		}
	}

	private void drawLadybug(Graphics2D g2d, LadybugCommun ladybugCommon) {
		var viewDirection = gameModelForViews.getLadybug().getViewDirection();
		var getPosition = gameModelForViews.getLadybug().getPosition();
		var image = ladybugCommon.getImage(viewDirection);
		g2d.drawImage(image, getPosition.x + 1, getPosition.y + 1, this);
	}

	private void drawMaze(Graphics2D g2d) {
		var ladybugPosition = gameModelForViews.getLadybug().getPosition();
		for (var y = 0; y < gameModelForViews.getScreenData().getScreenHeight(); y += Constants.BLOCK_SIZE) {
			for (var x = 0; x < gameModelForViews.getScreenData().getCurrentLevel().getNbrBlocksByLine()
			        * Constants.BLOCK_SIZE; x += Constants.BLOCK_SIZE) {
				var positionScreenBlock = new Point(x, y);

				if ((ladybugPosition.distance(positionScreenBlock) <= 3.5 * Constants.BLOCK_SIZE)
				        || gameModelForViews.getGroupGhosts().getGhosts().stream().filter(g -> !g.isComputed()).anyMatch(
				                g -> g.getPosition().distance(positionScreenBlock) <= 3.5 * Constants.BLOCK_SIZE)
				        || !gameModelForViews.getCurrentProgramStatus().isInGame()
				        || !gameModelForViews.getScreenData().isLitLampMode()) {
					screenBlockView.display(g2d, gameModelForViews.getScreenData(), x, y);
				}

			}
		}
	}

	private void drawOneCenterTextLine(Graphics2D g2d, String text) {
		var x = gameModelForViews.getScreenData().getScreenWidth();
		var y = gameModelForViews.getScreenData().getScreenHeight();
		var metr = getFontMetrics(defaultFont);
		g2d.setColor(Color.white);
		g2d.setFont(defaultFont);
		g2d.drawString(text, (x - metr.stringWidth(text)) / 2, y / 2 - Constants.BLOCK_SIZE);
	}

	/*
	 * FIXME : utiliser les streams comme dans drawGhosts(Graphics2D g2d)
	 */
	private void drawPresentationGhosts(Graphics2D g2d) {
		var x = gameModelForViews.getScreenData().getScreenWidth() / 2 - (7 * Constants.BLOCK_SIZE) / 2;
		var y = gameModelForViews.getScreenData().getScreenHeight() / 2;
		var ghosts = gameModelForViews.getGroupGhosts().getGhosts().stream().toList();
		for (Ghost ghost : ghosts) {
			g2d.drawImage(ghostView.getImage(ghost), x, y, this);
			x += 2 * Constants.BLOCK_SIZE;
		}
	}

	private void drawScoresIncrement(Graphics2D g2d) {
		g2d.setColor(Color.WHITE);
		g2d.setFont(scoreFont);
		var metr = getFontMetrics(scoreFont);
		// Affichage des scores incréments
		for (Message message : gameModelForViews.getGroupMessages().getMessages()) {
			var x = message.getPosition().x + Constants.BLOCK_SIZE / 2 - metr.stringWidth(message.getValue()) / 2;
			var y = message.getPosition().y + Constants.BLOCK_SIZE / 2;
			g2d.drawString(message.getValue() + message.getMessageType().getEndMessage(), x, y);
		}
	}

	private void drawSevenCenterTextLines(Graphics2D g2d, String line1, String line2, String line3, String line4,
	        String line5, String line6, String line7) {
		var x = gameModelForViews.getScreenData().getScreenWidth();
		var y = gameModelForViews.getScreenData().getScreenHeight();
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
		var x = gameModelForViews.getScreenData().getScreenWidth();
		var y = gameModelForViews.getScreenData().getScreenHeight();
		var metr = getFontMetrics(defaultFont);
		g2d.setColor(Color.white);
		g2d.setFont(defaultFont);
		g2d.drawString(line1, (x - metr.stringWidth(line1)) / 2, y / 2 - 2 * Constants.BLOCK_SIZE);
		g2d.drawString(line2, (x - metr.stringWidth(line2)) / 2, y / 2);
		g2d.drawString(line3, (x - metr.stringWidth(line3)) / 2, y / 2 + 2 * Constants.BLOCK_SIZE);
	}

	private void drawTwoCenterTextLines(Graphics2D g2d, String line1, String line2) {
		var x = gameModelForViews.getScreenData().getScreenWidth();
		var y = gameModelForViews.getScreenData().getScreenHeight();
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
		addKeyListener(keyGameController); // key listener pour les touches
	}

	@Override
	public void paintComponent(Graphics g) {
		if (gameModelForViews != null) {
			super.paintComponent(g);
			draw(g);
		}
	}

	@Override
	public void update(Observable gameModel, Object used) {
		gameModelForViews = (GameModelForViews) gameModel;
		repaint();
	}
}
