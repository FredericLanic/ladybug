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
import com.kycox.game.constant.Constants;
import com.kycox.game.constant.ghost.GhostStatusEnum;
import com.kycox.game.constant.ladybug.KinematicLadybugDeath;
import com.kycox.game.constant.ladybug.LadybugStatusEnum;
import com.kycox.game.contract.IGameModelForControleur;
import com.kycox.game.contract.IGameModelForGameSounds;
import com.kycox.game.contract.IGameModelForGameView;
import com.kycox.game.level.ScreenData;
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
        implements IGameModelForGameView, IGameModelForGameSounds, IGameModelForControleur {
	private static final Log	  logger		= LogFactory.getLog(GameModel.class);
	@Getter
	@Setter
	private boolean				  beginNewLevel	= false;
	@Getter
	@Inject
	private GameScore			  gameScore;
	@Getter
	@Inject
	private GameStatus			  gameStatus;
	private final Timer			  gameTimer		= createTimer();
	@Setter
	private Point				  ghostRequest	= Constants.POINT_ZERO;
	@Getter
	@Inject
	private GhostsGroup			  groupGhosts;
	@Getter
	@Inject
	private GroupIncrementScores  groupIncrementScores;
	@Getter
	@Inject
	private KinematicLadybugDeath kinematicLadybugDeath;
	@Getter
	@Inject
	private Ladybug				  ladybug;
	@Getter
	@Inject
	private NewSounds			  newSounds;
	@Getter
	@Inject
	private ScreenData			  screenData;
	@Getter
	@Setter
	private boolean				  soundActive	= false;
	@Inject
	private SuperPowerTimer		  superPowerTimer;

	@Override
	public void forceStopGame() {
		if (gameTimer.isRunning()) {
			gameScore.setOldScore(-1);
			initGame();
		}
	}

	/**
	 * Jeu en pause
	 */
	@Override
	public void gameInPause() {
		if (gameTimer.isRunning())
			stopGameTimer();
		else
			startGameTimer();
	}

	/**
	 * Retourne le nombre de joueurs : 1 ou 2
	 *
	 * @return
	 */
	@Override
	public int getNbrPlayers() {
		if (groupGhosts.getGhostNotComputed() == null) {
			return 1;
		}
		return 2;
	}

	@Override
	public void startGame() {
		initGame();
		initLevel();
	}

	/**
	 * Lancement du timer qui rythme le jeu FIXME : cette fonction reste public car
	 * elle est toujouts utilisée par GameSounds -> c'est le mal
	 */
	@Override
	public void startGameTimer() {
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
		gameTimer.stop();
	}

	private void actionsByTimerBip() { // voir pattern strategie pour supprimer les if then else
		if (gameStatus.isInGame() && LadybugStatusEnum.DEAD.equals(ladybug.getStatus())) {
			ladybugIsDead();
		} else if (gameStatus.isInGame() && LadybugStatusEnum.DYING.equals(ladybug.getStatus())) {
			ladybugIsDying();
		} else if (gameStatus.isInGame() && groupGhosts.userIsDead()) {
			gameStatus.setNoGame();
		} else {
//Récupération des actions de chacun
			ladybug.setActions(screenData);
			groupGhosts.setActions(ladybug);
//GESTION DE L'ETAT DES FANTOMES
			groupGhosts.updateSeetings(gameStatus.getNumLevel(), screenData);
//GESTION DE LA MORT DE LADYBUG
			if (groupGhosts.eatLadybug()) {
				ladybug.setStatus(LadybugStatusEnum.DYING);
				groupGhosts.manageNewLife();
			}
//GESTION DU SUPER POWER
			if (superPowerTimer.isStopping()) {
				groupGhosts.setFlashActive();
			} else if (superPowerTimer.isStopped()) {
				groupGhosts.setFear(false);
			}
			// Active le timer du super power si ladybug a mangé un méga point
			if (ladybug.isEatenAMegaPoint()) {
				runSuperPowerTimer();
				groupGhosts.setFear(true);
			}
//SCORE
			gameScore.setScore(groupGhosts, ladybug, groupIncrementScores);
			groupIncrementScores.removeIfDying();
//NOUVELLE VIE PACMAN
			if (gameScore.getIncrementScore() >= Constants.NEW_LIFE_BY_SCORE) {
				gameScore.setIncrementScore(0);
				ladybug.setNewLife(true);
			}
			ladybug.manageNewLife();
//DEPLACEMENT
			ladybug.move(screenData);
			groupGhosts.move(screenData, ladybug, ghostRequest);
//SCREENDATA
			screenData.updateScreenBlock(ladybug);
//VERIFICAITON NOMBRE POINT MANGEABLES
//FIXME : sortir ce test de ce block
			checkEndMaze();
		}
		setSoundRequests();
		setChanged();
		notifyObservers();
	}

	/**
	 * Vérifier si le labyrinthe est terminé
	 */
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
		ladybug.setStartLevel(gameStatus.getNumLevel(), screenData.getInitLadybugPos());
		// initialise les fantômes
		groupGhosts.setStartLevel(gameStatus.getNumLevel(), screenData);
	}

	/**
	 * Création du timer du jeu (coeur du jeu)
	 *
	 * @return
	 */
	private Timer createTimer() {
		ActionListener action = event -> {
			actionsByTimerBip();
		};
		return new Timer(PACE, action);
	}

	@PostConstruct
	private void init() {
		initGame();
		startGameTimer();
	}

	/**
	 * Initialise les variables au lancement du programme
	 */
	private void initGame() {
		gameStatus.setNoGame();
		// initialisation du numéro du niveau; incrémenté dans initLevel
		gameStatus.setNumLevel(1);
		// utilisé juste pour l'affichage de la fenêtre d'initialisation
		screenData.setLevelMap(gameStatus.getNumLevel(), gameStatus.isInGame());
		// 3 vies par défaut
		ladybug.setLeftLifes(Constants.NBR_INIT_LIFE);
		// ladybug n'est pas en vie lors de l'initialisation du jeu
		ladybug.setStatus(LadybugStatusEnum.DEAD);
		// initialise les scores
		gameScore.init();
		// Initialise le groupe de fantôme
		groupGhosts.setNumLevel(Constants.PRESENTATION_LEVEL);
		// mise de la vitesse du niveau 3 pour la présentation
		groupGhosts.setInitSpeeds(Constants.PRESENTATION_LEVEL);
		// initialise les positions des fantômes
		groupGhosts.initializePositions(screenData);
		// initialise les fantômes pour la présentation
		groupGhosts.setStatus(GhostStatusEnum.NORMAL);
		// initialise les vies de fantômes
		groupGhosts.setLeftLifes(Constants.NBR_INIT_LIFE);
		// active sound
		setSoundActive(true);
	}

	/**
	 * Initialise le niveau en fonction du niveau précédent
	 */
	private void initLevel() {
		// suppression des composants techniques du niveau précédent
		removePreviousTasksLevel();
		gameStatus.setInGame();
		// incrémente le numéro du niveau
		gameStatus.setNumLevel(gameStatus.getNumLevel() + 1);
		// recopie les paramètres du niveau dans les données flottantes du niveau
		screenData.setLevelMap(gameStatus.getNumLevel(), gameStatus.isInGame());
		// initialisation du super power
		groupGhosts.setFear(false);
		// début du level : utile pour le son du jingle
		setBeginNewLevel(true);
		// initialisation de la cinématique de la mort de ladybug
		kinematicLadybugDeath.initBip();
		// on continue le level
		continueLevel();
	}

	/**
	 * Ladybug a rencontré un fantôme !! Le jeu est terminé si toutes les vies de
	 * ladybug ont été utilisées.
	 */
	private void ladybugIsDead() {
		ladybug.minusLifesLeft();
		// test fin du jeu
		if (ladybug.getLeftLifes() == 0) {
			logger.info("Ladybug a perdu");
			gameScore.setOldScore(gameScore.getScore());
			// à mettre ailleurs :
			initGame();
		} else {
			continueLevel();
		}
	}

	/**
	 * Cinématique de la mort de ladybug
	 */
	private void ladybugIsDying() {
		// déplacement des fantômes
		groupGhosts.move(screenData, ladybug, ghostRequest);
		// Incrémentation du bip pour la cinématique
		kinematicLadybugDeath.incrementBip();
		// Fin de la cinématique de la mort de ladybug
		if (kinematicLadybugDeath.isEnd()) {
			ladybug.setStatus(LadybugStatusEnum.DEAD);
			kinematicLadybugDeath.initBip();
		}
	}

	/**
	 * Suppression des tâches du niveau qui est terminé (succès, échec ou pas de
	 * niveau).
	 */
	private void removePreviousTasksLevel() {
		// arrêt des timers super power
		superPowerTimer.forcedStop();
	}

	/**
	 * Lancement du timer pour le super power de ladybug
	 */
	private void runSuperPowerTimer() {
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
		newSounds.addGameBeginLevel(gameStatus.isLevelBegin());
		newSounds.addScaredGhost(groupGhosts.hasScaredGhost());
		newSounds.addRegeneratedGhost(groupGhosts.hasRegeneratedGhost());
		newSounds.addDyingGhost(groupGhosts.hasDyingGhost());
		newSounds.addLadybugEatGhost(groupGhosts.getNbrEatenGhost() > 0);
		newSounds.addLadybugEatenAPoint(ladybug.isEatenAPoint());
		newSounds.addLadybugIsDying(ladybug.getStatus().equals(LadybugStatusEnum.DYING),
		        kinematicLadybugDeath.getBip() == 0);
		newSounds.addNewLife(ladybug.isNewLife());
		newSounds.addTeleport(ladybug.isToBeTeleported());
		newSounds.addSirenSound(ladybug.getStatus().equals(LadybugStatusEnum.NORMAL),
		        screenData.getPercentageEatenPoint());
	}
}
