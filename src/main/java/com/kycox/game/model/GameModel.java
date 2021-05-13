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
import com.kycox.game.constant.ghost.GhostStatus;
import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.contract.IGameModelForController;
import com.kycox.game.contract.IGameModelForGameSounds;
import com.kycox.game.contract.IGameModelForGameView;
import com.kycox.game.level.ScreenData;
import com.kycox.game.score.GameScore;
import com.kycox.game.score.GroupMessages;
import com.kycox.game.sound.NewSounds;
import com.kycox.game.timer.SuperPowerTimer;
import com.kycox.game.timer.WaitAndDoActionAfterTimer;
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
public class GameModel extends Observable
        implements IGameModelForGameView, IGameModelForGameSounds, IGameModelForController {
	private static final Log logger = LogFactory.getLog(GameModel.class);
	@Setter
	private long beginningMilliseconds;
	@Getter
	@Inject
	private CurrentGameStatus currentGameStatus;
	@Setter
	private long endingLevelMilliseconds;
	@Getter
	@Inject
	private GameScore gameScore;
	private final Timer gameTimer = createGameTimer();
	@Setter
	private Point ghostRequest = Constants.POINT_ZERO;
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
	@Setter
	private boolean soundActive;
	@Inject
	private SuperPowerTimer superPowerTimer;
	private WaitAndDoActionAfterTimer waitAndDoActionAfterTimer;

	/*
	 * Remarque de Christophe M : Sur ce gros if, tes conditions sont difficilement
	 * lisibles. Tu pourrais créer une méthode pour chaque test, avec un nom métier
	 * qui exprime la règle métier, et pas la règle technique. Ceci fait, ton
	 * pattern startegy apparaitra plus naturellement. Tu pourras introduire une
	 * enum privée à ta classe, avec les cas de test dessus, les méthodes métier,
	 * écrites par lambda, et tu aurais un bloc qui s'écrit en une ligne.
	 */
	private void actionsByTimerBip() { // voir pattern strategie pour supprimer les if then else
		if (currentGameStatus.isProgramStart()) {
			programIsStarting();
		} else if (currentGameStatus.isGamePresentation()) {
			ghostsIsMovingInPresentation();
		} else if (currentGameStatus.isGameStart()) {
			gameIsStarting();
		} else if (currentGameStatus.isLevelStart()) {
			levelIsStarting();
		} else if (currentGameStatus.isLevelStarting()
		        || currentGameStatus.isProgramStarting() /* , isGameStarting, isGameEnding */) {
			// waiting
		} else if (currentGameStatus.isInGame() && ladybug.getStatus() == LadybugStatus.DEAD) {
			ladybugIsDead();
		} else if (currentGameStatus.isInGame() && ladybug.getStatus() == LadybugStatus.DYING) {
			ladybugIsDying();
			// moveGhosts();
		} else if (currentGameStatus.isInGame() && groupGhosts.userGhostHasLife()) {
			currentGameStatus.setGameEnd();
		} else if (currentGameStatus.isInGame()) {
			gameIsPlaying();
		} else if (currentGameStatus.isLevelEnd()) {
			levelIsEnding();
		} else if (currentGameStatus.isLevelEnding()) {
			setSoundActive(true);
			setSoundRequests();
		} else if (currentGameStatus.isGameEnd()) {
			gameIsEnding();
		}
		setChanged();
		notifyObservers();
	}

	private void caseOfGhostEatLadybug() {
		if (groupGhosts.eatLadybug()) {
			ladybug.setStatus(LadybugStatus.DYING);
			ladybugDying.initBip();
			groupGhosts.manageNewLife();
		}
	}

	private void caseOfLadybugEatAMegaPoint() {
		if (ladybug.isEatenAMegaPoint()) {
			logger.info("Ladybug has just eaten a mega point");
			runSuperPowerTimer();
			groupGhosts.setFear(true);
		}
	}

	private void caseOfNewLadybugLife() {
		ladybug.manageNewLife();
	}

	private void checkEndMaze() {
		// Niveau terminé
		if (screenData.getNbrBlocksWithPoint() == 0) {
			gameScore.addScore(Constants.SCORE_END_LEVEL);
			gameScore.initIncrementScore();
			currentGameStatus.setLevelEnd();
		}
	}

	/**
	 * Paramêtrage des fantômes dans le niveau actuel
	 *
	 * Initialisation des directions et de la vue de ladybug
	 *
	 * suite de l'initialisation du niveau ou quand ladybug meurt
	 */
	private void continueLevel() {
		// initialise ladybug pour le niveau
		ladybug.setStartLevel(currentGameStatus.getNumLevel(), screenData.getInitLadybugPos());
		// initialise les fantômes
		groupGhosts.setStartLevel(currentGameStatus.getNumLevel(), screenData);
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
			currentGameStatus.setProgramStart();
		}
	}

	/**
	 * Jeu en pause
	 */
	@Override
	public void gameInPause() {
		if (gameTimer.isRunning()) {
			logger.info("Game in pause");
			stopGameTimer();
		} else {
			logger.info("Game regoes");
			startGameTimer();
		}
	}

	private void gameIsEnding() {
		currentGameStatus.setGameEnding();
		waitAndDoActionAfterTimer = new WaitAndDoActionAfterTimer();
		waitAndDoActionAfterTimer.launch(beginningMilliseconds, currentGameStatus, CurrentGameStatus.TO_PROGRAM_START);
	}

	private void gameIsPlaying() {
		// ***
		caseOfNewLadybugLife();
		// ***
		setBodiesActions();
		// ***
		updateGhostSeetings();
		// ***
		caseOfGhostEatLadybug();
		// ***
		manageSuperPower();
		// ***
		caseOfLadybugEatAMegaPoint();
		// ***
		manageScores();
		// ***
		updateScreenBlock();
		// ***
		setSoundRequests();
		// ***
		moveBodies();
		// ***
		checkEndMaze(); // FIXME : sortir ce test de cette boucle
	}

	private void gameIsStarting() {
		currentGameStatus.setGameStarting();
		waitAndDoActionAfterTimer = new WaitAndDoActionAfterTimer();
		waitAndDoActionAfterTimer.launch(2500, currentGameStatus, CurrentGameStatus.TO_LEVEL_START);
		setSoundRequests();
	}

	@Override
	public int getGhostLeftLifes() {
		return groupGhosts.getLeftLives();
	}

	/**
	 * Retourne le nombre de joueurs : 1 ou 2
	 *
	 * @return
	 */
	@Override
	public int getNbrPlayers() {
		if (groupGhosts.hasNotComputedGhost()) {
			return 2;
		}
		return 1;
	}

	private void ghostsIsMovingInPresentation() {
		setBodiesActions();
		moveBodies();
		setSoundActive(true); // à supprimer ?
		setSoundRequests();
	}

	@PostConstruct
	private void init() {
		currentGameStatus.setProgramStart();
		startGameTimer();
	}

	/**
	 * Initialise les variables au lancement du programme
	 */
	private void initGame() {
		logger.info("Initialize game");
		// currentGameStatus.setGamePresentation();
		// initialisation du numéro du niveau; incrémenté dans initLevel
		currentGameStatus.setNumLevel(1);
		// utilisé juste pour l'affichage de la fenêtre d'initialisation
		screenData.setLevelMap(currentGameStatus.getNumLevel(), currentGameStatus.isInGame());
		// 3 vies par défaut
		ladybug.setLeftLifes(Constants.NBR_INIT_LIFE);
		// ladybug n'est pas en vie lors de l'initialisation du jeu
		ladybug.setStatus(LadybugStatus.DEAD);
		// initialise les scores
		gameScore.init();
		// Initialise le groupe de fantôme
		groupGhosts.setNumLevel(Constants.PRESENTATION_LEVEL);
		// mise de la vitesse du niveau 3 pour la présentation
		groupGhosts.setInitSpeeds(Constants.PRESENTATION_LEVEL);
		// initialise les positions des fantômes
		groupGhosts.initializePositions(screenData);
		// initialise les fantômes pour la présentation
		groupGhosts.setStatus(GhostStatus.NORMAL);
		// initialise les vies de fantômes
		groupGhosts.setLeftLifes(Constants.NBR_INIT_LIFE);
	}

	/**
	 * Initialise le niveau en fonction du niveau précédent
	 */
	private void initLevel() {
		logger.info("Initialize level");
		// suppression des composants techniques du niveau précédent
		removePreviousLevelTasks();
		// currentGameStatus.setLevelStart();
		currentGameStatus.setGameStart();
		// incrémente le numéro du niveau
		currentGameStatus.setNumLevel(currentGameStatus.getNumLevel() + 1);
		// recopie les paramètres du niveau dans les données flottantes du niveau
		screenData.setLevelMap(currentGameStatus.getNumLevel(), true);
		// initialisation du super power
		groupGhosts.setFear(false);
		// son activé
		setSoundActive(true);
		// ladybug est vivant
		ladybug.setStatus(LadybugStatus.NORMAL);
		// on continue le level
		continueLevel();
	}

	@Override
	public boolean isGamePresentation() {
		return currentGameStatus.isGamePresentation();
	}

	@Override
	public boolean isInGame() {
		return getCurrentGameStatus().isInGame();
	}

	/**
	 * Ladybug a rencontré un fantôme !! Le jeu est terminé si toutes les vies de
	 * ladybug ont été utilisées.
	 */
	private void ladybugIsDead() {
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

	/**
	 * Cinématique de la mort de ladybug
	 */
	private void ladybugIsDying() {
		ladybugDying.inProgress();
		if (ladybugDying.isInPogress()) {
			newSounds.initSounds();
		}
		if (ladybugDying.isEnd()) {
			ladybug.setStatus(LadybugStatus.DEAD);
			ladybugDying.initBip();
		}
	}

	private void levelIsEnding() {
		setSoundActive(false);
		currentGameStatus.setLevelEnding();
		waitAndDoActionAfterTimer = new WaitAndDoActionAfterTimer();
		waitAndDoActionAfterTimer.launch(endingLevelMilliseconds, currentGameStatus, CurrentGameStatus.TO_LEVEL_START);
	}

	private void levelIsStarting() {
		initLevel();
		currentGameStatus.setLevelStarting();
		waitAndDoActionAfterTimer = new WaitAndDoActionAfterTimer();
		waitAndDoActionAfterTimer.launch(beginningMilliseconds, currentGameStatus, CurrentGameStatus.TO_INGAME);
		setSoundRequests();
	}

	private void manageScores() {
		gameScore.setScore(groupGhosts, ladybug, groupMessages);
		groupMessages.removeIfDying();
		// ***
		if (gameScore.getIncrementScore() >= Constants.NEW_LIFE_BY_SCORE) {
			logger.info("New life for Ladybug");
			gameScore.initIncrementScore();
			ladybug.setNewLife(true);
		}
	}

	private void manageSuperPower() {
		if (superPowerTimer.isStopping()) {
			groupGhosts.setFlashActive();
		} else if (superPowerTimer.isStopped()) {
			groupGhosts.setFear(false);
		}
	}

	private void moveBodies() {
		ladybug.move(screenData);
		moveGhosts();
	}

	private void moveGhosts() {
		groupGhosts.move(screenData, ladybug, ghostRequest);
	}

	private void programIsStarting() {
		initGame();
		setSoundActive(false);
		currentGameStatus.setProgramStarting();
		waitAndDoActionAfterTimer = new WaitAndDoActionAfterTimer();
		waitAndDoActionAfterTimer.launch(Constants.PROGRAM_STARTING_MILLISECONDS, currentGameStatus,
		        CurrentGameStatus.TO_PRESENTATION);
		// le status passera à GAME_PRESENTATION
	}

	/**
	 * Suppression des tâches du niveau qui est terminé (succès, échec ou pas de
	 * niveau).
	 */
	private void removePreviousLevelTasks() {
		// arrêt des timers super power
		superPowerTimer.forcedStop();
	}

	/**
	 * Lancement du timer pour le super power de ladybug
	 */
	private void runSuperPowerTimer() {
		logger.info("Super power is up");
		// on force l'arrêt des timers qui ont pu être démarrés dans la partie
		// précédente
		superPowerTimer.forcedStop();
		// on lance le timer
		// FIXME : Il serait bien de d'apporter une notion du temps en fonction du
		// niveau
		superPowerTimer.launch(Constants.NBR_SECONDS_SUPER_POWER);
	}

	private void setBodiesActions() {
		ladybug.setActions(screenData);
		groupGhosts.setActions(ladybug);
	}

	@Override
	public void setLadybugRequest(Point point) {
		getLadybug().setUserRequest(point);
	}

	/**
	 * Ajoute des sons en fonction de l'état des fantômes et de ladybug
	 */
	private void setSoundRequests() {
		// initialise le sons
		newSounds.initSounds();
		newSounds.addGameBeginLevel(currentGameStatus.isLevelStarting());
		newSounds.addIntermission(currentGameStatus.isGamePresentation() && Utils.generateRandomInt(1000) > 997
		        || currentGameStatus.isLevelEnding());
		newSounds.addScaredGhost(groupGhosts.hasScaredGhost());
		newSounds.addRegeneratedGhost(groupGhosts.hasRegeneratedGhost());
		newSounds.addDyingGhost(groupGhosts.hasDyingGhost());
		newSounds.addLadybugEatGhost(groupGhosts.getNbrEatenGhost() > 0);
		newSounds.addLadybugEatenAPoint(ladybug.isEatenAPoint() && currentGameStatus.isInGame());
		newSounds.addNewLife(ladybug.isNewLife());
		newSounds.addTeleport(ladybug.isToBeTeleported());
		newSounds.addSirenSound(ladybug.getStatus() == LadybugStatus.NORMAL && currentGameStatus.isInGame(),
		        screenData.getPercentageEatenPoint());
		newSounds.addLadybugIsDying(ladybug.getStatus() == LadybugStatus.DYING, !ladybugDying.isInPogress());
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

	public void stopGameTimer() {
		logger.info("Stop Game Timer");
		gameTimer.stop();
	}

	private void updateGhostSeetings() {
		groupGhosts.updateSeetings(currentGameStatus.getNumLevel(), screenData.getPercentageEatenPoint());
	}

	private void updateScreenBlock() {
		screenData.updateScreenBlock(ladybug);
	}
}
