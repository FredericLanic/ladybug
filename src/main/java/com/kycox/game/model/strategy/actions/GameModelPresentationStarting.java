package com.kycox.game.model.strategy.actions;

import com.kycox.game.message.GameMessaging;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GameModelPresentationStarting extends AbstratGameModel implements IGameModelAction {
	private final GameMessaging gameMessaging;

	@Autowired
	public GameModelPresentationStarting(GameMessaging gameMessaging) {
		this.gameMessaging = gameMessaging;
	}

	@Override
	public void programBeat() {
		initGame();
		gameMessaging.init();
		currentGameStatus.setProgramPresentation();
	}
}
