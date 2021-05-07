package com.kycox.game.model.strategy.actions;

import javax.inject.Inject;
import javax.inject.Named;

import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.model.CurrentGameStatus;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.GameModelManageAction;
import com.kycox.game.model.strategy.IGameModelAction;
import com.kycox.game.timer.WaitAndDoActionAfterTimer;

import lombok.Setter;

@Named("GameModelGameIsInGame")
public class GameModelGameIsInGame extends AbstratGameModel implements IGameModelAction {
	
	@Inject
	private GameModelLadybugIsDying gameModeLadybugIsDying;
	@Inject
	private GameModelLadybugIsDead gameModeLadybugIsDead;
	@Inject
	private GameModelGhostIsDead gameModeGhostIsDead;
	@Inject
	private GameModelGameIsPlaying gameModeGameIsPlaying;
	@Inject
	private GameModelManageAction gameModelManageAction;
	
	@Override
	public void execute() {
		if (ladybug.getStatus() == LadybugStatus.DEAD) {
			gameModelManageAction.changeStrategy(gameModeLadybugIsDead);
		} else if (ladybug.getStatus() == LadybugStatus.DYING) {
			gameModelManageAction.changeStrategy(gameModeLadybugIsDying);
		} else if (groupGhosts.userIsDead()) {
			gameModelManageAction.changeStrategy(gameModeGhostIsDead);
		} else {			
			gameModelManageAction.changeStrategy(gameModeGameIsPlaying);
		}
		
		gameModelManageAction.execute();
	}	
}
