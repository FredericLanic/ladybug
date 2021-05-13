package com.kycox.game.model.strategy;

import java.security.SecureRandom;

import javax.inject.Inject;
import javax.inject.Named;

import com.kycox.game.body.ghost.GhostsGroup;
import com.kycox.game.body.ladybug.Ladybug;
import com.kycox.game.body.ladybug.LadybugDying;
import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.level.ScreenData;
import com.kycox.game.model.CurrentGameStatus;
import com.kycox.game.score.GameScore;
import com.kycox.game.score.GroupMessages;
import com.kycox.game.sound.NewSounds;
import com.kycox.game.timer.SuperPowerTimer;
import com.kycox.game.timer.WaitAndDoActionAfterTimer;

import lombok.Getter;
import lombok.Setter;

@Named("AbstratGameModel")
public class AbstratGameModel {
	@Getter
	@Inject
	protected CurrentGameStatus currentGameStatus;

	@Getter
	@Inject
	protected GameScore gameScore;

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
	 * Ajoute des sons en fonction de l'état des fantômes et de ladybug
	 */
	protected void setSoundRequests() {

		boolean musicOn = new SecureRandom().nextInt(1000) > 997;

		newSounds.initSounds();
		newSounds.addGameBeginLevel(currentGameStatus.isLevelStarting());
		newSounds.addIntermission(
		        currentGameStatus.isGamePresentation() && musicOn || currentGameStatus.isLevelEnding());
		newSounds.addScaredGhost(groupGhosts.hasScaredOrFlashedGhost());
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
}
