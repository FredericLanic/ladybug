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
package com.kycox.game.engine.view.down;

import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.tools.ImageUtils;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import com.kycox.game.launcher.GuiComponent;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

@GuiComponent
public class StatusGameView extends JPanel {
	@Serial
	private static final long serialVersionUID = 4546077700634533519L;
	@Setter
	private String currentProgramMessage;
	@Setter
	private int ghostNbrLifes;
	@Setter
	private transient Image imageGhostPlayer;
	@Setter
	private transient Image imageLadybugPlayer;
	@Setter
	private boolean inGame;
	@Setter
	private int ladybugNbrLifes;
	@Setter
	private int nbrPlayers;
	@Setter
	private int score;
	private final Font smallFont = new Font("CrackMan", Font.BOLD, 14);

	/**
	 * Taille maximale du dessin de Ladybug (et du fantôme joueur) à côté des vies :
	 * celle d'une case, comme dans le labyrinthe (étape 2.8, demande de Frédéric).
	 */
	static final int MAX_IMAGE_SIZE = GameMainConstants.BLOCK_SIZE;

	/**
	 * Taille du dessin : toute la hauteur de la bande, au lieu de la moitié
	 * (avant 2.8 : 18 px dans une zone de 96), sans dépasser
	 * {@link #MAX_IMAGE_SIZE}.
	 */
	static int imageSize(int bandHeight, int delta) {
		return Math.max(1, Math.min(bandHeight - 2 * delta, MAX_IMAGE_SIZE));
	}

	void display(Graphics g) {
		var delta = 2;
		var imageBorderSize = imageSize(getHeight(), delta);
		// number ladybug lifes
		displayNbrLifes(g, ImageUtils.resizeImage(imageLadybugPlayer, imageBorderSize, imageBorderSize),
		        ladybugNbrLifes, delta, 0);
		// number ghost lifes
		if (nbrPlayers > 1) {
			var ghost = ImageUtils.resizeImage(imageGhostPlayer, imageBorderSize, imageBorderSize);
			displayNbrLifes(g, ghost, ghostNbrLifes, delta + ghost.getWidth(null) + 30, 0);

		}
		if (inGame) {
			displayCentralMessage(g, "Score: " + score);
		}
		 displayMessageToRight(g, currentProgramMessage);
	}

	private void displayCentralMessage(Graphics g, String gameMessage) {
		if (gameMessage != null) {
			g.setFont(smallFont);
			g.setColor(Color.WHITE);
			var metr = getFontMetrics(smallFont);
			var coordX = (getWidth() - metr.stringWidth(gameMessage)) / 2;
			var coordY = getHeight() / 4;
			g.drawString(gameMessage, coordX, coordY);
		}
	}

	private void displayMessageToRight(Graphics g, String message) {
		// rien à afficher avant le premier battement du jeu : depuis l'étape 4.1, la
		// fenêtre est dessinée une première fois avant que la boucle ne démarre
		if (message == null) {
			return;
		}
		g.setFont(smallFont);
		g.setColor(Color.WHITE);
		var metr = getFontMetrics(smallFont);
		var coordX = getWidth() - metr.stringWidth(message) - 5;
		var coordY = 10;
		g.drawString(message, coordX, coordY);
	}

	private void displayNbrLifes(Graphics g, Image imageBody, int nbrLifes, int x, int y) {
		g.drawImage(imageBody, x, y, this);
		g.setColor(Color.YELLOW);
		g.setFont(smallFont);
		var coordX = x + imageBody.getWidth(null);
		var coordY = y + imageBody.getHeight(null) / 2 + smallFont.getSize() / 2;
		g.drawString("x " + nbrLifes, coordX, coordY);
	}

	@PostConstruct
	public void init() {
		setBackground(Color.BLACK);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		display(g);
	}
}
