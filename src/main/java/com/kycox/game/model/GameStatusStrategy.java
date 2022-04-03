package com.kycox.game.model;

import javax.inject.Inject;
import javax.inject.Named;

import com.kycox.game.model.strategy.GameModelManageAction;
import com.kycox.game.model.strategy.actions.GameModelGameIsEnding;
import com.kycox.game.model.strategy.actions.GameModelGameIsInGame;
import com.kycox.game.model.strategy.actions.GameModelGameIsStarting;
import com.kycox.game.model.strategy.actions.GameModelLevelIsEnded;
import com.kycox.game.model.strategy.actions.GameModelLevelIsEnding;
import com.kycox.game.model.strategy.actions.GameModelLevelIsStarting;
import com.kycox.game.model.strategy.actions.GameModelNoAction;
import com.kycox.game.model.strategy.actions.GameModelPresentation;
import com.kycox.game.model.strategy.actions.GameModelPresentationStarting;
import com.kycox.game.model.strategy.actions.GameModelProgramStarting;

@Named("GameStatusStrategy")
public class GameStatusStrategy {
	@Inject
	private static GameModelGameIsEnding		 gameModeGameIsEnding;
	@Inject
	private static GameModelGameIsStarting		 gameModeGameStarting;
	@Inject
	private static GameModelLevelIsEnding		 gameModeLevelIsEnding;
	@Inject
	private static GameModelLevelIsEnded		 gameModeLevelIsEnds;
	@Inject
	private static GameModelLevelIsStarting		 gameModeLevelStarting;
	@Inject
	private static GameModelGameIsInGame		 gameModelGameIsInGame;
	@Inject
	private static GameModelNoAction			 gameModelNoAction;
	@Inject
	private static GameModelPresentation		 gameModelPresentation;
	@Inject
	private static GameModelPresentationStarting gameModelPresentationStarting;
	@Inject
	private static GameModelProgramStarting		 gameModelProgramStarting;
//	@Inject
	private CurrentProgramStatus  currentProgramStatus;
	private GameModelManageAction gameModelManageAction;

	public GameStatusStrategy() {
	}

	public GameStatusStrategy(GameModelManageAction gameModelManageAction, CurrentProgramStatus currentProgramStatus) {
		this.currentProgramStatus  = currentProgramStatus;
		this.gameModelManageAction = gameModelManageAction;
	}

	public void actionsByTimerBip() {
		switch (currentProgramStatus.getGameStatus()) {
			case PROGRAM_START -> gameModelManageAction.changeStrategy(gameModelProgramStarting);
			case PROGRAM_PRESENTATION_START -> gameModelManageAction.changeStrategy(gameModelPresentationStarting);
			case PROGRAM_PRESENTATION -> gameModelManageAction.changeStrategy(gameModelPresentation);
			case GAME_START -> gameModelManageAction.changeStrategy(gameModeGameStarting);
			case LEVEL_START -> gameModelManageAction.changeStrategy(gameModeLevelStarting);
			case IN_GAME -> gameModelManageAction.changeStrategy(gameModelGameIsInGame);
			case LEVEL_END -> gameModelManageAction.changeStrategy(gameModeLevelIsEnds);
			case LEVEL_ENDING -> gameModelManageAction.changeStrategy(gameModeLevelIsEnding);
			case GAME_END -> gameModelManageAction.changeStrategy(gameModeGameIsEnding);
			case PROGRAM_STARTING -> gameModelManageAction.changeStrategy(gameModelNoAction);
			default -> throw new IllegalArgumentException("Unexpected value: " + currentProgramStatus.getGameStatus());
		}
		gameModelManageAction.execute();
		// StatusStrategy.of(currentProgramStatus.getGameStatus()).execute();
	}
}
