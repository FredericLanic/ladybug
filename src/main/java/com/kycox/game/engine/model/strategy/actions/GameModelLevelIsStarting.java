package com.kycox.game.engine.model.strategy.actions;

import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.constant.fruits.Fruits;
import com.kycox.game.engine.message.GameMessaging;
import com.kycox.game.engine.model.strategy.AbstratGameModel;
import com.kycox.game.engine.model.strategy.IGameModelAction;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GameModelLevelIsStarting extends AbstratGameModel implements IGameModelAction {
	private static final Logger logger = LoggerFactory.getLogger(GameModelLevelIsStarting.class);
	@Setter
	private long beginningDuration;
	private final Fruits fruits;

	public GameModelLevelIsStarting(Fruits fruits, GameMessaging gameMessaging) {
		this.fruits = fruits;
		this.gameMessaging = gameMessaging;
	}

	/**
	 * Initialise le niveau en fonction du niveau précédent
	 */
	private void initLevel() {
		logger.info("Initialize level");
		// suppression des composants techniques du niveau précédent
		removePreviousLevelTasks();
		currentGameStatus.setGameStart();
		// incrémente le numéro du niveau
		currentGameStatus.getStoredNumLevel();;
		currentGameStatus.updateNextLevel();
		// recopie les paramètres du niveau dans les données flottantes du niveau
		prepareMaze(currentGameStatus.getNumLevel(), true);
		// initialisation du super power
		groupGhosts.setFear(false);
		// ladybug est vivant
		ladybug.setStatus(LadybugStatus.NORMAL);
		// initialise les fruits
		fruits.init();
		// init litLampMode
		levelState.drawLitLampMode(currentGameStatus.getNumLevel());
		// on continue le level
		continueLevel();
	}

	@Override
	public void programBeat() {
		initLevel();
		currentGameStatus.setLevelStarting();
		tickScheduler.schedule(beginningDuration, currentGameStatus::setInGame);
		setSoundRequests();
	}

	/**
	 * Suppression des tâches du niveau qui est terminé (succès, échec ou pas de
	 * niveau).
	 */
	private void removePreviousLevelTasks() {
		// arrêt des timers super power
		superPowerTimer.forcedStop();
	}
}
