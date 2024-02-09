package com.kycox.game.engine.model.strategy.actions;

import com.kycox.game.engine.message.GameMessaging;
import com.kycox.game.engine.model.strategy.AbstratGameModel;
import com.kycox.game.engine.model.strategy.IGameModelAction;
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
