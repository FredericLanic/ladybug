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

import static com.kycox.game.constant.Constants.PACE;

import java.awt.Point;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.Timer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kycox.game.body.ghost.GhostsGroup;
import com.kycox.game.body.ladybug.Ladybug;
import com.kycox.game.body.ladybug.LadybugDying;
import com.kycox.game.constant.ghost.image.GhostsBodyImages;
import com.kycox.game.contract.GameModelForController;
import com.kycox.game.contract.GameModelForSounds;
import com.kycox.game.contract.GameModelForViews;
import com.kycox.game.level.ScreenData;
import com.kycox.game.model.strategy.GameModelManageAction;
import com.kycox.game.model.strategy.actions.GameModelGameIsEnding;
import com.kycox.game.model.strategy.actions.GameModelGameIsInGame;
import com.kycox.game.model.strategy.actions.GameModelGameIsPlaying;
import com.kycox.game.model.strategy.actions.GameModelGameIsStarting;
import com.kycox.game.model.strategy.actions.GameModelLevelIsEnded;
import com.kycox.game.model.strategy.actions.GameModelLevelIsEnding;
import com.kycox.game.model.strategy.actions.GameModelLevelIsStarting;
import com.kycox.game.model.strategy.actions.GameModelNoAction;
import com.kycox.game.model.strategy.actions.GameModelPresentation;
import com.kycox.game.model.strategy.actions.GameModelPresentationStarting;
import com.kycox.game.model.strategy.actions.GameModelProgramStarting;
import com.kycox.game.score.GroupMessages;
import com.kycox.game.score.Score;
import com.kycox.game.sound.NewSounds;
import com.kycox.game.tools.Utils;

import lombok.Getter;
import lombok.Setter;

/**
 * Modèle du jeu MVC : c'est le modèle qui contient le timer du jeu (coeur du
 * jeu)
 *
 */
@SuppressWarnings("deprecation")
@Named("GameModel")
public class GameModel extends Observable implements GameModelForViews, GameModelForSounds, GameModelForController {
	private static final Log logger = LogFactory.getLog(GameModel.class);
	@Inject
	@Getter
	private CurrentProgramStatus currentProgramStatus;
	@Inject
	private GameModelGameIsEnding gameModeGameIsEnding;
	@Inject
	private GameModelGameIsPlaying gameModeGameIsPlaying;
	@Inject
	private GameModelGameIsStarting gameModeGameStarting;
	@Inject
	private GameModelLevelIsEnding gameModeLevelIsEnding;
	@Inject
	private GameModelLevelIsEnded gameModeLevelIsEnds;
	@Inject
	private GameModelLevelIsStarting gameModeLevelStarting;
	@Inject
	private GameModelGameIsInGame gameModelGameIsInGame;
	@Inject
	private GameModelManageAction gameModelManageAction;
	@Inject
	private GameModelNoAction gameModelNoAction;
	@Inject
	private GameModelPresentation gameModelPresentation;
	@Inject
	private GameModelPresentationStarting gameModelPresentationStarting;
	@Inject
	private GameModelProgramStarting gameModelProgramStarting;
	@Getter
	@Inject
	private Score gameScore;
	@Getter
	@Inject
	private GhostsGroup groupGhosts;
	@Getter
	@Inject
	private GroupMessages groupMessages;
	@Getter
	@Inject
	private Ladybug ladybug;
	@Getter
	@Inject
	private LadybugDying ladybugDying;
	@Getter
	@Inject
	private NewSounds newSounds;
	private final Timer programTimer = createProgramTimer();
	@Getter
	@Inject
	private ScreenData screenData;
	@Getter
	@Setter
	private boolean showHelpForKeys = false;
	@Getter
	@Setter
	private boolean showHelpForXboxes = false;

	@Getter
	private boolean soundActive = true;

	// Workflow du programme :
	// PROGRAM_START
	// * PROGRAM_STARTING -> timer puis
	// * PROGRAM_PRESENTATION_START : initialise la nouvelle partie
	// * PROGRAM_PRESENTATION : en attente d'action de l'utilisateur
	// * GAME_START
	// * LEVEL START
	// * LEVEL_STARTING -> timer puis
	// * IN_GAME (jeu)
	// * LEVEL_END
	// * LEVEL_ENDING puis soit LEVEL_START soit GAME_END
	// * GAME_END
	// * GAME_ENDING -> timer puis
	// * PROGRAM_PRESENTATION_START
	private void actionsByTimerBip() {
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
			default -> gameModelManageAction.changeStrategy(gameModelNoAction);
		}
		gameModelManageAction.execute();
		// logger.debug("CurrentStatus : " + currentProgramStatus.getGameStatus());
		setChanged();
		notifyObservers();
	}

	@Override
	public void changeLitLampMode() {
		screenData.setLitLampMode(!screenData.isLitLampMode());
	}

	private Timer createProgramTimer() {
		ActionListener action = event -> {
			actionsByTimerBip();
		};
		return new Timer(PACE, action);
	}

	@Override
	public void forceStopGame() {
		if (programTimer.isRunning()) {
			logger.info("Force Stop Game");
			gameScore.setOldScore(-1);
			currentProgramStatus.setGameEnd();
			initSounds();
		}
	}

	@Override
	public void gameInPause() {
		if (programTimer.isRunning()) {
			logger.info("Game in pause");
			programTimer.stop();
		} else {
			logger.info("Game regoes");
			startProgramTimer();
		}
	}

	@Override
	public int getGhostLeftLifes() {
		return groupGhosts.getLeftLives();
	}

	@Override
	public int getIncrementScore() {
		return gameScore.getIncrementScore();
	}

	@Override
	public int getNbrPlayers() {
		if (groupGhosts.hasGhostUser()) {
			return 2;
		}
		return 1;
	}

	@Override
	public int getNbrPointsForNewLife() {
		return gameScore.getNbrPointsForNewLife();
	}

	@PostConstruct
	private void init() {
		currentProgramStatus.setProgramStart();
		gameModelManageAction.changeStrategy(gameModelNoAction);
		startProgramTimer();
	}

	public void initSounds() {
		newSounds.initSounds();
	}

	@Override
	public boolean isGamePresentation() {
		return currentProgramStatus.isProgramPresentation();
	}

	@Override
	public boolean isInGame() {
		return currentProgramStatus.isInGame();
	}

	public void setBeginningMilliseconds(long beginningMilliseconds) {
		gameModeGameIsEnding.setTimeEnding(beginningMilliseconds);
		gameModeLevelStarting.setBeginningMilliseconds(beginningMilliseconds);
	}

	public void setEndingLevelMilliseconds(long endingLevelMilliseconds) {
		gameModeLevelIsEnds.setEndingLevelMilliseconds(endingLevelMilliseconds);
	}

	@Override
	public void setGhostRequest(Point ghostRequest) {
		gameModeGameIsPlaying.setGhostRequest(ghostRequest);
	}

	@Override
	public void setLadybugRequest(Point point) {
		ladybug.setUserRequest(point);
	}

	@Override
	public void setMultiPlayers(boolean isMuliPlayers) {
		List<GhostsBodyImages> ghostBodyImages = Arrays.asList(GhostsBodyImages.values());
		ghostBodyImages.forEach(ghostBodyImage -> ghostBodyImage.setComputed(true));

		if (isMuliPlayers) {
			Utils.randomEnum(GhostsBodyImages.class).setComputed(false);
		}
	}

	@Override
	public void startGame() {
		logger.info("Initialize a new game");
		currentProgramStatus.initNumLevel();
		currentProgramStatus.setGameStart();
	}

	/**
	 * Lancement du timer qui rythme le jeu
	 */
	private void startProgramTimer() {
		logger.info("Start Game Timer");
		programTimer.start();
	}

	/**
	 * FIXME : peut être gérer en fonction de l'état du jeu quand par exemple on est
	 * en intro
	 */
	@Override
	public void startStopSoundActive() {
		logger.info("startStopSoundActive : " + soundActive);
		soundActive = !soundActive;
	}
}
