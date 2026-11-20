package com.kycox.game.engine.model.strategy.actions;

import com.kycox.game.engine.model.strategy.AbstratGameModel;
import com.kycox.game.engine.model.strategy.IGameModelAction;
import org.springframework.stereotype.Component;

@Component
public class GameModelLevelIsEnding extends AbstratGameModel implements IGameModelAction {

	@Override
	public void programBeat() {
		setSoundRequests();
	}
}
