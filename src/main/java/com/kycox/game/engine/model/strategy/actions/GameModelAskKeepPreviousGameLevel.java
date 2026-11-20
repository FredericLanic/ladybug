package com.kycox.game.engine.model.strategy.actions;

import com.kycox.game.tools.Position;
import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.engine.maze.repo.LevelRepository;
import com.kycox.game.engine.model.strategy.AbstratGameModel;
import com.kycox.game.engine.model.strategy.IGameModelAction;
import lombok.Setter;
import org.springframework.stereotype.Component;


@Component
public class GameModelAskKeepPreviousGameLevel extends AbstratGameModel implements IGameModelAction {
	private final LevelRepository levelRepository;

	@Setter
	private Position ghostRequest = GameMainConstants.POINT_ZERO;

	public GameModelAskKeepPreviousGameLevel(LevelRepository levelRepository) {
		this.levelRepository = levelRepository;
	}

	private void moveBodies() {
		ladybug.move(maze);
		moveGhosts();
	}

	private void moveGhosts() {
		groupGhosts.move(ladybug, maze, ghostRequest);
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
		ladybug.setActions(maze);
		groupGhosts.setActions(ladybug, maze);
	}
}
