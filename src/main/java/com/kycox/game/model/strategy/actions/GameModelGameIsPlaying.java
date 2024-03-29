package com.kycox.game.model.strategy.actions;

import com.kycox.game.constant.Constants;
import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.fruit.Fruits;
import com.kycox.game.message.GameAutomaticFunMessages;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;
import com.kycox.game.tools.Utils;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.security.SecureRandom;

@Component
public class GameModelGameIsPlaying extends AbstratGameModel implements IGameModelAction {
	private static final Log logger = LogFactory.getLog(GameModelGameIsPlaying.class);
	private final Fruits fruits;
	@Setter
	private Point ghostRequest = Constants.POINT_ZERO;

	public GameModelGameIsPlaying(Fruits fruits) {
		this.fruits = fruits;
	}

	@Override
	public void programBeat() {
		if (!currentGameStatus.isGameInPause()) {
			addNewFruit();
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
			// ***
			addNewFunProgramMessage();
		}
	}

	private void addNewFruit() {
		if (fruits.getActivationPercent() < screenData.getPercentageEatenPoint()) {
			screenData.addNewFruit(fruits.getCurrentIdFruit());
			fruits.next();
		}
	}

	private void addNewFunProgramMessage() {
		var addFunMessages = new SecureRandom().nextInt(1000) > 996;
		if (addFunMessages) {
			gameMessaging.put(Utils.randomEnum(GameAutomaticFunMessages.class).getMessage());
		}

	}

	private void caseOfGhostEatLadybug() {
		if (groupGhosts.eatLadybug()) {
			ladybug.setStatus(LadybugStatus.DYING);
			ladybugDying.initBip();
			// TODO : when user ghost has new life ?
			groupGhosts.manageNewLifeUnComputedGhost();
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
		return gameScore.getIncrementScore() >= gameScore.getNbrPointsForNewLife();
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
		groupGhosts.setActions(ladybug, screenData);
	}

	private void updateGhostSeetings() {
		groupGhosts.updateSeetings(currentGameStatus.getNumLevel(), screenData);
	}

	private void updateScreenBlock() {
		screenData.updateScreenBlock(ladybug);
	}
}
