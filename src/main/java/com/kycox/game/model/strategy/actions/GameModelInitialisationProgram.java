package com.kycox.game.model.strategy.actions;

import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kycox.game.constant.Constants;
import com.kycox.game.constant.ghost.GhostStatus;
import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.model.CurrentGameStatus;
import com.kycox.game.model.strategy.AbstratGameModel;
import com.kycox.game.model.strategy.IGameModelAction;
import com.kycox.game.timer.WaitAndDoActionAfterTimer;

@Named("GameModelInitialisationProgram")
public class GameModelInitialisationProgram extends AbstratGameModel implements IGameModelAction {
	
	private static final Log		  logger	   = LogFactory.getLog(GameModelInitialisationProgram.class);

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		initGame();
		setSoundActive(false);
		currentGameStatus.setProgramStarting();
		waitAndDoActionAfterTimer = new WaitAndDoActionAfterTimer();
		waitAndDoActionAfterTimer.launch(Constants.PROGRAM_STARTING_MILLISECONDS, currentGameStatus,
		        CurrentGameStatus.TO_PRESENTATION);
	}

	/**
	 * Initialise les variables au lancement du programme
	 */
	private void initGame() {
		logger.info("Initialize game");
		// currentGameStatus.setGamePresentation();
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
	
}
