package com.kycox.game.model.strategy.actions;

import javax.inject.Named;

import com.kycox.game.constant.Constants;
import com.kycox.game.model.CurrentProgramStatus;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;
import com.kycox.game.timer.WaitAndDoActionAfterTimer;

@Named("GameModelProgramStarting")
public class GameModelProgramStarting extends AbstratGameModel implements IGameModelAction {
	@Override
	public void programBeat() {
		initGame();
		setSoundActive(false);
		currentGameStatus.setProgramStarting();
		waitAndDoActionAfterTimer = new WaitAndDoActionAfterTimer();
		waitAndDoActionAfterTimer.launch(Constants.PROGRAM_STARTING_MILLISECONDS, currentGameStatus,
		        CurrentProgramStatus.TO_PROGRAM_PRESENTATION_START);
	}
}
