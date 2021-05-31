package com.kycox.game.model.strategy.actions;

import java.awt.Point;

import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kycox.game.constant.Constants;
import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;

import lombok.Setter;

@Named("GameModelGameIsPlaying")
public class GameModelGameIsPlaying extends AbstratGameModel implements IGameModelAction {

	private static final Log logger = LogFactory.getLog(GameModelGameIsPlaying.class);

	@Setter
	private Point ghostRequest = Constants.POINT_ZERO;

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
		if (screenData.getNbrBlocksWithPoint() == 0) {
			gameScore.addScore(Constants.SCORE_END_LEVEL);
			gameScore.initIncrementScore();
			currentGameStatus.setLevelEnd();
		}
	}

	private boolean hasEnoughtPointForANewLife() {
		return gameScore.getIncrementScore() >= Constants.NEW_LIFE_BY_SCORE;
	}

	private void manageScores() {
		gameScore.setScoreAndMessages(groupGhosts, ladybug, groupMessages);
		groupMessages.removeIfDying();
		// ***
		if (hasEnoughtPointForANewLife()) {
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
		groupGhosts.move(ladybug, screenData, ghostRequest);
	}

	@Override
	public void programBeat() {
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
		checkEndMaze();
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
		// IDEA : Il serait bien de d'apporter une notion du temps en fonction du
		// niveau
		superPowerTimer.launch(Constants.NBR_SECONDS_SUPER_POWER);
	}

	private void setBodiesActions() {
		ladybug.setActions(screenData);
		groupGhosts.setActions(ladybug);
	}

	private void updateGhostSeetings() {
		groupGhosts.updateSeetings(currentGameStatus.getNumLevel(), screenData.getPercentageEatenPoint());
	}

	private void updateScreenBlock() {
		screenData.updateScreenBlock(ladybug);
	}
}
