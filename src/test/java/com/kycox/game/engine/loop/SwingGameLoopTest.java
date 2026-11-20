package com.kycox.game.engine.loop;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

import com.kycox.game.engine.model.GameModel;

/**
 * La boucle du jeu (étape 4.1) fait battre le modèle au rythme du
 * {@code BeatPacer} : un battement toutes les 40 ms, retards rattrapés dans la
 * limite de {@link SwingGameLoop#MAX_CATCH_UP}.
 */
class SwingGameLoopTest {
	private static final long MS = 1_000_000L;

	private final GameModel gameModel = mock(GameModel.class);
	private final SwingGameLoop loop = new SwingGameLoop(gameModel);

	@Test
	void theFirstWakeUpPlaysOneBeat() {
		loop.wakeUp(0);

		verify(gameModel, times(1)).beat();
	}

	@Test
	void aWakeUpBeforeTheNextBeatPlaysNothing() {
		loop.wakeUp(0);
		loop.wakeUp(20 * MS);

		verify(gameModel, times(1)).beat();
	}

	@Test
	void aLateWakeUpCatchesUpTheMissedBeats() {
		loop.wakeUp(0);
		loop.wakeUp(120 * MS); // battements dus à 40, 80 et 120 ms

		verify(gameModel, times(4)).beat();
	}

	@Test
	void aVeryLongDelayIsCappedSoTheGameDoesNotJump() {
		loop.wakeUp(0);
		loop.wakeUp(10_000 * MS); // retour de mise en veille

		verify(gameModel, times(1 + SwingGameLoop.MAX_CATCH_UP)).beat();
	}

	@Test
	void creatingTheLoopDoesNotBeat() {
		verify(gameModel, never()).beat();
	}
}
