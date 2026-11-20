package com.kycox.game.engine.model.strategy.actions;

import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.engine.model.strategy.AbstratGameModel;
import com.kycox.game.engine.model.strategy.IGameModelAction;
import org.springframework.stereotype.Component;

@Component
public class GameModelLadybugIsDying extends AbstratGameModel implements IGameModelAction {
	@Override
	public void programBeat() {
		ladybugDying.inProgress();
		if (ladybugDying.isInProgress()) {
			newSounds.initSounds();
		}
		if (ladybugDying.isEnd()) {
			ladybug.setStatus(LadybugStatus.DEAD);
			ladybugDying.initBip();
		}
	}
}
