package com.kycox.game.engine.model.strategy.actions;

import com.kycox.game.tools.Position;
import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.engine.model.strategy.AbstratGameModel;
import com.kycox.game.engine.model.strategy.GameModelManageAction;
import com.kycox.game.engine.model.strategy.IGameModelAction;
import org.springframework.stereotype.Component;


@Component
public class GameModelGameIsInGame extends AbstratGameModel implements IGameModelAction {
	private final GameModelGameIsPlaying gameModeGameIsPlaying;
	private final GameModelGhostIsDead gameModeGhostIsDead;
	private final GameModelLadybugIsDead gameModeLadybugIsDead;
	private final GameModelLadybugIsDying gameModeLadybugIsDying;
	private final GameModelManageAction gameModelManageAction;

	public GameModelGameIsInGame(GameModelGameIsPlaying gameModeGameIsPlaying,
								 GameModelGhostIsDead gameModeGhostIsDead,
								 GameModelLadybugIsDead gameModeLadybugIsDead,
								 GameModelLadybugIsDying gameModeLadybugIsDying,
								 GameModelManageAction gameModelManageAction) {
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
		} else if (releaseEliminatedHumanGhostsThenCheck()) {
			gameModelManageAction.changeStrategy(gameModeGhostIsDead);
		} else {
			gameModelManageAction.changeStrategy(gameModeGameIsPlaying);
		}
		gameModelManageAction.execute();
	}

	/**
	 * Rend à l'ordinateur les fantômes humains éliminés (s'il en reste d'autres),
	 * puis dit si tous sont éliminés (règle C50, étape 5.3).
	 */
	private boolean releaseEliminatedHumanGhostsThenCheck() {
		groupGhosts.releaseEliminatedHumanGhosts();
		return groupGhosts.allHumanGhostsEliminated();
	}
}
