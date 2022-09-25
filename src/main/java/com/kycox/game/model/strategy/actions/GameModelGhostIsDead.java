package com.kycox.game.model.strategy.actions;

import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;
import org.springframework.stereotype.Component;

@Component
public class GameModelGhostIsDead extends AbstratGameModel implements IGameModelAction {
	@Override
	public void programBeat() {
		currentGameStatus.setGameEnd();
	}
}
