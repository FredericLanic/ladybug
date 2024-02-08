package com.kycox.game.model.strategy.actions;

import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.fruit.Fruits;
import com.kycox.game.maths.LitLampMode;
import com.kycox.game.message.GameMessaging;
import com.kycox.game.model.CurrentProgramStatus;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;
import com.kycox.game.timer.WaitAndDoActionAfterTimer;
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
	private final GameMessaging gameMessaging;

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
		screenData.setLevelMap(currentGameStatus.getNumLevel(), true);
		// initialisation du super power
		groupGhosts.setFear(false);
		// son activé
		setSoundActive(true);
		// ladybug est vivant
		ladybug.setStatus(LadybugStatus.NORMAL);
		// initialise les fruits
		fruits.init();
		// init litLampMode
		screenData.setLitLampMode(LitLampMode.isLitLampMode(currentGameStatus.getNumLevel()));
		gameMessaging.init();
		// on continue le level
		continueLevel();
	}

	@Override
	public void programBeat() {
		initLevel();
		currentGameStatus.setLevelStarting();
		waitAndDoActionAfterTimer = new WaitAndDoActionAfterTimer();
		waitAndDoActionAfterTimer.launch(beginningDuration, currentGameStatus, CurrentProgramStatus.TO_INGAME);
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
