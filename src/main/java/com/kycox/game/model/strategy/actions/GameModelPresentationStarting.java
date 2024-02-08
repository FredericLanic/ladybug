package com.kycox.game.model.strategy.actions;

import com.kycox.game.message.GameMessaging;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;
import org.springframework.stereotype.Component;

@Component
public class GameModelPresentationStarting extends AbstratGameModel implements IGameModelAction {
	public GameModelPresentationStarting(GameMessaging gameMessaging) {
		this.gameMessaging = gameMessaging;
	}

	@Override
	public void programBeat() {
		initGame();
		currentGameStatus.setProgramPresentation();
	}
}
