package com.kycox.game.model.strategy;

import javax.inject.Named;

@Named("GameModelManageAction")
public class GameModelManageAction {
	private IGameModelAction iGameModelAction;

	public void changeStrategy(IGameModelAction iGameModelAction) {
		this.iGameModelAction = iGameModelAction;
	}

	public void execute() {
		iGameModelAction.programBeat();
	}
}
