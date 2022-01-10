package com.kycox.game.model.strategy.actions;

import javax.inject.Named;

import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;

@Named("GameModelNoAction")
public class GameModelNoAction extends AbstratGameModel implements IGameModelAction {
	@Override
	public void programBeat() {
		// No action, so we do nothing
	}
}
