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
package com.kycox.game.view.conf;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kycox.game.constant.ghost.image.GhostsBodyImages;
import com.kycox.game.constant.ladybug.LadybugImages;

public class ConfJDialog extends JDialog implements ActionListener {
	private static final Log logger = LogFactory.getLog(ConfJDialog.class);
	private static final long serialVersionUID = 1L;
	private ButtonGroup group = new ButtonGroup();

	public ConfJDialog(JFrame mainFrame) {
		super(mainFrame, "Configuration", true);
		// setsize of dialog
		setSize(300, 150);
		setResizable(false);
		setLocationRelativeTo(mainFrame);
		setBackground(Color.BLACK);
		setUndecorated(true);
		var northPanel = new JPanel();
		northPanel.setBackground(Color.GRAY);
		var jLabelTitle = new JLabel("GAME CONFIGURATION");
		jLabelTitle.setForeground(Color.WHITE);
		northPanel.add(jLabelTitle);
		var centerPanel = new JPanel();
		centerPanel.setBackground(Color.BLACK);
		JPanel ghostPanel = new JGhostChoice(group);
		ghostPanel.setBackground(Color.BLACK);
		var ladybugImg = new JLabel(new ImageIcon(LadybugImages.LADYBUG_UP_3.getImage()));
		ladybugImg.setHorizontalAlignment(SwingConstants.CENTER);
		centerPanel.setLayout(new GridLayout(1, 2));
		centerPanel.add(ghostPanel);
		// Sud : les boutons 1 ou 2 joueurs
		var southPanel = new JPanel();
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.LINE_AXIS));
		southPanel.setBackground(Color.GRAY);
		// bouton 1 joueur
		var button1Player = new Button("1 player");
		button1Player.addActionListener(this);
		button1Player.setBackground(Color.BLACK);
		button1Player.setForeground(Color.WHITE);
		// bouton 2 joueurs
		var button2Players = new Button("2 players");
		button2Players.addActionListener(this);
		button2Players.setBackground(Color.BLACK);
		button2Players.setForeground(Color.WHITE);
		southPanel.add(button1Player);
		southPanel.add(button2Players);
		setLayout(new BorderLayout());
		add(northPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case "1 player":
				// on met un fantôme jouable par les touches
				GhostsBodyImages.ORANGE.setComputed(true);
				GhostsBodyImages.RED.setComputed(true);
				GhostsBodyImages.BLUE.setComputed(true);
				GhostsBodyImages.PINK.setComputed(true);
				dispose();
				break;
			case "2 players":
				GhostsBodyImages.ORANGE.setComputed(true);
				GhostsBodyImages.RED.setComputed(true);
				GhostsBodyImages.BLUE.setComputed(true);
				GhostsBodyImages.PINK.setComputed(true);
				var actionCommand = group.getSelection().getActionCommand();
				switch (actionCommand) {
					case JGhostChoice.BLINKY_CHOICE -> GhostsBodyImages.RED.setComputed(false);
					case JGhostChoice.INKY_CHOICE -> GhostsBodyImages.BLUE.setComputed(false);
					case JGhostChoice.CLYDE_CHOICE -> GhostsBodyImages.ORANGE.setComputed(false);
					case JGhostChoice.PINKY_CHOICE -> GhostsBodyImages.PINK.setComputed(false);
					default -> logger.info("No associated action for " + actionCommand);
				}
				dispose();
			default:
				break;
		}
		dispose();
	}
}
