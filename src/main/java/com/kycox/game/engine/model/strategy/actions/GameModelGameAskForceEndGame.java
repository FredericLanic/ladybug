package com.kycox.game.engine.model.strategy.actions;

import com.kycox.game.engine.model.strategy.AbstratGameModel;
import com.kycox.game.engine.model.strategy.IGameModelAction;
import org.springframework.stereotype.Component;

@Component
public class GameModelGameAskForceEndGame extends AbstratGameModel implements IGameModelAction {

	@Override
	public void programBeat() {
		// nothing to do
		// waiting for user confirmation to end or the game
	}
}
