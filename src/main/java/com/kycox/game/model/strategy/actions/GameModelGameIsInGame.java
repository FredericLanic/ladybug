package com.kycox.game.model.strategy.actions;

import javax.inject.Inject;
import javax.inject.Named;

import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.GameModelManageAction;
import com.kycox.game.model.strategy.IGameModelAction;

@Named("GameModelGameIsInGame")
public class GameModelGameIsInGame extends AbstratGameModel implements IGameModelAction {
	@Inject
	private GameModelGameIsPlaying gameModeGameIsPlaying;
	@Inject
	private GameModelGhostIsDead gameModeGhostIsDead;
	@Inject
	private GameModelLadybugIsDead gameModeLadybugIsDead;
	@Inject
	private GameModelLadybugIsDying gameModeLadybugIsDying;
	@Inject
	private GameModelManageAction gameModelManageAction;

	@Override
	public void programBeat() {
		if (ladybug.getStatus() == LadybugStatus.DEAD) {
			gameModelManageAction.changeStrategy(gameModeLadybugIsDead);
		} else if (ladybug.getStatus() == LadybugStatus.DYING) {
			gameModelManageAction.changeStrategy(gameModeLadybugIsDying);
		} else if (groupGhosts.userGhostHasNoLife()) {
			gameModelManageAction.changeStrategy(gameModeGhostIsDead);
		} else {
			gameModelManageAction.changeStrategy(gameModeGameIsPlaying);
		}
		gameModelManageAction.execute();
	}
}
