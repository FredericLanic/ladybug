package com.kycox.game.model.strategy.actions;

import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;
import org.springframework.stereotype.Component;

@Component
public class GameModelGameAskForceEndGame extends AbstratGameModel implements IGameModelAction {

	@Override
	public void programBeat() {
		// nothing to do
		// waiting for user confirmation to end or the game
	}
}
