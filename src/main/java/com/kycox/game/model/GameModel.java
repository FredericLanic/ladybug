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
import com.kycox.game.constant.Constants;
import com.kycox.game.contract.IGameModelForController;
import com.kycox.game.contract.IGameModelForGameSounds;
import com.kycox.game.contract.IGameModelForGameView;
import com.kycox.game.level.ScreenData;
import com.kycox.game.model.strategy.GameModelManageAction;
import com.kycox.game.model.strategy.actions.GameModelGameIsEnding;
import com.kycox.game.model.strategy.actions.GameModelGameIsInGame;
import com.kycox.game.model.strategy.actions.GameModelGameIsPlaying;
import com.kycox.game.model.strategy.actions.GameModelGameIsStarting;
import com.kycox.game.model.strategy.actions.GameModelInitialisationProgram;
import com.kycox.game.model.strategy.actions.GameModelLevelIsEnded;
import com.kycox.game.model.strategy.actions.GameModelLevelIsEnding;
import com.kycox.game.model.strategy.actions.GameModelLevelIsStarting;
import com.kycox.game.model.strategy.actions.GameModelNoAction;
import com.kycox.game.model.strategy.actions.GameModelPresentation;
import com.kycox.game.score.GameScore;
import com.kycox.game.score.GroupMessages;
import com.kycox.game.sound.NewSounds;

import lombok.Getter;

/**
 * Modèle du jeu MVC : c'est le modèle qui contient le timer du jeu (coeur du
 * jeu)
 *
 */
@SuppressWarnings("deprecation")
@Named("GameModel")
public class GameModel extends Observable
        implements IGameModelForGameView, IGameModelForGameSounds, IGameModelForController {
	private static final Log logger = LogFactory.getLog(GameModel.class);
	@Inject
	@Getter
	private CurrentGameStatus currentGameStatus;
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
	private GameModelInitialisationProgram gameModelInitialisationProgram;
	@Inject
	private GameModelManageAction gameModelManageAction;
	@Inject
	private GameModelNoAction gameModelNoAction;
	@Inject
	private GameModelPresentation gameModelPresentation;
	@Getter
	@Inject
	private GameScore gameScore;
	private final Timer gameTimer = createGameTimer();
	Point ghostRequest = Constants.POINT_ZERO;
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
	@Getter
	@Inject
	private ScreenData screenData;
	@Getter
	private boolean soundActive = true;

	/*
	 * Remarque de Christophe M : Sur ce gros if, tes conditions sont difficilement
	 * lisibles. Tu pourrais créer une méthode pour chaque test, avec un nom métier
	 * qui exprime la règle métier, et pas la règle technique. Ceci fait, ton
	 * pattern startegy apparaitra plus naturellement. Tu pourras introduire une
	 * enum privée à ta classe, avec les cas de test dessus, les méthodes métier,
	 * écrites par lambda, et tu aurais un bloc qui s'écrit en une ligne.
	 */
	// Workflow du programme :
	// PROGRAM_START
	// PROGRAM_STARTING -> timer puis
	// TO_PRESENTATION : en attente d'action de l'utilisateur
	// GAME_START
	// LEVEL START
	// LEVEL_STARTING -> timer puis
	// IN_GAME
	// LEVEL_END
	// LEVEL_ENDING puis soit LEVEL_START soit GAME_END
	// GAME_END
	// GAME_ENDING -> timer puis
	// TO_PRESENTATION

	private void actionsByTimerBip() {
		switch (currentGameStatus.getGameStatus()) {
			case PROGRAM_START -> gameModelManageAction.changeStrategy(gameModelInitialisationProgram);
			case GAME_PRESENTATION -> gameModelManageAction.changeStrategy(gameModelPresentation);
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

	private Timer createGameTimer() {
		ActionListener action = event -> {
			actionsByTimerBip();
		};
		return new Timer(PACE, action);
	}

	@Override
	public void forceStopGame() {
		if (gameTimer.isRunning()) {
			logger.info("Force Stop Game");
			gameScore.setOldScore(-1);
			currentGameStatus.setGameEnd();
		}
	}

	@Override
	public void gameInPause() {
		if (gameTimer.isRunning()) {
			logger.info("Game in pause");
			gameTimer.stop();
			;
		} else {
			logger.info("Game regoes");
			startGameTimer();
		}
	}

	@Override
	public int getGhostLeftLifes() {
		return groupGhosts.getLeftLives();
	}

	@Override
	public int getNbrPlayers() {
		if (groupGhosts.hasGhostUser()) {
			return 2;
		}
		return 1;
	}

	@PostConstruct
	private void init() {
		currentGameStatus.setProgramStart();
		gameModelManageAction.changeStrategy(gameModelNoAction);
		startGameTimer();
	}

	@Override
	public boolean isGamePresentation() {
		return currentGameStatus.isGamePresentation();
	}

	@Override
	public boolean isInGame() {
		return currentGameStatus.isInGame();
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
		gameModelPresentation.setGhostRequest(ghostRequest);
	}

	@Override
	public void setLadybugRequest(Point point) {
		getLadybug().setUserRequest(point);
	}

	@Override
	public void startGame() {
		logger.info("Start Game");
		currentGameStatus.initNumLevel();
		currentGameStatus.setGameStart();
	}

	/**
	 * Lancement du timer qui rythme le jeu
	 */
	private void startGameTimer() {
		logger.info("Start Game Timer");
		gameTimer.start();
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
