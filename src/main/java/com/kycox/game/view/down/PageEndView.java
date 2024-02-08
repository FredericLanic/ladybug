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

import com.kycox.game.contract.GameModelForViews;
import com.kycox.game.contract.MainGraphicStructure;
import com.kycox.game.message.GameMessaging;
import com.kycox.game.model.EventGameModel;
import com.kycox.game.timer.SimpleTimer;
import com.kycox.game.tools.Screen;
import com.kycox.game.view.ghost.GhostView;
import com.kycox.game.view.ladybug.LadybugView;
import jakarta.annotation.PostConstruct;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class PageEndView extends JPanel implements ApplicationListener<EventGameModel>, MainGraphicStructure {
	private static final long DURATION_MESSAGE_SHOWING = 500;
	private final transient GameMessaging gameMessaging;
	private final transient GhostView ghostView;
	private final transient LadybugView ladybugView;
	private final transient Screen screen;
	private final StatusGameView statusGameView;
	private transient GameModelForViews gameModelForViews;
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
		if (gameModelForViews != null) {
			super.paintComponent(g);
		}
	}

	@Override
	public void setPreferredSize(Dimension preferredSize) {
		super.setPreferredSize(preferredSize);
		initJPanelInside(preferredSize);
	}

	private void setVariableToScoreView() {
		statusGameView.setGhostNbrLifes(gameModelForViews.getGhostLeftLives());
		var ghostUnComputed = gameModelForViews.getUnComputedGhost();
		ghostUnComputed.ifPresent(ghost -> statusGameView.setImageGhostPlayer(ghostView.getImage(ghost)));
		statusGameView.setImageLadybugPlayer(ladybugView.getStaticView());
		statusGameView.setInGame(gameModelForViews.isInGame());
		statusGameView.setLadybugNbrLifes(gameModelForViews.getLadybug().getLeftLifes());
		statusGameView.setNbrPlayers(gameModelForViews.getNbrPlayers());
		statusGameView.setScore(gameModelForViews.getGameScore().getScore());
		if (!simpleTimer.isRunning()) {
			var newMessage = gameMessaging.get();
			statusGameView.setCurrentProgramMessage(newMessage);
			simpleTimer.launch(DURATION_MESSAGE_SHOWING);
		}
	}

	@Override
	public void onApplicationEvent(EventGameModel event) {
		Object obj = event.getSource();
		if (obj instanceof GameModelForViews eventGameModelForViews) {
			gameModelForViews = eventGameModelForViews;
			setVariableToScoreView();
			repaint();
		}
	}
}

