package com.kycox.game.model.strategy.actions;

import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GameModelLadybugIsDead extends AbstratGameModel implements IGameModelAction {
	private static final Logger logger = LoggerFactory.getLogger(GameModelLadybugIsDead.class);

	@Override
	public void programBeat() {
		logger.info("Ladybug is dead");
		ladybug.lostsALife();
		// test fin du jeu
		if (ladybug.getLeftLifes() == 0) {
			logger.info("Ladybug lost the game");
			gameScore.setOldScore(gameScore.getScore());
			currentGameStatus.setGameEnd();
		} else {
			continueLevel();
		}
	}
}
