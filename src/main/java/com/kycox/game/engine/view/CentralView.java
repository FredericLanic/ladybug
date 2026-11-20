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
package com.kycox.game.engine.view;

import com.kycox.game.tools.Position;
import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.constant.font.GameFont;
import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.constant.contract.DoActionAfterTimer;
import com.kycox.game.engine.controller.KeyboardController;
import com.kycox.game.engine.message.LadybugMessagesBundle;
import com.kycox.game.engine.model.EventGameModel;
import com.kycox.game.constant.game.GameStatus;
import com.kycox.game.tools.timer.WaitAndDoActionAfterTimer;
import com.kycox.game.tools.Utils;
import com.kycox.game.engine.view.ghost.GhostView;
import com.kycox.game.engine.view.ladybug.LadybugCommun;
import com.kycox.game.engine.view.ladybug.LadybugDyingView;
import com.kycox.game.engine.view.ladybug.LadybugView;
import com.kycox.game.engine.view.map.ScreenBlockView;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import com.kycox.game.launcher.GuiComponent;
import com.kycox.game.engine.view.map.MazeColors;
import com.kycox.game.preferences.LocalPreferences;
import com.kycox.game.snapshot.GameSnapshot;
import com.kycox.game.snapshot.GhostSnapshot;
import com.kycox.game.snapshot.ScoreMessageSnapshot;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

@GuiComponent
public class CentralView extends JPanel implements ApplicationListener<EventGameModel>, DoActionAfterTimer {
	private static final Logger logger = LoggerFactory.getLogger(CentralView.class);
	@Serial
	private static final long serialVersionUID = 1L;
	private final Font defaultFont = GameFont.PACFONT.getDefaultFont();
	private final Font scoreFont = new Font("CrackMan", Font.BOLD, 14);
	@Setter
	private long durationLadybugNewLife;
	/**
	 * La photo du dernier battement (4.6) : la vue ne voit jamais le modèle
	 * vivant. {@code null} avant le premier battement.
	 */
	private transient GameSnapshot snapshot;
	private final transient GhostView ghostView;
	private final transient KeyboardController keyboardController;
	private final transient LadybugDyingView ladybugDyingView;
	private final transient LadybugView ladybugView;
	private final transient ScreenBlockView screenBlockView;
	private final transient LadybugMessagesBundle ladybugMessagesBundle;
	/** Réglages de ce poste (aide, manette, debug), lus sans passer par le modèle (4.5). */
	private final transient LocalPreferences localPreferences;

	public CentralView(GhostView ghostView,
					   KeyboardController keyboardController,
					   LadybugDyingView ladybugDyingView,
					   LadybugView ladybugView,
					   ScreenBlockView screenBlockView,
					   LadybugMessagesBundle ladybugMessagesBundle,
					   LocalPreferences localPreferences)  {
		this.localPreferences = localPreferences;
		this.ghostView = ghostView;
		this.keyboardController = keyboardController;
		this.ladybugDyingView = ladybugDyingView;
		this.ladybugView = ladybugView;
		this.screenBlockView = screenBlockView;
		this.ladybugMessagesBundle = ladybugMessagesBundle;
	}

	@Override
	public void doActionAfterTimer(int nbrAction) {
		switch (nbrAction) {
			case 0 -> screenBlockView.setColorMaze(MazeColors.BLUE_LADYBUG);
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
		if (snapshot.ladybug().newLife()) {
			screenBlockView.setColorMaze(MazeColors.COLOR_EXTRA_PAC_LADYBUG);
			var newLiveTimer = new WaitAndDoActionAfterTimer();
			newLiveTimer.launch(durationLadybugNewLife, this, 0);
		}

		if (snapshot.is(GameStatus.PROGRAM_STARTING)) {
			drawOneCenterTextLine(g2d, ladybugMessagesBundle.getMessage("welcome"));
			drawPresentationGhosts(g2d);
		} else if (snapshot.is(GameStatus.ASK_FORCE_END_GAME)) {
			drawLadybug(g2d, ladybugView);
			drawGhosts(g2d);
			drawTwoCenterTextLines(g2d, ladybugMessagesBundle.getMessage("exitGame")
					, ladybugMessagesBundle.getMessage("yesNo"));
		} else if (snapshot.is(GameStatus.PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL)) {
			drawGhosts(g2d);
			drawThreeCenterTextLines(g2d, ladybugMessagesBundle.getMessage("continue")
					, ladybugMessagesBundle.getMessage("previousGame")
					, ladybugMessagesBundle.getMessage("yesNo"));
		} else if (snapshot.is(GameStatus.GAME_STARTING)) {
			drawOneCenterTextLine(g2d, ladybugMessagesBundle.getMessage("getReady"));
		} else if (snapshot.is(GameStatus.LEVEL_STARTING)) {
			drawLadybug(g2d, ladybugView);
			drawGhosts(g2d);
			var text = ladybugMessagesBundle.getMessage("level")
					+ " "
					+ Utils.integerToRoman(snapshot.numLevel()).toLowerCase();
			drawOneCenterTextLine(g2d, text);
		} else if (snapshot.is(GameStatus.PROGRAM_PRESENTATION)) {
			drawGhosts(g2d);
			if (localPreferences.isShowHelpForKeys()) {
				drawCenterTextLines(g2d, ladybugMessagesBundle.getMessage("help.keyboard.title")
						, ladybugMessagesBundle.getMessage("help.keyboard.sound")
						, ladybugMessagesBundle.getMessage("help.keyboard.ladybug.skin")
						, ladybugMessagesBundle.getMessage("help.keyboard.ghost.headband")
						, ladybugMessagesBundle.getMessage("help.keyboard.ghost.hat")
						, ladybugMessagesBundle.getMessage("help.keyboard.lamp")
						, ladybugMessagesBundle.getMessage("help.keyboard.camouflage")
						, ladybugMessagesBundle.getMessage("help.keyboard.ladybug.move")
						, ladybugMessagesBundle.getMessage("help.keyboard.ghost.move")
						, ladybugMessagesBundle.getMessage("help.keyboard.multi.player")
						, ladybugMessagesBundle.getMessage("help.keyboard.pause")
						, ladybugMessagesBundle.getMessage("help.keyboard.escape"));
			} else if (localPreferences.isShowHelpForXboxes()) {
				drawCenterTextLines(g2d, ladybugMessagesBundle.getMessage("help.xbox.title")
						, ladybugMessagesBundle.getMessage("help.xbox.sound")
						, ladybugMessagesBundle.getMessage("help.xbox.ladybug.skin")
						, ladybugMessagesBundle.getMessage("help.xbox.ghost.headband")
						, ladybugMessagesBundle.getMessage("help.xbox.ghost.hat")
						, ladybugMessagesBundle.getMessage("help.xbox.ladybug.move")
						, ladybugMessagesBundle.getMessage("help.xbox.ghost.move")
						, ladybugMessagesBundle.getMessage("help.xbox.multi.player"));
			} else if (localPreferences.isAtLeastOneXboxOneConnected()) {
				drawTwoCenterTextLines(g2d, ladybugMessagesBundle.getMessage("sOrAToStart")
						, ladybugMessagesBundle.getMessage("f1OrViewForHelp"));
			} else {
				drawTwoCenterTextLines(g2d, ladybugMessagesBundle.getMessage("sToStart")
						, ladybugMessagesBundle.getMessage("f1ForHelp"));
			}
		} else if (snapshot.is(GameStatus.GAME_ENDING)
		        || snapshot.is(GameStatus.GAME_END)) {
			drawGhosts(g2d);
			var levelText = ladybugMessagesBundle.getMessage("endLevel")
					+ " "
			        + Utils.integerToRoman(snapshot.numLevel()).toLowerCase();
			drawTwoCenterTextLines(g2d, ladybugMessagesBundle.getMessage("gameOver"), levelText);
		} else if (snapshot.is(GameStatus.LEVEL_ENDING)) {
			drawTwoCenterTextLines(g2d, ladybugMessagesBundle.getMessage("nextLevel")
					, ladybugMessagesBundle.getMessage("getReady"));
		} else if (snapshot.ladybug().status() == LadybugStatus.DYING) {
			ladybugDyingView.inProgress(snapshot.ladybug().agonyDuration());
			drawGhosts(g2d);
			drawLadybug(g2d, ladybugDyingView);
		} else if (snapshot.ladybug().status() == LadybugStatus.DEAD) {
			ladybugDyingView.init();
		} else if (snapshot.ladybug().status() != LadybugStatus.DEAD) {
			drawLadybug(g2d, ladybugView);
			drawGhosts(g2d);
			drawScoresIncrement(g2d);
		} else {
			var msg = "NO DISPLAY FOR STATUS " + snapshot.status();
			drawOneCenterTextLine(g2d, msg);
			logger.error(msg);
		}
	}

	/**
	 * Lignes centrées, réparties sur 13 cases de haut quel que soit leur nombre
	 * (3.5.3c). Remplace {@code drawEightCenterTextLines}, qui plaçait à la main
	 * exactement 8 lignes : l'aide du clavier en compte maintenant 12 (P, F6, F7
	 * et Échap ajoutés).
	 */
	private void drawCenterTextLines(Graphics2D g2d, String... lines) {
		var x = widthInPixels();
		var y = heightInPixels();
		var metr = getFontMetrics(defaultFont);
		g2d.setColor(Color.white);
		g2d.setFont(defaultFont);
		var top = y / 2.0 - 6.5 * GameMainConstants.BLOCK_SIZE;
		var gap = lines.length > 1 ? 13.0 * GameMainConstants.BLOCK_SIZE / (lines.length - 1) : 0;
		for (var i = 0; i < lines.length; i++) {
			g2d.drawString(lines[i], (x - metr.stringWidth(lines[i])) / 2, (int) (top + i * gap));
		}
	}

	/** Largeur du labyrinthe en pixels (étape 4.4 : calculée par la vue). */
	private int widthInPixels() {
		return snapshot.maze().nbrBlocksByLine() * GameMainConstants.BLOCK_SIZE;
	}

	/** Hauteur du labyrinthe en pixels (étape 4.4 : calculée par la vue). */
	private int heightInPixels() {
		return snapshot.maze().nbrLines() * GameMainConstants.BLOCK_SIZE;
	}

	private void drawGhosts(Graphics2D g2d) {
		var ladybugPosition = snapshot.ladybug().position();
		var ghosts = snapshot.ghosts();
		ghosts.stream()
		        .filter(ghost -> ghost.position().distance(ladybugPosition) <= 4 * GameMainConstants.BLOCK_SIZE || !ghost.computed()
		                || ghosts.stream().anyMatch(gc -> !gc.computed() && ghost.position().distance(gc.position()) <= 4 * GameMainConstants.BLOCK_SIZE)
		                || !snapshot.isInGame()
		                || !snapshot.litLampMode())
		        .forEach(g -> g2d.drawImage(ghostView.getImage(g), g.position().x() + 1, g.position().y() + 1, this));

		if (localPreferences.isDebugMode()) {
			g2d.setColor(Color.RED);
			ghosts.stream()
					.filter(ghost -> ghost.maximumAttackDist() > 0)
					.forEach(ghost -> g2d.drawOval(ghost.position().x() + 1 - ghost.maximumAttackDist() + GameMainConstants.BLOCK_SIZE / 2
							, ghost.position().y() + 1 - ghost.maximumAttackDist() + GameMainConstants.BLOCK_SIZE / 2
							, ghost.maximumAttackDist() * 2
							, ghost.maximumAttackDist() * 2));
		}
	}

	private void drawLadybug(Graphics2D g2d, LadybugCommun ladybugCommon) {
		var viewDirection = snapshot.ladybug().viewDirection();
		var getPosition = snapshot.ladybug().position();
		var image = ladybugCommon.getImage(viewDirection);
		g2d.drawImage(image, getPosition.x() + 1, getPosition.y() + 1, this);
	}

	private void drawMaze(Graphics2D g2d) {
		var ladybugPosition = snapshot.ladybug().position();
		for (var y = 0; y < heightInPixels(); y += GameMainConstants.BLOCK_SIZE) {
			for (var x = 0; x < snapshot.maze().nbrBlocksByLine()
			        * GameMainConstants.BLOCK_SIZE; x += GameMainConstants.BLOCK_SIZE) {
				var positionScreenBlock = new Position(x, y);

				if ((ladybugPosition.distance(positionScreenBlock) <= 3.5 * GameMainConstants.BLOCK_SIZE)
				        || snapshot.ghosts().stream().filter(g -> !g.computed()).anyMatch(
				                g -> g.position().distance(positionScreenBlock) <= 3.5 * GameMainConstants.BLOCK_SIZE)
				        || !snapshot.isInGame()
				        || !snapshot.litLampMode()) {
					screenBlockView.display(g2d, snapshot.maze(), snapshot.levelRendering(), x, y);
				}

			}
		}
	}

	private void drawOneCenterTextLine(Graphics2D g2d, String text) {
		var x = widthInPixels();
		var y = heightInPixels();
		var metr = getFontMetrics(defaultFont);
		g2d.setColor(Color.white);
		g2d.setFont(defaultFont);
		g2d.drawString(text, (x - metr.stringWidth(text)) / 2, y / 2 - GameMainConstants.BLOCK_SIZE);
	}

	/*
	 * FIXME : utiliser les streams comme dans drawGhosts(Graphics2D g2d)
	 */
	private void drawPresentationGhosts(Graphics2D g2d) {
		var x = widthInPixels() / 2 - (7 * GameMainConstants.BLOCK_SIZE) / 2;
		var y = heightInPixels() / 2;
		for (GhostSnapshot ghost : snapshot.ghosts()) {
			g2d.drawImage(ghostView.getImage(ghost), x, y, this);
			x += 2 * GameMainConstants.BLOCK_SIZE;
		}
	}

	private void drawScoresIncrement(Graphics2D g2d) {
		g2d.setColor(Color.WHITE);
		g2d.setFont(scoreFont);
		var metr = getFontMetrics(scoreFont);
		// Affichage des scores incréments
		for (ScoreMessageSnapshot message : snapshot.scoreMessages()) {
			var x = message.position().x() + GameMainConstants.BLOCK_SIZE / 2 - metr.stringWidth(message.value()) / 2;
			var y = message.position().y() + GameMainConstants.BLOCK_SIZE / 2;
			g2d.drawString(message.value() + message.messageType().getEndMessage(), x, y);
		}
	}

	private void drawSevenCenterTextLines(Graphics2D g2d, String line1, String line2, String line3, String line4,
	        String line5, String line6, String line7) {
		var x = widthInPixels();
		var y = heightInPixels();
		var metr = getFontMetrics(defaultFont);
		g2d.setColor(Color.white);
		g2d.setFont(defaultFont);
		g2d.drawString(line1, (x - metr.stringWidth(line1)) / 2, y / 2 - 6 * GameMainConstants.BLOCK_SIZE);
		g2d.drawString(line2, (x - metr.stringWidth(line2)) / 2, y / 2 - 4 * GameMainConstants.BLOCK_SIZE);
		g2d.drawString(line3, (x - metr.stringWidth(line3)) / 2, y / 2 - 2 * GameMainConstants.BLOCK_SIZE);
		g2d.drawString(line4, (x - metr.stringWidth(line4)) / 2, y / 2);
		g2d.drawString(line5, (x - metr.stringWidth(line5)) / 2, y / 2 + 2 * GameMainConstants.BLOCK_SIZE);
		g2d.drawString(line6, (x - metr.stringWidth(line6)) / 2, y / 2 + 4 * GameMainConstants.BLOCK_SIZE);
		g2d.drawString(line7, (x - metr.stringWidth(line7)) / 2, y / 2 + 6 * GameMainConstants.BLOCK_SIZE);
	}

	private void drawThreeCenterTextLines(Graphics2D g2d, String line1, String line2, String line3) {
		var x = widthInPixels();
		var y = heightInPixels();
		var metr = getFontMetrics(defaultFont);
		g2d.setColor(Color.white);
		g2d.setFont(defaultFont);
		g2d.drawString(line1, (x - metr.stringWidth(line1)) / 2, y / 2 - 2 * GameMainConstants.BLOCK_SIZE);
		g2d.drawString(line2, (x - metr.stringWidth(line2)) / 2, y / 2);
		g2d.drawString(line3, (x - metr.stringWidth(line3)) / 2, y / 2 + 2 * GameMainConstants.BLOCK_SIZE);
	}

	private void drawTwoCenterTextLines(Graphics2D g2d, String line1, String line2) {
		var x = widthInPixels();
		var y = heightInPixels();
		var metr = getFontMetrics(defaultFont);
		g2d.setColor(Color.white);
		g2d.setFont(defaultFont);
		g2d.drawString(line1, (x - metr.stringWidth(line1)) / 2, y / 2 - GameMainConstants.BLOCK_SIZE);
		g2d.drawString(line2, (x - metr.stringWidth(line2)) / 2, y / 2 + GameMainConstants.BLOCK_SIZE);
	}

	@PostConstruct
	public void init() {
		setFocusable(true);
		setBackground(Color.black);
		addKeyListener(keyboardController); // key listener pour les touches
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// avant le premier battement, le modèle n'est pas encore connu : fond noir seul.
		// Une exception ici interromprait tout le dessin de la fenêtre, et les zones
		// qui ne se redessinent jamais (haut, droite) resteraient blanches (4.1)
		if (snapshot != null) {
			draw(g);
		}
	}

	@Override
	public void onApplicationEvent(EventGameModel event) {
		snapshot = event.getSnapshot();
		repaint();
	}


}
