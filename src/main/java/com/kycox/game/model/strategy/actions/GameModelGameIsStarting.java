package com.kycox.game.model.strategy.actions;

import com.kycox.game.message.GameMessaging;
import com.kycox.game.model.CurrentProgramStatus;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;
import com.kycox.game.timer.WaitAndDoActionAfterTimer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GameModelGameIsStarting extends AbstratGameModel implements IGameModelAction {
	private final GameMessaging gameMessaging;

	@Autowired
	public GameModelGameIsStarting(GameMessaging gameMessaging) {
		this.gameMessaging = gameMessaging;
	}

	@Override
	public void programBeat() {
		gameMessaging.init();
		currentGameStatus.setGameStarting();
		waitAndDoActionAfterTimer = new WaitAndDoActionAfterTimer();
		waitAndDoActionAfterTimer.launch(2500, currentGameStatus, CurrentProgramStatus.TO_LEVEL_START);
		setSoundRequests();
	}
}
