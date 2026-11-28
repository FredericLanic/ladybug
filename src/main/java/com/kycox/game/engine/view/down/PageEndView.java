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

import com.kycox.game.constant.contract.MainGraphicStructure;
import com.kycox.game.engine.message.GameMessaging;
import com.kycox.game.engine.model.EventGameModel;
import com.kycox.game.snapshot.GameSnapshot;
import com.kycox.game.tools.timer.SimpleTimer;
import com.kycox.game.tools.Screen;
import com.kycox.game.engine.view.ghost.GhostView;
import com.kycox.game.engine.view.ladybug.LadybugView;
import jakarta.annotation.PostConstruct;
import org.springframework.context.ApplicationListener;
import com.kycox.game.launcher.GuiComponent;

import javax.swing.*;
import java.awt.*;

@GuiComponent
public class PageEndView extends JPanel implements ApplicationListener<EventGameModel>, MainGraphicStructure {
	private static final long DURATION_MESSAGE_SHOWING = 800;
	private final transient GameMessaging gameMessaging;
	private final transient GhostView ghostView;
	private final transient LadybugView ladybugView;
	private final transient Screen screen;
	private final StatusGameView statusGameView;
	/** La photo du dernier battement (4.6). */
	private transient GameSnapshot snapshot;
	private final JPanel jPanelLadybugKinematique = new JPanel();
	private final JPanel jPanelMainScore = new JPanel();
	private final transient SimpleTimer simpleTimer = new SimpleTimer();

	public PageEndView(GameMessaging gameMessaging, GhostView ghostView, LadybugView ladybugView, Screen screen, StatusGameView statusGameView) {
		this.gameMessaging = gameMessaging;
		this.ghostView = ghostView;
		this.ladybugView = ladybugView;
		this.screen = screen;
		this.statusGameView = statusGameView;
	}

	@PostConstruct
	public void init() {
		setFocusable(false);
	}

	private void initJPanelInside(Dimension parentDimension) {
		var dimension = new Dimension();
		dimension.setSize(parentDimension.getWidth(), parentDimension.getHeight() / 2);
		//
		addPanel(jPanelMainScore, dimension, BorderLayout.PAGE_START);
		addPanel(jPanelLadybugKinematique, dimension, BorderLayout.PAGE_END);
		//
		var preferredSize = new Dimension();
		preferredSize.setSize(screen.getEdgeGameSide(), dimension.getHeight());
		statusGameView.setPreferredSize(preferredSize);
		jPanelMainScore.add(statusGameView, BorderLayout.CENTER);
		//
		jPanelMainScore.setBackground(Color.BLACK);
		jPanelLadybugKinematique.setBackground(Color.BLACK);
	}

	@Override
	public void paintComponent(Graphics g) {
		// fond noir toujours peint, même avant le premier battement (4.1)
		super.paintComponent(g);
	}

	@Override
	public void setPreferredSize(Dimension preferredSize) {
		super.setPreferredSize(preferredSize);
		initJPanelInside(preferredSize);
	}

	private void setVariableToScoreView() {
		statusGameView.setGhostPlayers(snapshot.humanGhosts().stream()
		        .map(ghost -> new StatusGameView.PlayerLives(ghostView.getImage(ghost), ghost.leftLifes()))
		        .toList());
		statusGameView.setImageLadybugPlayer(ladybugView.getStaticView());
		statusGameView.setInGame(snapshot.isInGame());
		statusGameView.setLadybugNbrLifes(snapshot.ladybug().leftLifes());
		statusGameView.setScore(snapshot.score());
		if (!simpleTimer.isRunning()) {
			var newMessage = gameMessaging.get();
			statusGameView.setCurrentProgramMessage(newMessage);
			simpleTimer.launch(DURATION_MESSAGE_SHOWING);
		}
	}

	@Override
	public void onApplicationEvent(EventGameModel event) {
		snapshot = event.getSnapshot();
		setVariableToScoreView();
		repaint();
	}
}

