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
package com.kycox.ladybug.model;

import static com.kycox.ladybug.constant.Constants.PACE;

import java.awt.Point;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javax.swing.Timer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kycox.ladybug.action.ghost.GhostsGroupActions;
import com.kycox.ladybug.action.ladybug.LadybugActions;
import com.kycox.ladybug.body.ghost.GhostsGroup;
import com.kycox.ladybug.body.ladybug.Ladybug;
import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.constant.SoundsEnum;
import com.kycox.ladybug.constant.SuperPowerTimerEnum;
import com.kycox.ladybug.constant.ghost.GhostStatusEnum;
import com.kycox.ladybug.constant.ladybug.KinematicLadybugDeath;
import com.kycox.ladybug.constant.ladybug.LadybugStatusEnum;
import com.kycox.ladybug.level.ScreenData;
import com.kycox.ladybug.score.GameScore;
import com.kycox.ladybug.score.GroupIncrementScores;
import com.kycox.ladybug.timer.SuperPowerTimer;

import lombok.Getter;
import lombok.Setter;

/**
 * Modèle du jeu MVC : c'est le modèle qui contient le timer du jeu (coeur du
 * jeu)
 *
 */
@SuppressWarnings("deprecation")
public class GameModel extends Observable {
	private static final Log	  logger		= LogFactory.getLog(GameModel.class);
	@Getter
	@Setter
	private boolean				  beginNewLevel	= false;
	@Getter
	@Setter
	private GameScore			  gameScore;
	@Getter
	@Setter
	private GameStatus			  gameStatus;
	private final Timer			  gameTimer		= createTimer();
	@Setter
	private Point				  ghostRequest	= Constants.POINT_ZERO;
	@Getter
	private GhostsGroupActions	  ghostsActions;
	@Getter
	@Setter
	private GhostsGroup			  groupGhosts;
	@Getter
	@Setter
	private GroupIncrementScores  groupIncrementScores;
	@Getter
	@Setter
	private KinematicLadybugDeath kinematicLadybugDeath;
	@Getter
	@Setter
	private Ladybug				  ladybug;
	@Getter
	private LadybugActions		  ladybugActions;
	@Getter
	private List<SoundsEnum>	  lstSoundsEnum	= new ArrayList<>();
	@Getter
	private int					  newSounds;
	@Getter
	@Setter
	private ScreenData			  screenData;
	@Getter
	@Setter
	private boolean				  soundActive	= false;
	@Setter
	private SuperPowerTimer		  superPowerTimer;

	public void forceStopGame() {
		if (gameTimer.isRunning()) {
			gameScore.setOldScore(-1);
			initGame();
		}
	}

	/**
	 * Jeu en pause
	 */
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
	public int getNbrPlayers() {
		if (groupGhosts.getGhostNotComputed() == null) {
			return 1;
		}
		return 2;
	}

	public void init() {
		initGame();
		startGameTimer();
	}

	/**
	 * Initialise le niveau en fonction du niveau précédent
	 */
	public void initLevel() {
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

	public void setNewSounds(int sounds) {
		newSounds |= sounds;
	}

	public void startGame() {
		initGame();
		initLevel();
	}

	/**
	 * Lancement du timer qui rythme le jeu FIXME : cette fonction reste public car
	 * elle est toujorus utilisé par GameSounds -> c'est le mal
	 */
	public void startGameTimer() {
		gameTimer.start();
	}

	/**
	 * FIXME : peut être gérer en fonction de l'état du jeu quand par exemple on est
	 * en intro
	 */
	public void startStopSoundActive() {
		logger.info("startStopSoundActive : " + soundActive);
		soundActive = !soundActive;
	}

	public void stopGameTimer() {
		gameTimer.stop();
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
			if (gameStatus.isInGame() && LadybugStatusEnum.isDead().test(ladybug)) {
				ladybugIsDead();
			} else if (gameStatus.isInGame() && LadybugStatusEnum.isDying().test(ladybug)) {
				ladybugIsDying();
			} else if (gameStatus.isInGame() && groupGhosts.GhostUserIsDead()) {
				gameStatus.setNoGame();
			} else {
// Etat des fantômes de REGENERATING à NORMAL
				groupGhosts.setGhostStatusAfterRegeneration();
// Gestion de la nouvelle vie à Ladybug
				ladybug.addNewLife();
// Déplacement de ladybug et récupération de ses actions
				ladybugActions = ladybug.moveLadybug(screenData);
// Déplacement des fantômes
				groupGhosts.moveGhosts(screenData, ladybug, ghostRequest);
// Détections des actions des fantômes
				ghostsActions = groupGhosts.setAllGhostsActions(ladybug);
// GESTION DES INCREMENTS SCORES
				ladybugActions.addIncrementScores(groupIncrementScores);
				ghostsActions.addIncrementScores(groupIncrementScores);
				groupIncrementScores.removeIfDying();
// GESTION DE L'ETAT DES FANTOMES
				// Etats des fantômes
				ghostsActions.setGhostSettingAfterLadybugContact(gameStatus.getNumLevel());
				// modifier la vitesse des fantôme en cours de partie
				groupGhosts.setSpeed(gameStatus.getNumLevel(), screenData.getPercentageEatenPoint());
// GESTION DE LA MORT DE LADYBUG
				// modification de l'état de ladybug en fonction des actions détectées des
				// fantômes
				if (ghostsActions.eatLadybug()) {
					ladybug.setStatus(LadybugStatusEnum.DYING);
					ghostsActions.addNewLifeToKeyGhost();
				}
// GESTION DU SUPER POWER
				// mise à jour des status des fantômes en fonction du timer
				// Peut être le déplacer de là ??
				if (superPowerTimer.getStatus().equals(SuperPowerTimerEnum.STOPPING)) {
					groupGhosts.setGhostFlashActive();
				} else if ((superPowerTimer.getStatus().equals(SuperPowerTimerEnum.STOP))) {
					groupGhosts.setFear(false);
				}
				// Active le timer du super power si ladybug a mangé un méga point
				// Peut être le déplacer de là ??
				if (ladybugActions.isEatenAMegaPoint()) {
					runSuperPowerTimer();
					groupGhosts.setFear(true);
				}
// SCORE
				// Ajout du score
				gameScore.setScore(ghostsActions, ladybugActions);
				// ajout d'une vie supplémentaire si besoin
				if (gameScore.getIncrementScore() >= Constants.NEW_LIFE_BY_SCORE) {
					gameScore.setIncrementScore(0);
					ladybug.setNewLife(true);
				}
// SCREENDATA
				// Mise à jour du ScreenData
				screenData.updateScreenBlock(ladybugActions);
				// vérification de la fin du tableau
				checkEndMaze();
			}
			// notifie la view qu'il y a eu du changement
			setGameSounds(screenData.getPercentageEatenPoint());
			setChanged();
			notifyObservers();
		};
		// Création d'un timer qui génère un tic
		return new Timer(PACE, action);
	}

	private int getSirenSound(int percent) {
		if (ladybug.getStatus().equals(LadybugStatusEnum.NORMAL) && percent < 40) {
			return SoundsEnum.LADYBUG_SIREN_0.getIndex();
		} else if (ladybug.getStatus().equals(LadybugStatusEnum.NORMAL) && percent < 60) {
			return SoundsEnum.LADYBUG_SIREN_1.getIndex();
		} else if (ladybug.getStatus().equals(LadybugStatusEnum.NORMAL) && percent < 80) {
			return SoundsEnum.LADYBUG_SIREN_2.getIndex();
		} else if (ladybug.getStatus().equals(LadybugStatusEnum.NORMAL) && percent > 80) {
			return SoundsEnum.LADYBUG_SIREN_3.getIndex();
		}
		return 0;
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
		// initialise les score
		gameScore.init();
		// Initialise le groupe de fantôme
		groupGhosts.setNumLevel(Constants.PRESENTATION_LEVEL);
		// mise de la vitesse du niveau 3 pour la présentation
		groupGhosts.setInitSpeeds(Constants.PRESENTATION_LEVEL);
		// initialise les positions des fantômes
		groupGhosts.initPositions(screenData);
		// initialise les fantômes pour la présentation
		groupGhosts.setStatus(GhostStatusEnum.NORMAL);
		// initialise les vies de fantômes
		groupGhosts.setLeftLifes(Constants.NBR_INIT_LIFE);
		// active sound
		setSoundActive(true);
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
		groupGhosts.moveGhosts(screenData, ladybug, ghostRequest);
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
	private void setGameSounds(int percent) {
		// initialise le sons
		newSounds = 0;
		// Son depuis l'état du jeu
		if (gameStatus.isBeginningLevel()) {
			setNewSounds(SoundsEnum.LADYBUG_LEVEL_BEGINNING.getIndex());
		}
		// Son depuis les objets Fantômes
		if (groupGhosts.hasScaredGhost()) {
			setNewSounds(SoundsEnum.LADYBUG_INTER_MISSION.getIndex());
		}
		if (groupGhosts.hasRegeneratedGhost()) {
			setNewSounds(SoundsEnum.GHOST_REGENERATE.getIndex());
		}
		if (groupGhosts.hasDyingGhost()) {
			setNewSounds(SoundsEnum.GHOST_EATEN.getIndex());
		}
		if (ghostsActions.getNbrEatenGhost() > 0) {
			setNewSounds(SoundsEnum.LADYBUG_EAT_GHOST.getIndex());
			// son depuis Ladybug
		}
		if (ladybugActions.isEatenAMegaPoint()) {
			setNewSounds(SoundsEnum.LADYBUG_INTER_MISSION.getIndex());
		}
		if (ladybugActions.isEatenAPoint()) {
			setNewSounds(SoundsEnum.LADYBUG_CHOMP.getIndex());
		}
		if (ladybug.getStatus().equals(LadybugStatusEnum.DYING) && kinematicLadybugDeath.getBip() == 0) {
			setNewSounds(SoundsEnum.LADYBUG_IS_DYING.getIndex());
		}
		if (ladybug.isNewLife()) {
			setNewSounds(SoundsEnum.LADYBUG_EXTRA_PAC.getIndex());
		}
		setNewSounds(getSirenSound(screenData.getPercentageEatenPoint()));
	}
}
