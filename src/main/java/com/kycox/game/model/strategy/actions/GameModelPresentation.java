package com.kycox.game.model.strategy.actions;

import com.kycox.game.constant.GameMainConstants;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.awt.*;

@Setter
@Component
public class GameModelPresentation extends AbstratGameModel implements IGameModelAction {
	private Point ghostRequest = GameMainConstants.POINT_ZERO;

	private void moveBodies() {
		ladybug.move(screenData);
		moveGhosts();
	}

	private void moveGhosts() {
		groupGhosts.move(ladybug, screenData, ghostRequest);
	}

	@Override
	public void programBeat() {
		setBodiesActions();
		moveBodies();
		setSoundRequests();
	}

	private void setBodiesActions() {
		ladybug.setActions(screenData);
		groupGhosts.setActions(ladybug, screenData);
	}
}
