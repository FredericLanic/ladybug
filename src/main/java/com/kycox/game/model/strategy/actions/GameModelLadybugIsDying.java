package com.kycox.game.model.strategy.actions;

import javax.inject.Named;

import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;

@Named("GameModelLadybugIsDying")
public class GameModelLadybugIsDying extends AbstratGameModel implements IGameModelAction {
	
	@Override
	public void execute() {
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
