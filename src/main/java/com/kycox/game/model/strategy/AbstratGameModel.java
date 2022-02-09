package com.kycox.game.model.strategy;

import java.security.SecureRandom;

import javax.inject.Inject;
import javax.inject.Named;

import com.kycox.game.body.ghost.GhostsGroup;
import com.kycox.game.body.ladybug.Ladybug;
import com.kycox.game.body.ladybug.LadybugDying;
import com.kycox.game.constant.Constants;
import com.kycox.game.constant.ghost.GhostStatus;
import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.level.ScreenData;
import com.kycox.game.model.CurrentProgramStatus;
import com.kycox.game.score.GroupMessages;
import com.kycox.game.score.Score;
import com.kycox.game.sound.NewSounds;
import com.kycox.game.timer.SuperPowerTimer;
import com.kycox.game.timer.WaitAndDoActionAfterTimer;

import lombok.Getter;
import lombok.Setter;

@Named("AbstratGameModel")
public class AbstratGameModel {
	@Getter
	@Inject
	protected CurrentProgramStatus currentGameStatus;
	@Getter
	@Inject
	protected Score gameScore;
	@Getter
	@Inject
	protected GhostsGroup groupGhosts;
	@Getter
	@Inject
	protected GroupMessages groupMessages;
	@Getter
	@Inject
	protected Ladybug ladybug;
	@Getter
	@Inject
	protected LadybugDying ladybugDying;
	@Getter
	@Inject
	protected NewSounds newSounds;
	@Getter
	@Inject
	protected ScreenData screenData;
	@Getter
	@Setter
	protected boolean soundActive = true;
	@Inject
	protected SuperPowerTimer superPowerTimer;
	protected WaitAndDoActionAfterTimer waitAndDoActionAfterTimer;

	/**
	 * Paramêtrage des fantômes dans le niveau actuel
	 *
	 * Initialisation des directions et de la vue de ladybug
	 *
	 * suite de l'initialisation du niveau ou quand ladybug meurt
	 */
	protected void continueLevel() {
		// initialise ladybug pour le niveau
		ladybug.settingsForNewLevel(currentGameStatus.getNumLevel(), screenData.getInitLadybugPos());
		// initialise les fantômes
		groupGhosts.setStartLevel(currentGameStatus.getNumLevel(), screenData);
	}

	/**
	 * Initialise les variables au lancement du programme
	 */
	protected void initGame() {
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
	 * Ajoute des sons en fonction de l'état des fantômes et de ladybug
	 */
	protected void setSoundRequests() {
		var musicOn = new SecureRandom().nextInt(1000) > 997;
		newSounds.initSounds();

		newSounds.addGameBeginLevel(currentGameStatus.isLevelStarting());
		newSounds.addIntermission(
		        currentGameStatus.isProgramPresentation() && musicOn || currentGameStatus.isLevelEnding());
		newSounds.addScaredGhost(groupGhosts.hasScaredOrFlashedGhost() && currentGameStatus.isInGame());
		newSounds.addRegeneratedGhost(groupGhosts.hasRegeneratedGhost() && currentGameStatus.isInGame());
		newSounds.addDyingGhost(groupGhosts.hasDyingGhost() && currentGameStatus.isInGame());
		newSounds.addLadybugEatGhost(groupGhosts.getNbrEatenGhosts() > 0 && currentGameStatus.isInGame());
		newSounds.addLadybugEatenAPoint(ladybug.isEatenAPoint() && currentGameStatus.isInGame());
		newSounds.addNewLife(ladybug.isNewLife() && currentGameStatus.isInGame());
		newSounds.addTeleport(
		        (ladybug.isToBeTeleported() || groupGhosts.hasTeleportedGhosts() && currentGameStatus.isInGame()));
		newSounds.addSirenSound(ladybug.getStatus() == LadybugStatus.NORMAL && currentGameStatus.isInGame(),
		        screenData.getPercentageEatenPoint());
		newSounds.addLadybugIsDying(ladybug.getStatus() == LadybugStatus.DYING, !ladybugDying.isInPogress());
		newSounds.addLaybugEatenFruit(ladybug.hasEatenAFruit() && currentGameStatus.isInGame());
	}
}
