package com.kycox.game.tools.timer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.kycox.game.constant.TimerStatus;

/**
 * Tests du super-pouvoir en battements (étape 3.5.1c) : 15 s = 250 battements
 * de peur, puis 125 de clignotement.
 */
class SuperPowerTimerTest {

	private static void tick(SuperPowerTimer timer, int beats) {
		for (var i = 0; i < beats; i++) {
			timer.tick();
		}
	}

	@Test
	void stoppedBeforeLaunch() {
		assertThat(new SuperPowerTimer().getStatus()).isEqualTo(TimerStatus.STOP);
	}

	@Test
	void fearThenFlashThenStop() {
		var timer = new SuperPowerTimer();
		timer.launch(15);
		assertThat(timer.getStatus()).isEqualTo(TimerStatus.RUN);

		tick(timer, 249);
		assertThat(timer.getStatus()).isEqualTo(TimerStatus.RUN); // 9,96 s

		tick(timer, 1);
		assertThat(timer.isStopping()).isTrue(); // 10 s : début du clignotement

		tick(timer, 124);
		assertThat(timer.isStopping()).isTrue(); // 14,96 s

		tick(timer, 1);
		assertThat(timer.isStopped()).isTrue(); // 15 s
	}

	@Test
	void forcedStopEndsImmediately() {
		var timer = new SuperPowerTimer();
		timer.launch(15);
		tick(timer, 10);

		timer.forcedStop();

		assertThat(timer.isStopped()).isTrue();
	}

	@Test
	void newMegaPointRestartsTheFullDuration() {
		var timer = new SuperPowerTimer();
		timer.launch(15);
		tick(timer, 300); // en plein clignotement
		assertThat(timer.isStopping()).isTrue();

		timer.forcedStop();
		timer.launch(15);

		assertThat(timer.getStatus()).isEqualTo(TimerStatus.RUN);
		tick(timer, 250);
		assertThat(timer.isStopping()).isTrue();
	}
}
