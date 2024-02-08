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

import com.kycox.game.body.GhostsGroup;
import com.kycox.game.body.ghost.Ghost;
import com.kycox.game.body.ladybug.Ladybug;
import com.kycox.game.body.ladybug.LadybugDying;
import com.kycox.game.constant.ghost.GhostsBodyImages;
import com.kycox.game.contract.GameModelForController;
import com.kycox.game.contract.GameModelForSounds;
import com.kycox.game.contract.GameModelForViews;
import com.kycox.game.level.ScreenData;
import com.kycox.game.message.GameMessaging;
import com.kycox.game.model.strategy.GameModelManageAction;
import com.kycox.game.model.strategy.ManageActionContext;
import com.kycox.game.score.GroupMessages;
import com.kycox.game.score.Score;
import com.kycox.game.sound.NewSounds;
import com.kycox.game.tools.Utils;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.kycox.game.constant.GameMainConstants.PACE;

/**
 * Modèle du jeu MVC : c'est le modèle qui contient le timer du jeu (coeur du jeu)
 */
@Component
public class GameModel implements GameModelForViews, GameModelForSounds, GameModelForController {
	private static final Logger logger = LoggerFactory.getLogger(GameModel.class);
	@Getter
	private final CurrentProgramStatus currentProgramStatus;
	@Getter
	private final GameMessaging gameMessaging;
	private final GameModelManageAction gameModelManageAction;
	private final ManageActionContext manageActionContext;
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
	@Setter
	@Getter
	private boolean atLeastOneXboxOneConnected;
	@Getter
	boolean debugMode = false;
	private final ApplicationEventPublisher applicationEventPublisher;

	public GameModel(CurrentProgramStatus currentProgramStatus,
					 GameMessaging gameMessaging,
					 GameModelManageAction gameModelManageAction,
					 Score gameScore,
					 GhostsGroup groupGhosts,
					 GroupMessages groupMessages,
					 Ladybug ladybug,
					 LadybugDying ladybugDying,
					 NewSounds newSounds,
					 ScreenData screenData,
					 ManageActionContext manageActionContext,
					 ApplicationEventPublisher applicationEventPublisher
	) {
		this.currentProgramStatus = currentProgramStatus;
		this.gameMessaging = gameMessaging;
		this.gameModelManageAction = gameModelManageAction;
		this.manageActionContext = manageActionContext;
		this.gameScore = gameScore;
		this.groupGhosts = groupGhosts;
		this.groupMessages = groupMessages;
		this.ladybug = ladybug;
		this.ladybugDying = ladybugDying;
		this.newSounds = newSounds;
		this.screenData = screenData;
		// pour envoyer aux autres vues l'état courant du jeu
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@PostConstruct
	private void init() {
		currentProgramStatus.setProgramStart();
		gameModelManageAction.changeStrategy(manageActionContext.getNoAction());
		programTimer.start();
	}

	private void actionsByTimerBip() {
		gameModelManageAction.changeStrategy(manageActionContext.getStrategyStatusAction(currentProgramStatus.getGameStatus()));
		gameModelManageAction.execute();
		applicationEventPublisher.publishEvent(new EventGameModel(this));
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
	public void askForceEndGame() {
		currentProgramStatus.setGameAskForceEndGame();
	}

	@Override
	public int getGhostLeftLives() {
		return groupGhosts.getLeftLives();
	}

	@Override
	public int getNbrPlayers() {
		if (groupGhosts.hasGhostUser()) {
			return 2;
		}
		return 1;
	}

	@Override
	public Optional<Ghost> getUnComputedGhost() {
		return groupGhosts.getGhosts().stream().filter(g -> !g.isComputed()).findFirst();
	}

	public void initSounds() {
		newSounds.initSounds();
	}

	@Override
	public boolean isProgramPresentation() {
		return currentProgramStatus.isProgramPresentation();
	}

	@Override
	public boolean isProgramAskKeepPreviousGameLevel() {
		return currentProgramStatus.isProgramAskKeepPreviousGameLevel();
	}

	@Override
	public boolean isInGame() {
		return currentProgramStatus.isInGame();
	}

	@Override
	public void setInGame() {
		currentProgramStatus.setInGame();
	}

	@Override
	public void programForceExit() {
		System.exit(0);
	}

	@Override
	public boolean isGameAskForceEndGame() {
		return currentProgramStatus.isGameAskForceEndGame();
	}

	public void setBeginningDuration(long beginningDuration) {
		manageActionContext.setBeginningDuration(beginningDuration);
	}

	public void setTimeEndingMilliseconds(long timeEndingMilliseconds) {
		manageActionContext.setTimeEndingMilliseconds(timeEndingMilliseconds);
	}

	public void setEndingLevelMilliseconds(long endingLevelMilliseconds) {
		manageActionContext.setEndingLevelMilliseconds(endingLevelMilliseconds);
	}

	@Override
	public void setGameInPause() {
		currentProgramStatus.setGameInPause(!currentProgramStatus.isGameInPause());
		gameMessaging.addGameInPause(currentProgramStatus.isGameInPause());
	}

	@Override
	public void setGhostRequest(Point ghostRequest) {
		manageActionContext.setGhostRequest(ghostRequest);
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
		gameMessaging.addPlayerMode(isMuliPlayers);
	}

	@Override
	public void startGame() {
		logger.info("Initialize a new game");
		currentProgramStatus.setProgramAskKeepPreviousGameLevel();
	}

	@Override
	public void startStopSoundActive() {
		logger.info("startStopSoundActive : " + soundActive);
		soundActive = !soundActive;
		gameMessaging.addSoundMode(soundActive);
	}

	@Override
	public void initNumLevel() {
		currentProgramStatus.initNumLevel();
	}

	@Override
	public void initializeLevelNumAndStartGame(boolean resetLevelNum) {
		if (resetLevelNum) {
			initNumLevel();
		}
		currentProgramStatus.getStoredNumLevel();
		currentProgramStatus.setGameStart();
	}

	public void changeDebugMode() {
		debugMode = !debugMode;
	}

	public void manageGhostCamouflage() {
		groupGhosts.changeGhostCamouflage();
	}

}
