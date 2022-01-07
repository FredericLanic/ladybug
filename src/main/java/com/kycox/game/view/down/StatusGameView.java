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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kycox.game.tools.ImageUtils;

import lombok.Setter;

@Named("StatusGameView")
public class StatusGameView extends JPanel {
	private static final Log logger = LogFactory.getLog(StatusGameView.class);
	private static final long serialVersionUID = 4546077700634533519L;
	@Setter
	private int ghostNbrLifes;
	@Setter
	private Image imageGhostPlayer;
	@Setter
	private Image imageLadybugPlayer;
	@Setter
	private int incrementScore;
	@Setter
	private boolean isInGame;
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

	private void display(Graphics g) {
		var x = 0;
		var y = 0;
		var delta = 2;
		var imageBorderSize = (getHeight() - 6 * delta) / 2;
		// number ladybug lifes
		displayNbrLifes(g, ImageUtils.resizeImage(imageLadybugPlayer, imageBorderSize, imageBorderSize),
		        ladybugNbrLifes, x + delta, y);
		// number ghost lifes
		if (nbrPlayers > 1) {
			displayNbrLifes(g, ImageUtils.resizeImage(imageGhostPlayer, imageBorderSize, imageBorderSize),
			        ghostNbrLifes, x + delta, y + getHeight() / 2);
		}
		// text
		displayMessageForPlayer(g);
	}

	private void displayMessageDuringGame(Graphics g) {
		var message = new StringBuilder();
		message.append("Level: ");
		message.append(numLevel);
		message.append(" - new life: ");
		message.append(incrementScore);
		message.append("/");
		message.append(nbrPointsForNewLife);
		message.append(" - Score: ");
		message.append(score);
		displayMessageToRight(g, message.toString());
	}

	private void displayMessageForPlayer(Graphics g) {
		if (isInGame) {
			displayMessageDuringGame(g);
		} else {
			displayOffGame(g);
		}
	}

	private void displayMessageToRight(Graphics g, String message) {
		g.setFont(smallFont);
		g.setColor(new Color(96, 128, 255));
		var coordX = getWidth() / 2;
		var coordY = getHeight() / 4 + smallFont.getSize() / 2;
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

	private void displayOffGame(Graphics g) {
		var message = new StringBuilder();
		message.append("Game config : ");
		message.append(nbrPlayers);
		message.append(" player");
		message.append((nbrPlayers > 1 ? "s" : ""));
		displayMessageToRight(g, message.toString());
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
