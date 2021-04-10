/**
  Copyright (C) 2019  Frédéric Lanic frederic.lanic@outlook.fr

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
import com.kycox.game.properties.GameProperties;
import com.kycox.game.score.GameScore;
import com.kycox.game.score.GroupIncrementScores;
import com.kycox.game.sound.NewSounds;
import com.kycox.game.timer.SuperPowerTimer;

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
	private static final Log	 logger		   = LogFactory.getLog(GameModel.class);
	@Getter
	@Setter
	private boolean				 beginNewLevel = false;
	@Getter
	@Inject
	private CurrentGameStatus	 currentGameStatus;
	@Getter
	@Inject
	private GameScore			 gameScore;
	private final Timer			 gameTimer	   = createGameTimer();
	@Setter
	private Point				 ghostRequest  = Constants.POINT_ZERO;
	@Getter
	@Inject
	private GhostsGroup			 groupGhosts;
	@Getter
	@Inject
	private GroupIncrementScores groupIncrementScores;
	@Getter
	@Inject
	private Ladybug				 ladybug;
	@Getter
	@Inject
	private LadybugDying		 ladybugDying;
	@Getter
	@Inject
	private NewSounds			 newSounds;
	@Getter
	@Inject
	private ScreenData			 screenData;
	@Getter
	@Setter
	private boolean				 soundActive;
	@Inject
	private SuperPowerTimer		 superPowerTimer;	

	@Override
	public void forceStopGame() {
		if (gameTimer.isRunning()) {
			logger.info("Force Stop Game");
			gameScore.setOldScore(-1);
			initGame();
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

	@Override
	public void startGame() {
		logger.info("Start Game");
		initGame();
		initLevel();
	}

	/**
	 * Lancement du timer qui rythme le jeu FIXME : cette fonction reste public car
	 * elle est toujouts utilisée par GameSounds -> c'est le mal
	 */
	@Override
	public void startGameTimer() {
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

	@Override
	public void stopGameTimer() {
		logger.info("Stop Game Timer");
		gameTimer.stop();
	}
	
	public void setLadybugRequest(Point point) {
		getLadybug().setUserRequest(point);
	}

	public boolean isInGame() {
		return getCurrentGameStatus().isInGame();
	}
	
	private void actionsByTimerBip() { // voir pattern strategie pour supprimer les if then else		
		if (isInGame() && LadybugStatus.DEAD.equals(ladybug.getStatus())) {
			ladybugIsDead();
		} else if (isInGame() && LadybugStatus.DYING.equals(ladybug.getStatus())) {
			ladybugIsDying();
			moveGhosts();
		} else if (isInGame() && groupGhosts.userIsDead()) {
			currentGameStatus.setNoGame();
		} else {
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
		setChanged();
		notifyObservers();
	}
	
	private void updateScreenBlock() {
		screenData.updateScreenBlock(ladybug);
	}
	
	private void caseOfNewLadybugLife() {
		ladybug.manageNewLife();	
	}
	
	private void updateGhostSeetings() {
		groupGhosts.updateSeetings(currentGameStatus.getNumLevel(), screenData.getPercentageEatenPoint());
	}
	
	private void moveBodies() {
		ladybug.move(screenData);
		moveGhosts();
	}
	
	private void moveGhosts() {
		groupGhosts.move(screenData, ladybug, ghostRequest);
	}
	
	private void manageScores() {
		gameScore.setScore(groupGhosts, ladybug, groupIncrementScores);
		groupIncrementScores.removeIfDying();
		// ***
		if (gameScore.getIncrementScore() >= Constants.NEW_LIFE_BY_SCORE) {
			logger.info("New life for Ladybug");
			gameScore.setIncrementScore(0);
			ladybug.setNewLife(true);
		}
	}

	private void caseOfLadybugEatAMegaPoint() {
		if (ladybug.isEatenAMegaPoint()) {
			logger.info("Ladybug has just eaten a mega point");
			runSuperPowerTimer();
			groupGhosts.setFear(true);
		}
	}
	
	private void caseOfGhostEatLadybug() {
		if (groupGhosts.eatLadybug()) {
			ladybug.setStatus(LadybugStatus.DYING);
			ladybugDying.initBip();
			groupGhosts.manageNewLife();
		}
	}

	
	private void setBodiesActions() {
		ladybug.setActions(screenData);
		groupGhosts.setActions(ladybug);
	}
	
	private void checkEndMaze() {
		// Niveau terminé
		if (screenData.getNbrBlocksWithPoint() == 0) {
			gameScore.addScore(Constants.SCORE_END_LEVEL);
			initLevel();
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

	@PostConstruct
	private void init() {
		soundActive = false;
		initGame();
		startGameTimer();
	}

	/**
	 * Initialise les variables au lancement du programme
	 */
	private void initGame() {
		logger.info("Initialize game");
		currentGameStatus.setNoGame();
		// initialisation du numéro du niveau; incrémenté dans initLevel
		currentGameStatus.setNumLevel(1);
		// utilisé juste pour l'affichage de la fenêtre d'initialisation
		screenData.setLevelMap(currentGameStatus.getNumLevel(), isInGame());
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
		// active sound
		setSoundActive(true);
	}

	/**
	 * Initialise le niveau en fonction du niveau précédent
	 */
	private void initLevel() {
		logger.info("Initialize level");
		// suppression des composants techniques du niveau précédent
		removePreviousLevelTasks();
		currentGameStatus.setInGame();
		// incrémente le numéro du niveau
		currentGameStatus.setNumLevel(currentGameStatus.getNumLevel() + 1);
		// recopie les paramètres du niveau dans les données flottantes du niveau
		screenData.setLevelMap(currentGameStatus.getNumLevel(), isInGame());
		// initialisation du super power
		groupGhosts.setFear(false);
		// début du level : utile pour le son du jingle
		setBeginNewLevel(true);
		// on continue le level
		continueLevel();
	}

	/**
	 * Ladybug a rencontré un fantôme !! Le jeu est terminé si toutes les vies de
	 * ladybug ont été utilisées.
	 */
	private void ladybugIsDead() {
		logger.info("Ladybug is dead");
		ladybug.minusLifesLeft();
		// test fin du jeu
		if (ladybug.getLeftLifes() == 0) {
			logger.info("Ladybug lost the game");
			gameScore.setOldScore(gameScore.getScore());
			// FIXME : à mettre ailleurs
			initGame();
		} else {
			continueLevel();
		}
	}

	/**
	 * Cinématique de la mort de ladybug
	 */
	private void ladybugIsDying() {
		logger.info("Ladybug is dying");
		//
		ladybugDying.inProgress();
		if (ladybugDying.isInPogress()) {
			newSounds.initSounds();
		}
		if (ladybugDying.isEnd()) {
			ladybug.setStatus(LadybugStatus.DEAD);
			ladybugDying.initBip();
		}
	}

	private void manageSuperPower() {
		if (superPowerTimer.isStopping()) {
			groupGhosts.setFlashActive();
		} else if (superPowerTimer.isStopped()) {
			groupGhosts.setFear(false);
		}
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
		logger.info("Super power for ladybug");
		// on force l'arrêt des timers qui ont pu être démarrés dans la partie
		// précédente
		superPowerTimer.forcedStop();
		// on lance le timer
		// FIXME : Il serait bien de d'apporter une notion du temps en fonction du
		// niveau
		superPowerTimer.launch(Constants.NBR_SECONDS_SUPER_POWER);
	}

	/**
	 * Ajoute des sons en fonction de l'état des fantômes et de ladybug
	 */
	private void setSoundRequests() {
		// initialise le sons
		newSounds.initSounds();
		newSounds.addGameBeginLevel(currentGameStatus.isLevelBegin());
		newSounds.addScaredGhost(groupGhosts.hasScaredGhost());
		newSounds.addRegeneratedGhost(groupGhosts.hasRegeneratedGhost());
		newSounds.addDyingGhost(groupGhosts.hasDyingGhost());
		newSounds.addLadybugEatGhost(groupGhosts.getNbrEatenGhost() > 0);
		newSounds.addLadybugEatenAPoint(ladybug.isEatenAPoint());
		newSounds.addNewLife(ladybug.isNewLife());
		newSounds.addTeleport(ladybug.isToBeTeleported());
		newSounds.addSirenSound(ladybug.getStatus().equals(LadybugStatus.NORMAL), screenData.getPercentageEatenPoint());
		newSounds.addLadybugIsDying(ladybug.getStatus().equals(LadybugStatus.DYING), !ladybugDying.isInPogress());
	}
}
