package com.kycox.game.model.strategy;

import org.springframework.stereotype.Component;

@Component
public class GameModelManageAction {
	private IGameModelAction iGameModelAction;

	public void changeStrategy(IGameModelAction iGameModelAction) {
		this.iGameModelAction = iGameModelAction;
	}

	public void execute() {
		iGameModelAction.programBeat();
	}
}
