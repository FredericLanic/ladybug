package com.kycox.game.model.strategy.actions;

import java.awt.Point;

import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kycox.game.constant.Constants;
import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.model.CurrentGameStatus;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;
import com.kycox.game.timer.WaitAndDoActionAfterTimer;

import lombok.Setter;

@Named("GameModelLevelIsStarting")
public class GameModelLevelIsStarting extends AbstratGameModel implements IGameModelAction {
	
	private static final Log		  logger	   = LogFactory.getLog(GameModelLevelIsStarting.class);

	@Setter
	private long beginningMilliseconds;
	
	@Setter
	private Point					  ghostRequest = Constants.POINT_ZERO;
	
	@Override
	public void execute() {
		initLevel();
		currentGameStatus.setLevelStarting();
		waitAndDoActionAfterTimer = new WaitAndDoActionAfterTimer();
		waitAndDoActionAfterTimer.launch(beginningMilliseconds, currentGameStatus, CurrentGameStatus.TO_INGAME);
		setSoundRequests();
	}
	
	/**
	 * Initialise le niveau en fonction du niveau précédent
	 */
	private void initLevel() {
		logger.info("Initialize level");
		// suppression des composants techniques du niveau précédent
		removePreviousLevelTasks();
		// currentGameStatus.setLevelStart();
		currentGameStatus.setGameStart();
		// incrémente le numéro du niveau
		currentGameStatus.setNumLevel(currentGameStatus.getNumLevel() + 1);
		// recopie les paramètres du niveau dans les données flottantes du niveau
		screenData.setLevelMap(currentGameStatus.getNumLevel(), true);
		// initialisation du super power
		groupGhosts.setFear(false);
		// son activé
		setSoundActive(true);
		// ladybug est vivant
		ladybug.setStatus(LadybugStatus.NORMAL);
		// on continue le level
		continueLevel();
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