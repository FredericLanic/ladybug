/**
  Copyright (C) 2019  Frédéric Lanic frederic.lanic@outlook.fr

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
/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.kycox.ladybug.view.conf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.kycox.ladybug.constant.PicturesEnum;

public class JGhostChoice extends JPanel implements KeyListener {
	public static final String BLINKY_CHOICE	= "Blinky";
	public static final String CLYDE_CHOICE		= "Clyde";
	public static final String INKY_CHOICE		= "Inky";
	public static final String PINKY_CHOICE		= "Pinky";
	private static final long  serialVersionUID	= 1L;
	private ButtonGroup		   group;
	private JLabel			   picture;

	public JGhostChoice(ButtonGroup group) {
		super(new BorderLayout());
		this.group = group;
		setBackground(Color.BLACK);
		// Create the radio buttons.
		JRadioButton pinkyButton = new JRadioButton(PINKY_CHOICE);
		pinkyButton.setMnemonic(KeyEvent.VK_B);
		pinkyButton.setActionCommand(PINKY_CHOICE);
		pinkyButton.setSelected(true);
		pinkyButton.setBackground(Color.BLACK);
		pinkyButton.setForeground(Color.WHITE);
		JRadioButton inkyButton = new JRadioButton(INKY_CHOICE);
		inkyButton.setMnemonic(KeyEvent.VK_C);
		inkyButton.setActionCommand(INKY_CHOICE);
		inkyButton.setBackground(Color.BLACK);
		inkyButton.setForeground(Color.WHITE);
		JRadioButton blinkyButton = new JRadioButton(BLINKY_CHOICE);
		blinkyButton.setMnemonic(KeyEvent.VK_D);
		blinkyButton.setActionCommand(BLINKY_CHOICE);
		blinkyButton.setBackground(Color.BLACK);
		blinkyButton.setForeground(Color.WHITE);
		JRadioButton clydeButton = new JRadioButton(CLYDE_CHOICE);
		clydeButton.setMnemonic(KeyEvent.VK_P);
		clydeButton.setActionCommand(CLYDE_CHOICE);
		clydeButton.setBackground(Color.BLACK);
		clydeButton.setForeground(Color.WHITE);
		// Group the radio buttons.
		group.add(inkyButton);
		group.add(blinkyButton);
		group.add(pinkyButton);
		group.add(clydeButton);
		// Register a listener for the radio buttons.
		pinkyButton.addKeyListener(this);
		inkyButton.addKeyListener(this);
		blinkyButton.addKeyListener(this);
		clydeButton.addKeyListener(this);
		// Set up the picture label.
		picture = new JLabel(new ImageIcon(PicturesEnum.GHOST_PINK_LEFT_EYES.getImg()));
		// The preferred size is hard-coded to be the width of the
		// widest image and the height of the tallest image.
		// A real program would compute this.
		picture.setPreferredSize(new Dimension(177, 122));
		// Put the radio buttons in a column in a panel.
		JPanel radioPanel = new JPanel(new GridLayout(0, 1));
		radioPanel.setBackground(Color.BLACK);
		radioPanel.add(inkyButton);
		radioPanel.add(blinkyButton);
		radioPanel.add(pinkyButton);
		radioPanel.add(clydeButton);
		add(radioPanel, BorderLayout.LINE_START);
		add(picture, BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// nothing to do
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (group.getSelection().getActionCommand()) {
			case BLINKY_CHOICE:
				picture.setIcon(new ImageIcon(PicturesEnum.GHOST_RED_LEFT_EYES.getImg()));
				break;
			case INKY_CHOICE:
				picture.setIcon(new ImageIcon(PicturesEnum.GHOST_BLUE_LEFT_EYES.getImg()));
				break;
			case CLYDE_CHOICE:
				picture.setIcon(new ImageIcon(PicturesEnum.GHOST_ORANGE_LEFT_EYES.getImg()));
				break;
			case PINKY_CHOICE:
				picture.setIcon(new ImageIcon(PicturesEnum.GHOST_PINK_LEFT_EYES.getImg()));
				break;
			default:
				break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// nothing to do
	}
}
