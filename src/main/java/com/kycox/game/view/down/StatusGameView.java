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
package com.kycox.game.view.down;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.swing.JPanel;

import com.kycox.game.tools.ImageUtils;

import lombok.Setter;

@Named("StatusGameView")
public class StatusGameView extends JPanel {
	private static final long serialVersionUID = 4546077700634533519L;
	@Setter
	private int ghostNbrLifes;
	@Setter
	private transient Image imageGhostPlayer;
	@Setter
	private transient Image imageLadybugPlayer;
	@Setter
	private int incrementScore;
	@Setter
	private boolean inGame;
	@Setter
	private int ladybugNbrLifes;
	@Setter
	private int nbrPlayers;
	@Setter
	private int nbrPointsForNewLife;
	@Setter
	private int numLevel;
	@Setter
	private int score;
	private final Font smallFont = new Font("CrackMan", Font.BOLD, 14);
	@Setter
	private boolean soundActive;

	private void display(Graphics g) {
		var delta = 2;
		var imageBorderSize = (getHeight() - 6 * delta) / 2;
		// number ladybug lifes
		displayNbrLifes(g, ImageUtils.resizeImage(imageLadybugPlayer, imageBorderSize, imageBorderSize),
		        ladybugNbrLifes, delta, 0);
		// number ghost lifes
		if (nbrPlayers > 1) {
			var ghost = ImageUtils.resizeImage(imageGhostPlayer, imageBorderSize, imageBorderSize);
			displayNbrLifes(g, ghost, ghostNbrLifes, delta + ghost.getWidth(null) + 30, 0);

		}
		// text
		displayMessageForPlayer(g);
	}

	private void displayMessageDuringGame(Graphics g) {
		var scoreMessage = new StringBuilder();
		scoreMessage.append("Score: ");
		scoreMessage.append(score);
		displayMessageToRight(g, scoreMessage.toString());
	}

	private void displayMessageForPlayer(Graphics g) {
		if (inGame) {
			displayMessageDuringGame(g);
		}
	}

	private void displayMessageToRight(Graphics g, String message) {
		g.setFont(smallFont);
		g.setColor(new Color(96, 128, 255));
		var coordX = getWidth() - 100;
		var coordY = getHeight() / 3;
		g.drawString(message, coordX, coordY);

		if (!soundActive) {
			var coordY1 = coordY + smallFont.getSize();
			g.drawString("Sound off", coordX, coordY1);
		}
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
