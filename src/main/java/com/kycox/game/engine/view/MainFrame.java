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

import com.kycox.game.constant.contract.MainGraphicStructure;
import com.kycox.game.tools.Screen;
import com.kycox.game.engine.view.down.PageEndView;
import com.kycox.game.engine.view.left.PageLeftView;
import com.kycox.game.launcher.GuiComponent;
import jakarta.annotation.PostConstruct;

import javax.swing.*;
import java.awt.*;

/**
 * Frame principale du jeu.
 * FIXME : peut-être utiliser une autre techno que swing
 */
@GuiComponent
public class MainFrame extends JFrame implements MainGraphicStructure {
	private final CentralView gameView;
	private final PageEndView pageEndView;

	private final PageLeftView pageLeftView;
	private final transient Screen screen;
	private final Dimension lineDimension = new Dimension();
	private final Dimension pageDimension = new Dimension();
	private final Dimension topDimension = new Dimension();
	private final Dimension bottomAreaDimension = new Dimension();
	private final Dimension bottomGapDimension = new Dimension();
	private final Dimension gameDimension = new Dimension();

	public MainFrame(CentralView gameView, PageEndView pageEndView, PageLeftView pageLeftView, Screen screen) {
		this.gameView = gameView;
		this.pageEndView = pageEndView;
		this.pageLeftView = pageLeftView;
		this.screen = screen;
	}

	private void addPanels() {
		addPanel(new JPanel(), topDimension, BorderLayout.PAGE_START);
		addPanel(pageLeftView, lineDimension, BorderLayout.LINE_START);
		addPanel(gameView, gameDimension, BorderLayout.CENTER);
		addPanel(new JPanel(), lineDimension, BorderLayout.LINE_END);
		addPanel(bottomArea(), bottomAreaDimension, BorderLayout.PAGE_END);
	}

	/**
	 * Zone du bas collée au labyrinthe (2.8, demande de Frédéric) : le score et
	 * l'animation de Ladybug juste sous le labyrinthe, sur deux cases de haut,
	 * puis une bande vide. Le bloc « labyrinthe + zone du bas » est ainsi centré
	 * dans l'écran, et le labyrinthe garde exactement sa taille.
	 */
	private JPanel bottomArea() {
		var bottomArea = new JPanel(new BorderLayout());
		bottomArea.setBackground(Color.BLACK);
		pageEndView.setBackground(Color.BLACK);
		pageEndView.setPreferredSize(pageDimension);
		bottomArea.add(pageEndView, BorderLayout.PAGE_START);
		var emptyBand = new JPanel();
		emptyBand.setBackground(Color.BLACK);
		emptyBand.setPreferredSize(bottomGapDimension);
		bottomArea.add(emptyBand, BorderLayout.PAGE_END);
		return bottomArea;
	}

	@PostConstruct
	private void init() {
		setTitle("Ladybug by Kycox");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// sur l'écran de la souris, dans la place disponible (2.8)
		setBounds(screen.getUsableBounds());
		setExtendedState(Frame.MAXIMIZED_BOTH);
		setUndecorated(true);
		var monIcone = new ImageIcon(getClass().getResource("/images/ghosts/color/oxiane.png"));
		setIconImage(monIcone.getImage());
		initScreenSize();
		addPanels();
	}

	private void initScreenSize() {
		gameDimension.setSize(screen.getEdgeGameSide(), screen.getEdgeGameSide());
		pageDimension.setSize(screen.getScreenWidth(), screen.getBottomZoneHeight());
		topDimension.setSize(screen.getScreenWidth(), screen.getTopHeight());
		bottomGapDimension.setSize(screen.getScreenWidth(), screen.getBottomGapHeight());
		bottomAreaDimension.setSize(screen.getScreenWidth(), screen.getBottomZoneHeight() + screen.getBottomGapHeight());
		lineDimension.setSize(screen.getBorderWidth(), screen.getEdgeGameSide());
	}
}