package com.kycox.game.model.strategy.actions;

import javax.inject.Named;

import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;

@Named("GameModelPresentationStarting")
public class GameModelPresentationStarting extends AbstratGameModel implements IGameModelAction {
	@Override
	public void programBeat() {
		initGame();
		currentGameStatus.setProgramPresentation();
	}
}
