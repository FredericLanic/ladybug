package com.kycox.game.model.strategy;

import com.kycox.game.constant.GameStatus;
import com.kycox.game.model.strategy.actions.*;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class ManageActionContext {
	private final Map<GameStatus, IGameModelAction> mapGameContext = new HashMap<>();

	private final GameModelGameIsEnding gameModelGameIsEnding;
	private final GameModelLevelIsStarting gameModelLevelIsStarting;
	private final GameModelLevelIsEnded gameModelLevelIsEnded;
	private final GameModelGameIsInGame gameModelGameIsInGame;

	public ManageActionContext(GameModelProgramStarting gameModelProgramStarting,
							   GameModelPresentationStarting gameModelPresentationStarting,
							   GameModelPresentation gameModelPresentation,
							   GameModelGameIsStarting gameModelGameIsStarting,
							   GameModelLevelIsStarting gameModelLevelIsStarting,
							   GameModelGameIsInGame gameModelGameIsInGame,
							   GameModelLevelIsEnded gameModelLevelIsEnded,
							   GameModelLevelIsEnding gameModelLevelIsEnding,
							   GameModelGameIsEnding gameModelGameIsEnding,
							   GameModelNoAction gameModelNoAction,
							   GameModelAskKeepPreviousGameLevel gameModelAskKeepPreviousGameLevel) {
		this.gameModelGameIsEnding = gameModelGameIsEnding;
		this.gameModelLevelIsStarting = gameModelLevelIsStarting;
		this.gameModelLevelIsEnded = gameModelLevelIsEnded;
		this.gameModelGameIsInGame = gameModelGameIsInGame;

		mapGameContext.put(GameStatus.PROGRAM_START, gameModelProgramStarting);
		mapGameContext.put(GameStatus.PROGRAM_PRESENTATION_START, gameModelPresentationStarting);
		mapGameContext.put(GameStatus.PROGRAM_PRESENTATION, gameModelPresentation);
		mapGameContext.put(GameStatus.PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL, gameModelAskKeepPreviousGameLevel);
		mapGameContext.put(GameStatus.GAME_START, gameModelGameIsStarting);
		mapGameContext.put(GameStatus.LEVEL_START, gameModelLevelIsStarting);
		mapGameContext.put(GameStatus.IN_GAME, gameModelGameIsInGame);
		mapGameContext.put(GameStatus.LEVEL_END, gameModelLevelIsEnded);
		mapGameContext.put(GameStatus.LEVEL_ENDING, gameModelLevelIsEnding);
		mapGameContext.put(GameStatus.GAME_END, gameModelGameIsEnding);
		mapGameContext.put(GameStatus.NO_STATUS, gameModelNoAction);
	}

	public IGameModelAction getStrategyStatusAction(GameStatus gameStatus) {
		IGameModelAction searchAction = mapGameContext.get(gameStatus);
		if (searchAction != null) {
			return searchAction;
		} else {
			return getNoAction();
		}
	}

	public IGameModelAction getNoAction() {
		return mapGameContext.get(GameStatus.NO_STATUS);
	}

	public void setBeginningMilliseconds(long beginningMilliseconds) {
		gameModelLevelIsStarting.setBeginningMilliseconds(beginningMilliseconds);
	}

	public void setTimeEndingMilliseconds(long timeEndingMilliseconds) {
		gameModelGameIsEnding.setTimeEnding(timeEndingMilliseconds);
	}

	public void setEndingLevelMilliseconds(long endingLevelMilliseconds) {
		gameModelLevelIsEnded.setEndingLevelMilliseconds(endingLevelMilliseconds);
	}

	public void setGhostRequest(Point ghostRequest) {
		gameModelGameIsInGame.setGhostRequest(ghostRequest);
	}
}
