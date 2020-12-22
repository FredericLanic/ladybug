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
package com.kycox.game.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.kycox.game.contract.IMainGraphicStructrure;
import com.kycox.game.tools.Screen;
import com.kycox.game.view.down.PageEndView;

/**
 * Frame principale du jeu.
 *
 * FIXME : peut-être utiliser une autre techno que swing
 *
 */
@Named("MainFrame")
public class MainFrame extends JFrame implements IMainGraphicStructrure {
	private static final long serialVersionUID = 1L;
	private Dimension		  gameDimension	   = new Dimension();
	@Inject
	private GameView		  gameView;
	private Dimension		  lineDimension	   = new Dimension();
	private Dimension		  pageDimension	   = new Dimension();
	@Inject
	private PageEndView		  pageEndView;
	@Inject
	private Screen			  screen;

	@PostConstruct
	private void init() {
		setTitle("LadyBug");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setExtendedState(Frame.MAXIMIZED_BOTH);
		initScreenSize();
		setUndecorated(true);
		addPanel(new JPanel(), pageDimension, BorderLayout.PAGE_START);
		addPanel(new JPanel(), lineDimension, BorderLayout.LINE_START);
		addPanel(new JPanel(), lineDimension, BorderLayout.LINE_END);
		addPanel(pageEndView, pageDimension, BorderLayout.PAGE_END);
		addPanel(gameView, gameDimension, BorderLayout.CENTER);
	}

	private void initScreenSize() {
		gameDimension.setSize(screen.getEdgeGameSide(), screen.getEdgeGameSide());
		pageDimension.setSize(screen.getScreenWidth(), screen.getBorderHeight());
		lineDimension.setSize(screen.getBorderWidth(), screen.getEdgeGameSide());
	}
}