package com.kycox.game.engine.model.strategy.actions;

import com.kycox.game.engine.model.strategy.AbstratGameModel;
import com.kycox.game.engine.model.strategy.IGameModelAction;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Component
public class GameModelGameIsEnding extends AbstratGameModel implements IGameModelAction {
	private long timeEnding;

	@Override
	public void programBeat() {
		currentGameStatus.setGameEnding();
		tickScheduler.schedule(timeEnding, currentGameStatus::setProgramPresentationStart);
	}
}
