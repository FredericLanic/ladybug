package com.kycox.game.model.strategy.actions;

import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kycox.game.constant.Constants;
import com.kycox.game.model.CurrentProgramStatus;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;
import com.kycox.game.timer.WaitAndDoActionAfterTimer;

@Named("GameModelInitialisationProgram")
public class GameModelInitialisationProgram extends AbstratGameModel implements IGameModelAction {

	private static final Log logger = LogFactory.getLog(GameModelInitialisationProgram.class);

	@Override
	public void programBeat() {
		// TODO Auto-generated method stub
		initGame();
		setSoundActive(false);
		currentGameStatus.setProgramStarting();
		waitAndDoActionAfterTimer = new WaitAndDoActionAfterTimer();
		waitAndDoActionAfterTimer.launch(Constants.PROGRAM_STARTING_MILLISECONDS, currentGameStatus,
		        CurrentProgramStatus.TO_PROGRAM_PRESENTATION_START);
	}

}
