package com.kycox.game.model.strategy.actions;

import com.kycox.game.message.GameMessaging;
import com.kycox.game.model.CurrentProgramStatus;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;
import com.kycox.game.tools.timer.WaitAndDoActionAfterTimer;
import org.springframework.stereotype.Component;

@Component
public class GameModelGameIsStarting extends AbstratGameModel implements IGameModelAction {
	public GameModelGameIsStarting(GameMessaging gameMessaging) {
		this.gameMessaging = gameMessaging;
	}

	@Override
	public void programBeat() {
		currentGameStatus.setGameStarting();
		waitAndDoActionAfterTimer = new WaitAndDoActionAfterTimer();
		waitAndDoActionAfterTimer.launch(2500, currentGameStatus, CurrentProgramStatus.TO_LEVEL_START);
		setSoundRequests();
	}
}
