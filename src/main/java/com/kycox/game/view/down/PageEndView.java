package com.kycox.game.view.down;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kycox.game.constant.ghost.image.GhostsColorImages;
import com.kycox.game.contract.IGameModelForGameView;
import com.kycox.game.contract.IMainGraphicStructrure;
import com.kycox.game.tools.Screen;
import com.kycox.game.view.ladybug.LadybugView;

import lombok.Setter;

@Named("PageEndView")
public class PageEndView extends JPanel implements Observer, IMainGraphicStructrure {
	private static final Log				logger					 = LogFactory.getLog(PageEndView.class);
	private static final long				serialVersionUID		 = 1L;
	private transient IGameModelForGameView	gameModel;
	@Setter
	private int								height;
	private JPanel							jPanelLadybugKinematique = new JPanel();
	@Inject
	private LadybugView						ladybugView;
	@Inject
	private Screen							screen;
	@Inject
	private StatusGameView					statusGameView;

	@PostConstruct
	public void init() {
		setFocusable(false);
	}

	@Override
	public void setPreferredSize(Dimension preferredSize) {
		super.setPreferredSize(preferredSize);
		initJPanelInside(preferredSize);
	}

	@Override
	public void update(Observable gameModel, Object arg) {
		if (gameModel != null) {
			this.gameModel = (IGameModelForGameView) gameModel;
			setVariableToScoreView(this.gameModel);
			repaint();
		} else {
			logger.info("GameModel is null in " + PageEndView.class);
		}
	}

	private void initJPanelInside(Dimension parentDimension) {
		Dimension dimension = new Dimension();
		dimension.setSize(parentDimension.getWidth(), parentDimension.getHeight() / 2);
		//
		JPanel jPanelMainSore = new JPanel();
		addPanel(jPanelMainSore, dimension, BorderLayout.PAGE_START);
		addPanel(jPanelLadybugKinematique, dimension, BorderLayout.PAGE_END);
		//
		Dimension preferredSize = new Dimension();
		preferredSize.setSize(screen.getEdgeGameSide(), dimension.getHeight());
		statusGameView.setPreferredSize(preferredSize);
		jPanelMainSore.add(statusGameView, BorderLayout.CENTER);
		//
		jPanelMainSore.setBackground(Color.BLACK);
		jPanelLadybugKinematique.setBackground(Color.BLACK);
	}

	private void setVariableToScoreView(IGameModelForGameView gameModel) {
		statusGameView.setGhostNbrLifes(gameModel.getGhostLeftLifes());
		// Rajouter les yeux
		statusGameView.setImageGhostPlayer(GhostsColorImages.GHOST_COLOR_GREY.getImage());
		statusGameView.setImageLadybugPlayer(ladybugView.getStaticView());
		statusGameView.setInGame(gameModel.isInGame());
		statusGameView.setLadybugNbrLifes(gameModel.getLadybug().getLeftLifes());
		statusGameView.setNbrPlayers(gameModel.getNbrPlayers());
		statusGameView.setNumLevel(gameModel.getCurrentGameStatus().getNumLevel());
		statusGameView.setScore(gameModel.getGameScore().getScore());
	}
}
