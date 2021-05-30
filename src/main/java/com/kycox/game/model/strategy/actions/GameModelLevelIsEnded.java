package com.kycox.game.model.strategy.actions;

import javax.inject.Named;

import com.kycox.game.model.CurrentProgramStatus;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;
import com.kycox.game.timer.WaitAndDoActionAfterTimer;

import lombok.Setter;

@Named("GameModelLevelIsEnded")
public class GameModelLevelIsEnded extends AbstratGameModel implements IGameModelAction {
	@Setter
	private long endingLevelMilliseconds;

	@Override
	public void execute() {
		setSoundActive(false);
		currentGameStatus.setLevelEnding();
		waitAndDoActionAfterTimer = new WaitAndDoActionAfterTimer();
		waitAndDoActionAfterTimer.launch(endingLevelMilliseconds, currentGameStatus, CurrentProgramStatus.TO_LEVEL_START);
	}
}
