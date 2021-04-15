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

import lombok.Getter;
import lombok.Setter;

@Named("StatusGameView")
public class StatusGameView extends JPanel {
	private static final Log  logger		   = LogFactory.getLog(StatusGameView.class);
	private static final long serialVersionUID = 4546077700634533519L;
	@Setter
	private int				  ghostNbrLifes;
	@Setter
	private Image			  imageGhostPlayer;
	@Setter
	private Image			  imageLadybugPlayer;
	@Setter
	private boolean			  isInGame;
	@Setter
	private int				  ladybugNbrLifes;
	@Setter
	private int				  nbrPlayers;
	@Setter
	private int				  numLevel;
	@Setter
	private int				  score;	
	@Setter
	private boolean 		  soundActive;
	private final Font		  smallFont		   = new Font("CrackMan", Font.BOLD, 14);

	@PostConstruct
	public void init() {
		setBackground(Color.BLACK);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		display(g);
	}

	private void diplayCurrentScoreAndLevel(Graphics g) {
		
		
		if (!isInGame)
			return;
		StringBuilder message = new StringBuilder();
		message.append("Level: ");
		message.append(numLevel);
		message.append(" - Score: ");
		message.append(score);
		message.append(" - Sound");		
		message.append(soundActive?" ON":" OFF");
		
		displayTexte(g, message.toString());
	}

	private void display(Graphics g) {
		int	x				= 0;
		int	y				= 0;
		int	delta			= 2;
		int	imageBorderSize	= (this.getHeight() - 6 * delta) / 2;
		// number ladybug lifes
		displayNbrLifes(g, ImageUtils.resizeImage(imageLadybugPlayer, imageBorderSize, imageBorderSize),
		        ladybugNbrLifes, x + delta, y);
		// number ghost lifes
		if (nbrPlayers > 1) {
			displayNbrLifes(g, ImageUtils.resizeImage(imageGhostPlayer, imageBorderSize, imageBorderSize),
			        ghostNbrLifes, x + delta, y + this.getHeight() / 2);
		}
		// text
		diplayCurrentScoreAndLevel(g);
		displayOffGame(g);
	}

	private void displayNbrLifes(Graphics g, Image imageBody, int nbrLifes, int x, int y) {
		if (!isInGame)
			return;
		g.drawImage(imageBody, x, y, this);
		g.setColor(Color.YELLOW);
		g.setFont(smallFont);
		int	coordX = x + imageBody.getWidth(null);
		int	coordY = y + imageBody.getHeight(null) / 2 + smallFont.getSize() / 2;
		g.drawString("x " + nbrLifes, coordX, coordY);
	}

	private void displayOffGame(Graphics g) {
		if (isInGame)
			return;
		StringBuilder message = new StringBuilder();
		message.append("Game config : ");
		message.append(nbrPlayers);
		message.append(" player");
		message.append((nbrPlayers > 1 ? "s" : ""));
		displayTexte(g, message.toString());
	}

	private void displayTexte(Graphics g, String message) {
		g.setFont(smallFont);
		g.setColor(new Color(96, 128, 255));
		int	coordX = this.getWidth() / 2;
		int	coordY = this.getHeight() / 4 + smallFont.getSize() / 2;
		g.drawString(message, coordX, coordY);
	}
}
