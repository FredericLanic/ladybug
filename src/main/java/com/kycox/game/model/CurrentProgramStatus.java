/**
  Copyright (C) 2021  Frédéric Lanic frederic.lanic@outlook.fr

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.kycox.game.model;

import com.kycox.game.constant.game.GameStatus;
import com.kycox.game.contract.DoActionAfterTimer;
import com.kycox.game.contract.GameStatusForGameView;
import com.kycox.game.level.repo.LevelRepository;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CurrentProgramStatus implements GameStatusForGameView, DoActionAfterTimer {
	private static final Logger logger = LoggerFactory.getLogger(CurrentProgramStatus.class);
	public static final int TO_INGAME = 0;
	public static final int TO_LEVEL_START = 3;
	public static final int TO_PROGRAM_PRESENTATION_START = 2;
	public static final int TO_PROGRAM_START = 1;
	public static final int TO_PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL = 4;

	@Setter
	@Getter
	private boolean gameInPause;
	@Getter
	private GameStatus gameStatus;
	@Getter
	@Setter
	private int numLevel;

	private final LevelRepository levelRepository;

	public CurrentProgramStatus(LevelRepository levelRepository) {
		this.levelRepository = levelRepository;
	}

	@Override
	public void doActionAfterTimer(int nbrAction) {
		switch (nbrAction) {
			case TO_INGAME -> setInGame();
			case TO_PROGRAM_START -> setProgramStart();
			case TO_PROGRAM_PRESENTATION_START -> setProgramPresentationStart();
			case TO_LEVEL_START -> setLevelStart();
			case TO_PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL -> setProgramAskKeepPreviousGameLevel();
			default -> logger.debug("no number " + nbrAction + " action");
		}
	}

	// KYLIAN C'EST ICI
	public void initNumLevel() {
		numLevel = 0;
		storeNumLevel();
	}

	// KYLIAN C'EST ICI AUSSI
	public void getStoredNumLevel() {
		setNumLevel(levelRepository.getNumLevel());
	}

	public void storeNumLevel() {
		levelRepository.saveNumLevel(numLevel);
	}

	public void updateNextLevel() {
		numLevel++;
	}

	@Override
	public boolean isGameEnd() {
		return gameStatus == GameStatus.GAME_END;
	}

	@Override
	public boolean isGameEnding() {
		return gameStatus == GameStatus.GAME_ENDING;
	}

	public boolean isGameStart() {
		return gameStatus == GameStatus.GAME_START;
	}

	@Override
	public boolean isGameStarting() {
		return gameStatus == GameStatus.GAME_STARTING;
	}

	@Override
	public boolean isInGame() {
		return gameStatus == GameStatus.IN_GAME;
	}

	public boolean isLevelEnd() {
		return gameStatus == GameStatus.LEVEL_END;
	}

	@Override
	public boolean isLevelEnding() {
		return gameStatus == GameStatus.LEVEL_ENDING;
	}

	public boolean isLevelStart() {
		return gameStatus == GameStatus.LEVEL_START;
	}

	@Override
	public boolean isLevelStarting() {
		return gameStatus == GameStatus.LEVEL_STARTING;
	}

	@Override
	public boolean isProgramAskKeepPreviousGameLevel() { return gameStatus == GameStatus.PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL;}

	@Override
	public boolean isProgramPresentation() {
		return gameStatus == GameStatus.PROGRAM_PRESENTATION;
	}
	public boolean isProgramStart() {
		return gameStatus == GameStatus.PROGRAM_START;
	}

	@Override
	public boolean isProgramStarting() {
		return gameStatus == GameStatus.PROGRAM_STARTING;
	}

	public void setGameEnd() {
		gameStatus = GameStatus.GAME_END;
		logger.info("Passage du status en " + gameStatus);
	}

	public void setGameEnding() {
		gameStatus = GameStatus.GAME_ENDING;
		logger.info("Passage du status en " + gameStatus);
	}

	public void setGameStart() {
		gameStatus = GameStatus.GAME_START;
		logger.info("Passage du status en " + gameStatus);
	}

	public void setGameStarting() {
		gameStatus = GameStatus.GAME_STARTING;
		logger.info("Passage du status en " + gameStatus);
	}

	public void setInGame() {
		gameStatus = GameStatus.IN_GAME;
		logger.info("Passage du status en " + gameStatus);
	}

	public void setGameAskForceEndGame() {
		gameStatus = GameStatus.ASK_FORCE_END_GAME;
		logger.info("Passage du status en " + gameStatus);
	}

	@Override
	public boolean isGameAskForceEndGame() {
		return gameStatus == GameStatus.ASK_FORCE_END_GAME;
	}

	public void setLevelEnd() {
		gameStatus = GameStatus.LEVEL_END;
		logger.info("Passage du status en " + gameStatus);
	}

	public void setLevelEnding() {
		gameStatus = GameStatus.LEVEL_ENDING;
		logger.info("Passage du status en " + gameStatus);
	}

	public void setLevelStart() {
		gameInPause = false;
		gameStatus = GameStatus.LEVEL_START;
		logger.info("Passage du status en " + gameStatus);
	}

	public void setLevelStarting() {
		gameStatus = GameStatus.LEVEL_STARTING;
		logger.info("Passage du status en " + gameStatus);
	}

	public void setProgramPresentation() {
		gameStatus = GameStatus.PROGRAM_PRESENTATION;
		logger.info("Passage du status en " + gameStatus);
	}

	public void setProgramPresentationStart() {
		gameStatus = GameStatus.PROGRAM_PRESENTATION_START;
		logger.info("Passage du status en " + gameStatus);
	}

	public void setProgramStart() {
		gameStatus = GameStatus.PROGRAM_START;
		logger.info("Passage du status en " + gameStatus);
	}

	public void setProgramStarting() {
		gameStatus = GameStatus.PROGRAM_STARTING;
		logger.info("Passage du status en " + gameStatus);
	}

	public void setProgramAskKeepPreviousGameLevel() {
		gameStatus = GameStatus.PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL;
		logger.info("Passage du status en " + gameStatus);
	}

	@Override
	public String toString() {
		return gameStatus.toString();
	}

}
