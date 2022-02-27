package com.kycox.game.model.strategy.actions;

import java.awt.Point;

import javax.inject.Inject;
import javax.inject.Named;

import com.kycox.game.constant.Constants;
import com.kycox.game.message.GameMessaging;
import com.kycox.game.model.CurrentProgramStatus;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;
import com.kycox.game.timer.WaitAndDoActionAfterTimer;

import lombok.Setter;

@Named("GameModelGameIsStarting")
public class GameModelGameIsStarting extends AbstratGameModel implements IGameModelAction {
	@Inject
	private GameMessaging gameMessaging;
	@Setter
	private Point ghostRequest = Constants.POINT_ZERO;

	@Override
	public void programBeat() {
		gameMessaging.init();
		currentGameStatus.setGameStarting();
		waitAndDoActionAfterTimer = new WaitAndDoActionAfterTimer();
		waitAndDoActionAfterTimer.launch(2500, currentGameStatus, CurrentProgramStatus.TO_LEVEL_START);
		setSoundRequests();
	}
}
