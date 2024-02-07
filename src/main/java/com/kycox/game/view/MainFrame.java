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
package com.kycox.game.view;

import com.kycox.game.contract.MainGraphicStructure;
import com.kycox.game.tools.Screen;
import com.kycox.game.view.down.PageEndView;
import com.kycox.game.view.left.PageLeftView;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import javax.swing.*;
import java.awt.*;

/**
 * Frame principale du jeu.
 * FIXME : peut-être utiliser une autre techno que swing
 */
@Component
public class MainFrame extends JFrame implements MainGraphicStructure {
	private final CentralView gameView;
	private final PageEndView pageEndView;

	private final PageLeftView pageLeftView;
	private final transient Screen screen;
	private final Dimension lineDimension = new Dimension();
	private final Dimension pageDimension = new Dimension();
	private final Dimension gameDimension = new Dimension();

	public MainFrame(CentralView gameView, PageEndView pageEndView, PageLeftView pageLeftView, Screen screen) {
		this.gameView = gameView;
		this.pageEndView = pageEndView;
		this.pageLeftView = pageLeftView;
		this.screen = screen;
	}

	private void addPanels() {
		addPanel(new JPanel(), pageDimension, BorderLayout.PAGE_START);
		addPanel(pageLeftView, lineDimension, BorderLayout.LINE_START);
		addPanel(gameView, gameDimension, BorderLayout.CENTER);
		addPanel(new JPanel(), lineDimension, BorderLayout.LINE_END);
		addPanel(pageEndView, pageDimension, BorderLayout.PAGE_END);
	}

	@PostConstruct
	private void init() {
		setTitle("Ladybug by Kycox");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setExtendedState(Frame.MAXIMIZED_BOTH);
		setUndecorated(true);
		var monIcone = new ImageIcon(getClass().getResource("/images/ghosts/color/oxiane.png"));
		setIconImage(monIcone.getImage());
		initScreenSize();
		addPanels();
	}

	private void initScreenSize() {
		gameDimension.setSize(screen.getEdgeGameSide(), screen.getEdgeGameSide());
		pageDimension.setSize(screen.getScreenWidth(), screen.getBorderHeight());
		lineDimension.setSize(screen.getBorderWidth(), screen.getEdgeGameSide());
	}
}