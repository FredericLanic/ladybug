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
package com.kycox.ladybug.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.view.conf.ConfJDialog;

import lombok.Setter;

/**
 * Frame principale du jeu.
 *
 * FIXME : peut-être utiliser une autre techno que swing
 *
 */
public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private transient Toolkit defaultToolKit   = Toolkit.getDefaultToolkit();
	private double			  edge			   = (double) 15 * Constants.BLOCK_SIZE;
	@Setter
	private GameView		  gameView;
	private double			  rightLeftWidth   = (defaultToolKit.getScreenSize().getWidth() - 15 * Constants.BLOCK_SIZE)
	        / 2;
	private double			  topBottomHeight  = (defaultToolKit.getScreenSize().getHeight()
	        - 16 * Constants.BLOCK_SIZE) / 2;

	/**
	 * Création des JPanel de la JFrame
	 *
	 * @param gameView
	 */
	public void init() {
		setTitle("LadyBug");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setUndecorated(true);
		new ConfJDialog(this);
		// organisation des panels
		Dimension dimension = new Dimension();
		dimension.setSize(edge, edge);
		initGameView(dimension);
		// @FIXME : un peu de refacto !!
		JPanel jPanel = new JPanel();
		dimension = new Dimension();
		dimension.setSize(defaultToolKit.getScreenSize().getWidth(), topBottomHeight);
		jPanel.setBackground(Color.BLACK);
		jPanel.setPreferredSize(dimension);
		add(jPanel, BorderLayout.PAGE_START);
		jPanel	  = new JPanel();
		dimension = new Dimension();
		dimension.setSize(rightLeftWidth, edge);
		jPanel.setBackground(Color.BLACK);
		jPanel.setPreferredSize(dimension);
		add(jPanel, BorderLayout.LINE_START);
		jPanel	  = new JPanel();
		dimension = new Dimension();
		dimension.setSize(defaultToolKit.getScreenSize().getWidth(), topBottomHeight);
		jPanel.setBackground(Color.BLACK);
		jPanel.setPreferredSize(dimension);
		add(jPanel, BorderLayout.PAGE_END);
		jPanel	  = new JPanel();
		dimension = new Dimension();
		dimension.setSize(rightLeftWidth, edge);
		jPanel.setBackground(Color.BLACK);
		jPanel.setPreferredSize(dimension);
		add(jPanel, BorderLayout.LINE_END);
	}

	private void initGameView(Dimension dimension) {
		gameView.setPreferredSize(dimension);
		gameView.setBackground(Color.BLACK);
		add(gameView, BorderLayout.CENTER);
	}
}