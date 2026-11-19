package com.kycox.game.engine.model;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import com.kycox.game.body.GhostsGroup;
import com.kycox.game.body.ladybug.Ladybug;
import com.kycox.game.body.ladybug.LadybugDying;
import com.kycox.game.constant.game.GameStatus;
import com.kycox.game.engine.message.GameMessaging;
import com.kycox.game.engine.model.strategy.GameModelManageAction;
import com.kycox.game.engine.model.strategy.IGameModelAction;
import com.kycox.game.engine.model.strategy.ManageActionContext;
import com.kycox.game.engine.score.GroupMessages;
import com.kycox.game.engine.score.Score;
import com.kycox.game.engine.screendata.ScreenData;
import com.kycox.game.engine.sound.NewSounds;
import com.kycox.game.tools.timer.SuperPowerTimer;
import com.kycox.game.tools.timer.TickScheduler;

/**
 * Un battement de la boucle du jeu, appelé directement par un test, sans timer
 * Swing (étape 3.5.1e). Les collaborateurs sont simulés (Mockito) : on vérifie
 * seulement l'ordre des opérations d'un battement.
 */
class GameModelBeatTest {
	private CurrentProgramStatus currentProgramStatus;
	private GameModelManageAction gameModelManageAction;
	private ManageActionContext manageActionContext;
	private GroupMessages groupMessages;
	private ApplicationEventPublisher publisher;
	private TickScheduler tickScheduler;
	private SuperPowerTimer superPowerTimer;
	private IGameModelAction inGameAction;
	private GameModel gameModel;

	@BeforeEach
	void setUp() {
		currentProgramStatus = mock(CurrentProgramStatus.class);
		gameModelManageAction = mock(GameModelManageAction.class);
		manageActionContext = mock(ManageActionContext.class);
		groupMessages = mock(GroupMessages.class);
		publisher = mock(ApplicationEventPublisher.class);
		tickScheduler = mock(TickScheduler.class);
		superPowerTimer = mock(SuperPowerTimer.class);
		inGameAction = mock(IGameModelAction.class);

		when(currentProgramStatus.getGameStatus()).thenReturn(GameStatus.IN_GAME);
		when(manageActionContext.getStrategyStatusAction(GameStatus.IN_GAME)).thenReturn(inGameAction);

		gameModel = new GameModel(currentProgramStatus, mock(GameMessaging.class), gameModelManageAction,
		        mock(Score.class), mock(GhostsGroup.class), groupMessages, mock(Ladybug.class),
		        mock(LadybugDying.class), mock(NewSounds.class), mock(ScreenData.class), manageActionContext,
		        publisher, tickScheduler, superPowerTimer);
	}

	@Test
	void aBeatAdvancesClocksThenPlaysTheCurrentStateThenNotifies() {
		gameModel.beat();

		var order = inOrder(tickScheduler, superPowerTimer, groupMessages, gameModelManageAction, publisher);
		order.verify(tickScheduler).tick();
		order.verify(superPowerTimer).tick();
		order.verify(groupMessages).tick();
		order.verify(gameModelManageAction).changeStrategy(inGameAction);
		order.verify(gameModelManageAction).execute();
		order.verify(publisher).publishEvent(any(EventGameModel.class));
	}

	@Test
	void aTestDrivesAsManyBeatsAsItWants() {
		for (var i = 0; i < 63; i++) {
			gameModel.beat();
		}

		verify(tickScheduler, times(63)).tick();
		verify(gameModelManageAction, times(63)).execute();
	}
}
