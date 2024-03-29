package com.kycox.game.model.strategy.actions;

import com.kycox.game.model.CurrentProgramStatus;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;
import com.kycox.game.timer.WaitAndDoActionAfterTimer;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class GameModelLevelIsEnded extends AbstratGameModel implements IGameModelAction {
	@Setter
	private long endingLevelMilliseconds;

	@Override
	public void programBeat() {
		newSounds.initSounds();
		currentGameStatus.setLevelEnding();
		currentGameStatus.storeNumLevel();
		waitAndDoActionAfterTimer = new WaitAndDoActionAfterTimer();
		waitAndDoActionAfterTimer.launch(endingLevelMilliseconds, currentGameStatus,
		        CurrentProgramStatus.TO_LEVEL_START);
	}
}
