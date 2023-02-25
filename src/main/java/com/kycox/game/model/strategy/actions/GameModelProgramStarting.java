package com.kycox.game.model.strategy.actions;

import com.kycox.game.constant.GameMainConstants;
import com.kycox.game.model.CurrentProgramStatus;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;
import com.kycox.game.timer.WaitAndDoActionAfterTimer;
import org.springframework.stereotype.Component;

@Component
public class GameModelProgramStarting extends AbstratGameModel implements IGameModelAction {
	@Override
	public void programBeat() {
		initGame();
		setSoundActive(false);
		currentGameStatus.setProgramStarting();
		waitAndDoActionAfterTimer = new WaitAndDoActionAfterTimer();
		waitAndDoActionAfterTimer.launch(GameMainConstants.PROGRAM_STARTING_MILLISECONDS, currentGameStatus,
		        CurrentProgramStatus.TO_PROGRAM_PRESENTATION_START);
	}
}
