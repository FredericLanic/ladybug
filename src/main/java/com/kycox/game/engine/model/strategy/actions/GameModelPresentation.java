package com.kycox.game.engine.model.strategy.actions;

import com.kycox.game.tools.Position;
import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.engine.model.strategy.AbstratGameModel;
import com.kycox.game.engine.model.strategy.IGameModelAction;
import lombok.Setter;
import org.springframework.stereotype.Component;


@Setter
@Component
public class GameModelPresentation extends AbstratGameModel implements IGameModelAction {
	private Position ghostRequest = GameMainConstants.POINT_ZERO;

	private void moveBodies() {
		ladybug.move(maze);
		moveGhosts();
	}

	private void moveGhosts() {
		groupGhosts.move(ladybug, maze, ghostRequest);
	}

	@Override
	public void programBeat() {
		setBodiesActions();
		moveBodies();
		setSoundRequests();
	}

	private void setBodiesActions() {
		ladybug.setActions(maze);
		groupGhosts.setActions(ladybug, maze);
	}
}
