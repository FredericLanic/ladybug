package com.kycox.game.model.strategy.actions;

import javax.inject.Named;

import com.kycox.game.model.CurrentGameStatus;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;
import com.kycox.game.timer.WaitAndDoActionAfterTimer;

import lombok.Setter;

@Named("GameModelNoAction")
public class GameModelNoAction extends AbstratGameModel implements IGameModelAction {	
	@Override
	public void execute() { }	
}
