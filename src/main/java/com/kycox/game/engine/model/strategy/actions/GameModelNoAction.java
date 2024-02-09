package com.kycox.game.engine.model.strategy.actions;

import com.kycox.game.engine.model.strategy.AbstratGameModel;
import com.kycox.game.engine.model.strategy.IGameModelAction;
import org.springframework.stereotype.Component;

@Component
public class GameModelNoAction extends AbstratGameModel implements IGameModelAction {
	@Override
	public void programBeat() {
		// No action, so we do nothing
	}
}
