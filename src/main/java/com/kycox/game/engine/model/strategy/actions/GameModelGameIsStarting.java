package com.kycox.game.engine.model.strategy.actions;

import com.kycox.game.engine.message.GameMessaging;
import com.kycox.game.engine.model.CurrentProgramStatus;
import com.kycox.game.engine.model.strategy.AbstratGameModel;
import com.kycox.game.engine.model.strategy.IGameModelAction;
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
