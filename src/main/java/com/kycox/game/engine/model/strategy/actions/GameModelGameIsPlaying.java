package com.kycox.game.engine.model.strategy.actions;

import com.kycox.game.tools.Position;
import com.kycox.game.body.ghost.Ghost;
import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.constant.ghost.GhostStatus;
import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.constant.fruits.Fruits;
import com.kycox.game.engine.message.GameFunMessages;
import com.kycox.game.engine.model.strategy.AbstratGameModel;
import com.kycox.game.engine.model.strategy.IGameModelAction;
import com.kycox.game.tools.GameRandom;
import com.kycox.game.tools.Utils;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class GameModelGameIsPlaying extends AbstratGameModel implements IGameModelAction {
	private static final Logger logger = LoggerFactory.getLogger(GameModelGameIsPlaying.class);
	private final Fruits fruits;
	@Setter
	private Position ghostRequest = GameMainConstants.POINT_ZERO;

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
			logGameEvents();
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
			logger.info("Fruit {} appears", fruits.getCurrentIdFruit());
			screenData.addNewFruit(fruits.getCurrentIdFruit());
			fruits.next();
		}
	}

	private void addNewFunProgramMessage() {
		var addFunMessages = GameRandom.nextInt(1000) > 996;
		if (addFunMessages) {
			gameMessaging.addMessage(
					Utils.randomEnum(GameFunMessages.class).getKey()
			);
		}

	}

	private void caseOfGhostEatLadybug() {
		if (groupGhosts.eatLadybug()) {
			groupGhosts.getGhosts().stream().filter(g -> g.getGhostActions().isEatLadybug())
			        .forEach(g -> logger.info("Ladybug is eaten by {} ({})", g.getName(),
			                g.isComputed() ? "computer" : "human player"));
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
			gameScore.addScore(GameMainConstants.SCORE_END_LEVEL);
			gameScore.initIncrementScore();
			currentGameStatus.setLevelEnd();
		}
	}

	private boolean hasEnoughtPointForANewLife() {
		return gameScore.getIncrementScore() >= gameScore.getNbrPointsForNewLife();
	}

	/**
	 * Sondes de log (étape 3.4) : un message INFO par événement de jeu que la
	 * checklist vérifiait à l'œil. Le robot de test et Claude les retrouvent dans
	 * les logs. Appelée après {@link #manageScores()}, le score affiché est donc à
	 * jour.
	 */
	private void logGameEvents() {
		if (ladybug.isEatenAPoint()) {
			logger.info("Ladybug eats a point at {}, score {}", blockOf(ladybug.getPositionBlock()),
			        gameScore.getScore());
		}
		if (ladybug.hasEatenAFruit()) {
			logger.info("Ladybug eats fruit {}, score {}", ladybug.getEatenAIdRefFruit(), gameScore.getScore());
		}
		if (ladybug.isToBeTeleported()) {
			logger.info("Ladybug is teleported from {}", blockOf(ladybug.getPositionBlock()));
		}
		for (Ghost ghost : groupGhosts.getGhosts()) {
			if (ghost.getGhostActions().isEatenByLadybug()) {
				logger.info("{} is eaten by Ladybug, score {}", ghost.getName(), gameScore.getScore());
			}
			if (ghost.getGhostActions().isToBeTeleported()) {
				logger.info("{} is teleported", ghost.getName());
			}
			// REGENERATED ne dure qu'un battement : le message n'apparaît qu'une fois
			if (ghost.getStatus() == GhostStatus.REGENERATED) {
				logger.info("{} is regenerated at {}", ghost.getName(), blockOf(ghost.getPositionBlock()));
			}
		}
	}

	private static String blockOf(Position block) {
		return "(" + block.x() + "," + block.y() + ")";
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
		superPowerTimer.launch(GameMainConstants.NBR_SECONDS_SUPER_POWER);
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
