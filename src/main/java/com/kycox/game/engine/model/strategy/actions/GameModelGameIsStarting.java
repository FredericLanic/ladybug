package com.kycox.game.engine.model.strategy.actions;

import com.kycox.game.engine.message.GameMessaging;
import com.kycox.game.engine.model.strategy.AbstratGameModel;
import com.kycox.game.engine.model.strategy.IGameModelAction;
import org.springframework.stereotype.Component;

@Component
public class GameModelGameIsStarting extends AbstratGameModel implements IGameModelAction {
	public GameModelGameIsStarting(GameMessaging gameMessaging) {
		this.gameMessaging = gameMessaging;
	}

	@Override
	public void programBeat() {
		currentGameStatus.setGameStarting();
		tickScheduler.schedule(2500, currentGameStatus::setLevelStart);
		setSoundRequests();
	}
}
