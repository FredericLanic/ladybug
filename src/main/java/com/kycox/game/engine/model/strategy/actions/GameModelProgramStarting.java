package com.kycox.game.engine.model.strategy.actions;

import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.engine.model.strategy.AbstratGameModel;
import com.kycox.game.engine.model.strategy.IGameModelAction;
import org.springframework.stereotype.Component;

@Component
public class GameModelProgramStarting extends AbstratGameModel implements IGameModelAction {
	@Override
	public void programBeat() {
		initGame();
		setSoundActive(false);
		currentGameStatus.setProgramStarting();
		tickScheduler.schedule(GameMainConstants.PROGRAM_STARTING_MILLISECONDS,
		        currentGameStatus::setProgramPresentationStart);
	}
}
