package com.kycox.game.model.strategy.actions;

import com.kycox.game.model.CurrentProgramStatus;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;
import com.kycox.game.tools.timer.WaitAndDoActionAfterTimer;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Component
public class GameModelGameIsEnding extends AbstratGameModel implements IGameModelAction {
	private long timeEnding;

	@Override
	public void programBeat() {
		currentGameStatus.setGameEnding();
		waitAndDoActionAfterTimer = new WaitAndDoActionAfterTimer();
		waitAndDoActionAfterTimer.launch(timeEnding, currentGameStatus,
		        CurrentProgramStatus.TO_PROGRAM_PRESENTATION_START);
	}
}
