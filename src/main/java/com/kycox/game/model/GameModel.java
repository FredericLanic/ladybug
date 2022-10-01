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

import com.kycox.game.body.ghost.Ghost;
import com.kycox.game.body.ghost.GhostsGroup;
import com.kycox.game.body.ladybug.Ladybug;
import com.kycox.game.body.ladybug.LadybugDying;
import com.kycox.game.constant.ghost.image.GhostsBodyImages;
import com.kycox.game.contract.GameModelForController;
import com.kycox.game.contract.GameModelForSounds;
import com.kycox.game.contract.GameModelForViews;
import com.kycox.game.level.ScreenData;
import com.kycox.game.message.GameMessages;
import com.kycox.game.message.GameMessaging;
import com.kycox.game.model.strategy.GameModelManageAction;
import com.kycox.game.model.strategy.actions.*;
import com.kycox.game.score.GroupMessages;
import com.kycox.game.score.Score;
import com.kycox.game.sound.NewSounds;
import com.kycox.game.tools.Utils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Optional;

import static com.kycox.game.constant.Constants.PACE;

/**
 * Modèle du jeu MVC : c'est le modèle qui contient le timer du jeu (coeur du
 * jeu)
 *
 */
@SuppressWarnings("deprecation")
@Component
public class GameModel extends Observable implements GameModelForViews, GameModelForSounds, GameModelForController {
	private static final Log logger = LogFactory.getLog(GameModel.class);
	@Setter
	@Getter
	private boolean atLeastOneXboxOneConnected;
	@Getter
	private final CurrentProgramStatus currentProgramStatus;
	@Getter
	private final GameMessaging gameMessaging;
	private final GameModelGameIsEnding gameModeGameIsEnding;
	private final GameModelGameIsPlaying gameModeGameIsPlaying;
	private final GameModelGameIsStarting gameModeGameStarting;
	private final GameModelLevelIsEnding gameModeLevelIsEnding;
	private final GameModelLevelIsEnded gameModeLevelIsEnds;
	private final GameModelLevelIsStarting gameModeLevelStarting;
	private final GameModelGameIsInGame gameModelGameIsInGame;
	private final GameModelManageAction gameModelManageAction;
	private final GameModelNoAction gameModelNoAction;
	private final GameModelPresentation gameModelPresentation;
	private final GameModelPresentationStarting gameModelPresentationStarting;
	private final GameModelProgramStarting gameModelProgramStarting;
	@Getter
	private final Score gameScore;
	@Getter
	private final GhostsGroup groupGhosts;
	@Getter
	private final GroupMessages groupMessages;
	@Getter
	private final Ladybug ladybug;
	@Getter
	private final LadybugDying ladybugDying;
	@Getter
	private final NewSounds newSounds;
	@Getter
	private final ScreenData screenData;
	@Getter
	@Setter
	private boolean showHelpForKeys = false;
	@Getter
	@Setter
	private boolean showHelpForXboxes = false;
	@Getter
	private boolean soundActive = true;
	private final Timer programTimer = createProgramTimer();

	@Autowired
	public GameModel(CurrentProgramStatus currentProgramStatus,
					 GameMessaging gameMessaging,
					 GameModelGameIsEnding gameModeGameIsEnding,
					 GameModelGameIsPlaying gameModeGameIsPlaying,
					 GameModelGameIsStarting gameModeGameStarting,
					 GameModelLevelIsEnding gameModeLevelIsEnding,
					 GameModelLevelIsEnded gameModeLevelIsEnds,
					 GameModelLevelIsStarting gameModeLevelStarting,
					 GameModelGameIsInGame gameModelGameIsInGame,
					 GameModelManageAction gameModelManageAction,
					 GameModelNoAction gameModelNoAction,
					 GameModelPresentation gameModelPresentation,
					 GameModelPresentationStarting gameModelPresentationStarting,
					 GameModelProgramStarting gameModelProgramStarting,
					 Score gameScore,
					 GhostsGroup groupGhosts,
					 GroupMessages groupMessages,
					 Ladybug ladybug,
					 LadybugDying ladybugDying,
					 NewSounds newSounds,
					 ScreenData screenData) {
		this.currentProgramStatus = currentProgramStatus;
		this.gameMessaging = gameMessaging;
		// états du jeu
		this.gameModeGameIsEnding = gameModeGameIsEnding;
		this.gameModeGameIsPlaying = gameModeGameIsPlaying;
		this.gameModeGameStarting = gameModeGameStarting;
		this.gameModeLevelIsEnding = gameModeLevelIsEnding;
		this.gameModeLevelIsEnds = gameModeLevelIsEnds;
		this.gameModeLevelStarting = gameModeLevelStarting;
		this.gameModelGameIsInGame = gameModelGameIsInGame;
		this.gameModelManageAction = gameModelManageAction;
		this.gameModelNoAction = gameModelNoAction;
		this.gameModelPresentation = gameModelPresentation;
		this.gameModelPresentationStarting = gameModelPresentationStarting;
		this.gameModelProgramStarting = gameModelProgramStarting;
		//
		this.gameScore = gameScore;
		this.groupGhosts = groupGhosts;
		this.groupMessages = groupMessages;
		this.ladybug = ladybug;
		this.ladybugDying = ladybugDying;
		this.newSounds = newSounds;
		this.screenData = screenData;
	}

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
		setChanged();
		notifyObservers();
	}

	@Override
	public void changeLitLampMode() {
		screenData.setLitLampMode(!screenData.isLitLampMode());
	}

	private Timer createProgramTimer() {
		ActionListener action = event -> actionsByTimerBip();
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

	@Override
	public Optional<Ghost> getUnComputedGhost() {
		return getGroupGhosts().getGhosts().stream().filter(g -> !g.isComputed()).findFirst();
	}

	@PostConstruct
	private void init() {
		currentProgramStatus.setProgramStart();
		gameModelManageAction.changeStrategy(gameModelNoAction);
		programTimer.start();
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

	@Override
	public void programForceExit() {
		System.exit(0);
	}

	public void setBeginningMilliseconds(long beginningMilliseconds) {
		gameModeGameIsEnding.setTimeEnding(beginningMilliseconds);
		gameModeLevelStarting.setBeginningMilliseconds(beginningMilliseconds);
	}

	public void setEndingLevelMilliseconds(long endingLevelMilliseconds) {
		gameModeLevelIsEnds.setEndingLevelMilliseconds(endingLevelMilliseconds);
	}

	@Override
	public void setGameInPause() {
		currentProgramStatus.setGameInPause(!currentProgramStatus.isGameInPause());
		gameMessaging.put(currentProgramStatus.isGameInPause() ? GameMessages.GAME_IN_PAUSE.getMessage()
		        : GameMessages.HEY_WE_GO.getMessage());
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
			gameMessaging.put(GameMessages.TWO_PLAYERS_MODE.getMessage());
		} else {
			gameMessaging.put(GameMessages.ONE_PLAYER_MODE.getMessage());
		}
	}

	@Override
	public void startGame() {
		logger.info("Initialize a new game");
		currentProgramStatus.initNumLevel();
		currentProgramStatus.setGameStart();
	}

	@Override
	public void startStopSoundActive() {
		logger.info("startStopSoundActive : " + soundActive);
		soundActive = !soundActive;
		gameMessaging.put(soundActive ? GameMessages.SOUND_ACTIVE.getMessage() : GameMessages.SOUND_OFF.getMessage());
	}
}
