package com.kycox.game.model.strategy.actions;

import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;
import org.springframework.stereotype.Component;

@Component
public class GameModelLadybugIsDying extends AbstratGameModel implements IGameModelAction {
	@Override
	public void programBeat() {
		ladybugDying.inProgress();
		if (ladybugDying.isInPogress()) {
			newSounds.initSounds();
		}
		if (ladybugDying.isEnd()) {
			ladybug.setStatus(LadybugStatus.DEAD);
			ladybugDying.initBip();
		}
	}
}
