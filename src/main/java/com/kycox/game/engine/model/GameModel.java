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
package com.kycox.game.engine.model;

import com.kycox.game.tools.Position;
import com.kycox.game.body.GhostsGroup;
import com.kycox.game.body.ghost.Ghost;
import com.kycox.game.body.ladybug.Ladybug;
import com.kycox.game.body.ladybug.LadybugDying;
import com.kycox.game.body.ghost.GhostColor;
import com.kycox.game.constant.contract.GameModelForController;
import com.kycox.game.engine.maze.Maze;
import com.kycox.game.engine.message.GameMessaging;
import com.kycox.game.engine.model.strategy.GameModelManageAction;
import com.kycox.game.engine.model.strategy.ManageActionContext;
import com.kycox.game.engine.score.GroupMessages;
import com.kycox.game.engine.score.Score;
import com.kycox.game.constant.sounds.Sounds;
import com.kycox.game.engine.sound.NewSounds;
import com.kycox.game.engine.sound.SoundDurations;
import com.kycox.game.tools.Utils;
import com.kycox.game.tools.timer.SuperPowerTimer;
import com.kycox.game.tools.timer.TickScheduler;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;


/**
 * Modèle du jeu MVC : l'état de la partie et ses règles, sans aucune horloge.
 *
 * <p>Depuis l'étape 4.1, le modèle ne cadence plus le jeu lui-même : il expose
 * {@link #beat()}, appelé 25 fois par seconde par la boucle du jeu
 * ({@code SwingGameLoop}, avec l'interface) ou directement par un test
 * ({@code GameDriver}). Le modèle ne dépend donc plus de Swing.
 */
@Component
public class GameModel implements GameModelForController {
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
	private final Maze maze;
	/** Mode lampe et style de dessin du niveau (4.5). */
	@Getter
	private final LevelState levelState;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final TickScheduler tickScheduler;
	private final SuperPowerTimer superPowerTimer;
	private final SoundDurations soundDurations;
	/** Durée de la fin de partie, avant le retour à l'écran de présentation. */
	private static final long TIME_ENDING_MILLISECONDS = 1500;

	public GameModel(CurrentProgramStatus currentProgramStatus,
					 GameMessaging gameMessaging,
					 GameModelManageAction gameModelManageAction,
					 Score gameScore,
					 GhostsGroup groupGhosts,
					 GroupMessages groupMessages,
					 Ladybug ladybug,
					 LadybugDying ladybugDying,
					 NewSounds newSounds,
					 Maze maze,
					 LevelState levelState,
					 ManageActionContext manageActionContext,
					 ApplicationEventPublisher applicationEventPublisher,
					 TickScheduler tickScheduler,
					 SuperPowerTimer superPowerTimer,
					 SoundDurations soundDurations
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
		this.maze = maze;
		this.levelState = levelState;
		// pour envoyer aux autres vues l'état courant du jeu
		this.applicationEventPublisher = applicationEventPublisher;
		this.tickScheduler = tickScheduler;
		this.superPowerTimer = superPowerTimer;
		this.soundDurations = soundDurations;
	}

	@PostConstruct
	private void init() {
		currentProgramStatus.setProgramStart();
		gameModelManageAction.changeStrategy(manageActionContext.getNoAction());
		// attentes réglées sur la durée des sons (3.5.3b : lues dans les fichiers, sans carte son)
		ladybugDying.setAgonyDuration(soundDurations.of(Sounds.LADYBUG_IS_DYING));
		setBeginningDuration(soundDurations.of(Sounds.GAME_BEGIN_LEVEL));
		setTimeEndingMilliseconds(TIME_ENDING_MILLISECONDS);
		setEndingLevelMilliseconds(soundDurations.of(Sounds.LADYBUG_INTERMISSION));
	}

	/**
	 * Un battement de la boucle du jeu (toutes les {@code PACE} ms), pattern
	 * <b>Game Loop / Update Method</b> (fiche {@code doc/patterns/game-loop.md}).
	 *
	 * <p>Ordre : les actions différées arrivées à échéance (3.5.1b), le
	 * super-pouvoir (3.5.1c) et les messages affichés (3.5.1d) avancent d'un
	 * battement ; puis l'état courant du jeu joue son battement ; enfin les vues
	 * et les sons sont prévenus.
	 *
	 * <p>Appelé par la boucle du jeu ({@code SwingGameLoop}, étape 4.1), ou
	 * directement par un test (3.5.1e).
	 *
	 * <p>Les sons demandés repartent de zéro à chaque battement (3.5.3a) : un son
	 * n'est demandé qu'au battement où son événement se produit. Avant, certains
	 * états ne vidaient pas la liste, et la musique du début de niveau restait
	 * demandée pendant tout {@code LEVEL_STARTING} (elle pouvait repartir).
	 */
	public void beat() {
		newSounds.initSounds();
		tickScheduler.tick();
		superPowerTimer.tick();
		groupMessages.tick();
		gameModelManageAction.changeStrategy(manageActionContext.getStrategyStatusAction(currentProgramStatus.getGameStatus()));
		gameModelManageAction.execute();
		applicationEventPublisher.publishEvent(new EventGameModel(this, GameSnapshotFactory.of(this)));
	}

	@Override
	public void changeLitLampMode() {
		levelState.toggleLitLampMode();
	}

	@Override
	/**
	 * Arrête la partie en cours (Échap puis O). Ne dépend plus du timer Swing
	 * (3.5.3c) : la règle reste la même en jeu, et devient testable sans écran.
	 */
	public void forceStopGame() {
		logger.info("Force Stop Game");
		gameScore.setOldScore(-1);
		currentProgramStatus.setGameEnd();
		initSounds();
	}

	@Override
	public void askForceEndGame() {
		currentProgramStatus.setGameAskForceEndGame();
	}

	public int getGhostLeftLives() {
		return groupGhosts.getLeftLives();
	}

	/** Ladybug, plus un joueur par fantôme joué par un humain (étape 5.3). */
	public int getNbrPlayers() {
		return 1 + groupGhosts.getHumanGhosts().size();
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
	public void setMultiPlayers(boolean isMuliPlayers) {
		groupGhosts.setAllComputed();
		if (isMuliPlayers) {
			// même tirage qu'avant 4.2 (une couleur parmi quatre, dans le même ordre) :
			// les parties rejouées avec une même graine restent identiques ; depuis 5.4,
			// il désigne un seul fantôme précis
			groupGhosts.chooseHumanGhost(Utils.randomEnum(GhostColor.class)).ifPresent(groupGhosts::setHumanPlayer);
		}
		gameMessaging.addPlayerMode(isMuliPlayers);
	}

	@Override
	public void startGame() {
		logger.info("Initialize a new game");
		currentProgramStatus.setProgramAskKeepPreviousGameLevel();
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

	public void manageGhostCamouflage() {
		groupGhosts.changeGhostCamouflage();
	}

}
