package com.kycox.game.engine.model.strategy.actions;

import com.kycox.game.engine.model.strategy.AbstratGameModel;
import com.kycox.game.engine.model.strategy.IGameModelAction;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Component
public class GameModelLevelIsEnded extends AbstratGameModel implements IGameModelAction {
	private long endingLevelMilliseconds;

	@Override
	public void programBeat() {
		newSounds.initSounds();
		currentGameStatus.setLevelEnding();
		currentGameStatus.storeNumLevel();
		tickScheduler.schedule(endingLevelMilliseconds, currentGameStatus::setLevelStart);
	}
}
