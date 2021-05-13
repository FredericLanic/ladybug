package com.kycox.game.model.strategy.actions;

import java.awt.Point;

import javax.inject.Named;

import com.kycox.game.constant.Constants;
import com.kycox.game.model.CurrentGameStatus;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;
import com.kycox.game.timer.WaitAndDoActionAfterTimer;

import lombok.Setter;

@Named("GameModelGameIsStarting")
public class GameModelGameIsStarting extends AbstratGameModel implements IGameModelAction {
	@Setter
	private Point ghostRequest = Constants.POINT_ZERO;

	@Override
	public void execute() {
		currentGameStatus.setGameStarting();
		waitAndDoActionAfterTimer = new WaitAndDoActionAfterTimer();
		waitAndDoActionAfterTimer.launch(2500, currentGameStatus, CurrentGameStatus.TO_LEVEL_START);
		setSoundRequests();
	}
}
