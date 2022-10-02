package com.kycox.game.model.strategy.actions;

import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.GameModelManageAction;
import com.kycox.game.model.strategy.IGameModelAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class GameModelGameIsInGame extends AbstratGameModel implements IGameModelAction {
	private final GameModelGameIsPlaying gameModeGameIsPlaying;
	private final GameModelGhostIsDead gameModeGhostIsDead;
	private final GameModelLadybugIsDead gameModeLadybugIsDead;
	private final GameModelLadybugIsDying gameModeLadybugIsDying;
	private final GameModelManageAction gameModelManageAction;
	@Autowired
	public GameModelGameIsInGame(GameModelGameIsPlaying gameModeGameIsPlaying,
								 GameModelGhostIsDead gameModeGhostIsDead,
								 GameModelLadybugIsDead gameModeLadybugIsDead,
								 GameModelLadybugIsDying gameModeLadybugIsDying,
								 GameModelManageAction gameModelManageAction
	) {
		this.gameModeGameIsPlaying = gameModeGameIsPlaying;
		this.gameModeGhostIsDead = gameModeGhostIsDead;
		this.gameModeLadybugIsDead = gameModeLadybugIsDead;
		this.gameModeLadybugIsDying = gameModeLadybugIsDying;
		this.gameModelManageAction = gameModelManageAction;
	}

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

	public void setGhostRequest(Point ghostRequest) {
		gameModeGameIsPlaying.setGhostRequest(ghostRequest);
	}
}
