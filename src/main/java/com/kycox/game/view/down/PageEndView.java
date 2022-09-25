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
import com.kycox.game.timer.SimpleTimer;
import com.kycox.game.tools.Screen;
import com.kycox.game.view.ghost.GhostView;
import com.kycox.game.view.ladybug.LadybugView;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

@Component
public class PageEndView extends JPanel implements Observer, MainGraphicStructure {
	private static final long DURATION_MESSAGE_SHOWING = 500;
	private static final Log logger = LogFactory.getLog(PageEndView.class);
	private static final long serialVersionUID = 1L;
	@Inject
	private GameMessaging gameMessaging;
	private transient GameModelForViews gameModelForViews;
	@Inject
	private GhostView ghostView;
	private JPanel jPanelLadybugKinematique = new JPanel();
	private JPanel jPanelMainScore = new JPanel();
	@Inject
	private LadybugView ladybugView;
	@Inject
	private Screen screen;
	private SimpleTimer simpleTimer = new SimpleTimer();

	@Inject
	private StatusGameView statusGameView;

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
		statusGameView.setGhostNbrLifes(gameModelForViews.getGhostLeftLifes());
		var ghostUnComputed = gameModelForViews.getUnComputedGhost();
		if (ghostUnComputed.isPresent()) {
			statusGameView.setImageGhostPlayer(ghostView.getImage(ghostUnComputed.get()));
		}
		statusGameView.setImageLadybugPlayer(ladybugView.getStaticView());
		statusGameView.setInGame(gameModelForViews.isInGame());
		statusGameView.setLadybugNbrLifes(gameModelForViews.getLadybug().getLeftLifes());
		statusGameView.setNbrPlayers(gameModelForViews.getNbrPlayers());
		statusGameView.setNumLevel(gameModelForViews.getCurrentProgramStatus().getNumLevel());
		statusGameView.setScore(gameModelForViews.getGameScore().getScore());
		statusGameView.setIncrementScore(gameModelForViews.getIncrementScore());
		statusGameView.setNbrPointsForNewLife(gameModelForViews.getNbrPointsForNewLife());
		statusGameView.setSoundActive(gameModelForViews.isSoundActive());
		if (!simpleTimer.isRunning()) {
			var newMessage = gameMessaging.get();
			statusGameView.setCurrentProgramMessage(newMessage);
			simpleTimer.launch(DURATION_MESSAGE_SHOWING);
		}
	}

	@Override
	public void update(Observable gameModelForViews, Object arg) {
		if (gameModelForViews != null) {
			this.gameModelForViews = (GameModelForViews) gameModelForViews;
			setVariableToScoreView();
			repaint();
		} else {
			logger.info("gameModelForViews is null in " + PageEndView.class);
		}
	}
}
