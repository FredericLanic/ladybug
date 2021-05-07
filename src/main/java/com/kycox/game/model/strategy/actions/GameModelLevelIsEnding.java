package com.kycox.game.model.strategy.actions;

import javax.inject.Named;

import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;

@Named("GameModelLevelIsEnding")
public class GameModelLevelIsEnding extends AbstratGameModel implements IGameModelAction {
	
	@Override
	public void execute() {
		setSoundActive(true);
		setSoundRequests();
	}
}
