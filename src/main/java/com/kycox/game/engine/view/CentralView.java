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
import com.kycox.game.engine.controller.KeyboardController;
import com.kycox.game.engine.message.LadybugMessagesBundle;
import com.kycox.game.engine.model.EventGameModel;
import com.kycox.game.constant.game.GameStatus;
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
import com.kycox.game.engine.view.ghost.GhostPalette;
import com.kycox.game.constant.ghost.GhostsBodyImages;
import java.util.List;
import com.kycox.game.preferences.LocalPreferences;
import com.kycox.game.snapshot.GameSnapshot;
import com.kycox.game.snapshot.GhostSnapshot;
import com.kycox.game.snapshot.ScoreMessageSnapshot;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

@GuiComponent
public class CentralView extends JPanel implements ApplicationListener<EventGameModel> {
	private static final Logger logger = LoggerFactory.getLogger(CentralView.class);
	@Serial
	private static final long serialVersionUID = 1L;
	private final Font defaultFont = GameFont.PACFONT.getDefaultFont();
	private final Font scoreFont = new Font("CrackMan", Font.BOLD, 14);
	/** Durée de l'effet « vie gagnée » (celle du son), en millisecondes. */
	@Setter
	private long durationLadybugNewLife;
	/**
	 * Battements restants de l'effet « vie gagnée » (murs dorés qui clignotent,
	 * texte « +1 VIE »). Décompté à la réception de chaque photo, et non au
	 * dessin : le signal « vie gagnée » ne dure qu'un battement et pouvait tomber
	 * entre deux dessins (D25).
	 */
	private long newLifeBeatsLeft;
	/** Durée totale de l'effet en cours, en battements. */
	private long newLifeTotalBeats;
	/** Les murs restent dorés 4 battements, puis normaux 4 battements, etc. */
	private static final int BLINK_BEATS = 4;
	/** Trait pointillé entre un fantôme et sa destination (mode debug). */
	private static final Stroke TARGET_LINE = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0,
	        new float[] { 6, 6 }, 0);
	/** Opacité du petit fantôme posé sur la destination (mode debug). */
	private static final float TARGET_GHOST_OPACITY = 0.75f;
	/** Le texte « +1 VIE » monte de ce nombre de pixels par battement. */
	private static final int NEW_LIFE_TEXT_RISE = 1;
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

	/**
	 * FIXME : même remarque que dans le modèle du jeu; appliquer un design pattern
	 * strategy; ça devient un peu dificile à lire
	 */
	private void draw(Graphics g) {
		var g2d = (Graphics2D) g;
		drawMaze(g2d);

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
				// convention Xbox (20.1) : A = oui, B = non ; Menu (≡) et Affichage (⧉) dessinés
				drawCenterTextLines(g2d, ladybugMessagesBundle.getMessage("help.xbox.title")
						, ladybugMessagesBundle.getMessage("help.xbox.yes")
						, ladybugMessagesBundle.getMessage("help.xbox.no")
						, ladybugMessagesBundle.getMessage("help.xbox.multi.player")
						, ladybugMessagesBundle.getMessage("help.xbox.pause")
						, ladybugMessagesBundle.getMessage("help.xbox.escape")
						, ladybugMessagesBundle.getMessage("help.xbox.sound")
						, ladybugMessagesBundle.getMessage("help.xbox.ladybug.skin")
						, ladybugMessagesBundle.getMessage("help.xbox.ghost.headband")
						, ladybugMessagesBundle.getMessage("help.xbox.ghost.hat")
						, ladybugMessagesBundle.getMessage("help.xbox.ladybug.move")
						, ladybugMessagesBundle.getMessage("help.xbox.ghost.move"));
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
			drawNewLifeText(g2d);
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
			PadIcons.draw(g2d, lines[i], (x - PadIcons.width(lines[i], metr)) / 2, (int) (top + i * gap), metr);
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
			drawGhostTargets(g2d, ghosts);
		}
	}

	/**
	 * Mode debug : pour chaque fantôme qui vise une case, un trait pointillé de
	 * sa couleur jusqu'à cette case, et un petit fantôme de sa couleur posé dessus.
	 */
	void drawGhostTargets(Graphics2D g2d, List<GhostSnapshot> ghosts) {
		var previousStroke = g2d.getStroke();
		var previousComposite = g2d.getComposite();
		var half = GameMainConstants.BLOCK_SIZE / 2;
		var miniSize = GameMainConstants.BLOCK_SIZE / 2;
		for (var ghost : ghosts) {
			ghost.targetBlock().ifPresent(block -> {
				var target = Utils.convertBlockPointToGraphicPoint(block);
				var targetX = target.x() + half;
				var targetY = target.y() + half;
				g2d.setComposite(previousComposite);
				g2d.setColor(GhostPalette.of(ghost.color()));
				g2d.setStroke(TARGET_LINE);
				g2d.drawLine(ghost.position().x() + 1 + half, ghost.position().y() + 1 + half, targetX, targetY);
				g2d.setComposite(AlphaComposite.SrcOver.derive(TARGET_GHOST_OPACITY));
				g2d.drawImage(GhostsBodyImages.of(ghost.color()).getImage(), targetX - miniSize / 2,
				        targetY - miniSize / 2, miniSize, miniSize, this);
			});
		}
		g2d.setStroke(previousStroke);
		g2d.setComposite(previousComposite);
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
					screenBlockView.display(g2d, snapshot.maze(), snapshot.levelRendering(), x, y, isWallHighlightOn());
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
		PadIcons.draw(g2d, line1, (x - PadIcons.width(line1, metr)) / 2, y / 2 - GameMainConstants.BLOCK_SIZE, metr);
		PadIcons.draw(g2d, line2, (x - PadIcons.width(line2, metr)) / 2, y / 2 + GameMainConstants.BLOCK_SIZE, metr);
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

	/**
	 * Une vie gagnée lance l'effet pour la durée du son ; sinon, un battement de
	 * moins (un battement = {@link GameMainConstants#PACE} ms).
	 */
	private void updateNewLifeEffect() {
		if (snapshot.ladybug().newLife()) {
			newLifeTotalBeats = Math.max(1, (durationLadybugNewLife + GameMainConstants.PACE - 1) / GameMainConstants.PACE);
			newLifeBeatsLeft = newLifeTotalBeats;
		} else if (newLifeBeatsLeft > 0) {
			newLifeBeatsLeft--;
		}
	}

	/** Vrai pendant toute la durée de l'effet « vie gagnée ». */
	boolean isNewLifeEffectOn() {
		return newLifeBeatsLeft > 0;
	}

	/** Battements écoulés depuis la vie gagnée. */
	private long newLifeElapsedBeats() {
		return newLifeTotalBeats - newLifeBeatsLeft;
	}

	/** Vrai pendant les phases allumées du clignotement : murs dorés. */
	boolean isWallHighlightOn() {
		return isNewLifeEffectOn() && (newLifeElapsedBeats() / BLINK_BEATS) % 2 == 0;
	}

	/**
	 * Texte doré « +1 VIE » au-dessus de Ladybug, qui monte et s'efface pendant
	 * l'effet, comme les messages de score (D25).
	 */
	private void drawNewLifeText(Graphics2D g2d) {
		if (!isNewLifeEffectOn()) {
			return;
		}
		var text = ladybugMessagesBundle.getMessage("newLife");
		var opacity = (float) newLifeBeatsLeft / newLifeTotalBeats;
		var position = snapshot.ladybug().position();
		var metrics = getFontMetrics(scoreFont);
		var x = position.x() + GameMainConstants.BLOCK_SIZE / 2 - metrics.stringWidth(text) / 2;
		var y = position.y() - (int) (newLifeElapsedBeats() * NEW_LIFE_TEXT_RISE);
		var previous = g2d.getComposite();
		g2d.setComposite(AlphaComposite.SrcOver.derive(opacity));
		g2d.setFont(scoreFont);
		g2d.setColor(MazeColors.NEW_LIFE_GOLD);
		g2d.drawString(text, x, y);
		g2d.setComposite(previous);
	}

	@Override
	public void onApplicationEvent(EventGameModel event) {
		snapshot = event.getSnapshot();
		updateNewLifeEffect();
		repaint();
	}


}
