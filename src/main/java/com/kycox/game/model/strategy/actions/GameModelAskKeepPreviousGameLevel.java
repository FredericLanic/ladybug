package com.kycox.game.model.strategy.actions;

import com.kycox.game.constant.Constants;
import com.kycox.game.level.RepositoryLevel;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class GameModelAskKeepPreviousGameLevel extends AbstratGameModel implements IGameModelAction {
	private final RepositoryLevel repositoryLevel;

	@Setter
	private Point ghostRequest = Constants.POINT_ZERO;

	public GameModelAskKeepPreviousGameLevel(RepositoryLevel repositoryLevel) {
		this.repositoryLevel = repositoryLevel;
	}

	private void moveBodies() {
		ladybug.move(screenData);
		moveGhosts();
	}

	private void moveGhosts() {
		groupGhosts.move(ladybug, screenData, ghostRequest);
	}

	@Override
	public void programBeat() {
		if (repositoryLevel.getNumLevel() == 0) {
			currentGameStatus.setGameStart();
		} else {
			setBodiesActions();
			moveBodies();
			setSoundRequests();
		}
	}

	private void setBodiesActions() {
		ladybug.setActions(screenData);
		groupGhosts.setActions(ladybug, screenData);
	}
}
