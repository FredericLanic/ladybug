package com.kycox.game.model.strategy.actions;

import com.kycox.game.constant.GameMainConstants;
import com.kycox.game.level.repo.LevelRepository;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class GameModelAskKeepPreviousGameLevel extends AbstratGameModel implements IGameModelAction {
	private final LevelRepository levelRepository;

	@Setter
	private Point ghostRequest = GameMainConstants.POINT_ZERO;

	public GameModelAskKeepPreviousGameLevel(LevelRepository levelRepository) {
		this.levelRepository = levelRepository;
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
		if (levelRepository.getNumLevel() == 0) {
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
