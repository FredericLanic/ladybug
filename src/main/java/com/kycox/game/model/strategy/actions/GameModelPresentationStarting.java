package com.kycox.game.model.strategy.actions;

import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;

@Named("GameModelPresentationStarting")
public class GameModelPresentationStarting extends AbstratGameModel implements IGameModelAction {
	private static final Log logger = LogFactory.getLog(GameModelPresentationStarting.class);

	@Override
	public void programBeat() {
		// TODO Auto-generated method stub
		initGame();
		currentGameStatus.setProgramPresentation();
	}
}
