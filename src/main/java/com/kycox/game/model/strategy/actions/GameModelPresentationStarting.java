package com.kycox.game.model.strategy.actions;

import javax.inject.Inject;
import javax.inject.Named;

import com.kycox.game.message.GameMessaging;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;

@Named("GameModelPresentationStarting")
public class GameModelPresentationStarting extends AbstratGameModel implements IGameModelAction {
	@Inject
	private GameMessaging gameMessaging;

	@Override
	public void programBeat() {
		initGame();
		gameMessaging.init();
		currentGameStatus.setProgramPresentation();
	}
}
