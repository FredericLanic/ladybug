package com.kycox.game.tools.timer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests du compte à rebours et du planificateur en battements (étape 3.5.1b).
 */
class TickSchedulerTest {

	@ParameterizedTest(name = "{0} ms → {1} battements")
	@CsvSource({ "0, 0", "1, 1", "40, 1", "41, 2", "1500, 38", "2500, 63", "15000, 375" })
	void millisecondsAreRoundedUpToWholeTicks(long milliseconds, int expectedTicks) {
		assertThat(TickCountdown.ticksFor(milliseconds)).isEqualTo(expectedTicks);
	}

	@Test
	void countdownEndsExactlyOnItsLastTick() {
		var countdown = new TickCountdown();
		countdown.startTicks(3);

		assertThat(countdown.tick()).isFalse();
		assertThat(countdown.tick()).isFalse();
		assertThat(countdown.tick()).isTrue();
		assertThat(countdown.isRunning()).isFalse();
		assertThat(countdown.tick()).isFalse(); // une seule fin
	}

	@Test
	void stoppedCountdownNeverEnds() {
		var countdown = new TickCountdown();
		countdown.start(2500);
		countdown.stop();

		assertThat(countdown.tick()).isFalse();
		assertThat(countdown.isRunning()).isFalse();
	}

	@Test
	void actionRunsAfterTheRightNumberOfTicks() {
		var scheduler = new TickScheduler();
		var runs = new ArrayList<Integer>();
		scheduler.schedule(2500, () -> runs.add(1));

		for (var i = 0; i < 62; i++) {
			scheduler.tick();
		}
		assertThat(runs).isEmpty();

		scheduler.tick(); // 63e battement
		assertThat(runs).containsExactly(1);
		assertThat(scheduler.pendingCount()).isZero();

		scheduler.tick();
		assertThat(runs).containsExactly(1); // une seule exécution
	}

	@Test
	void zeroDurationRunsOnTheNextTick() {
		var scheduler = new TickScheduler();
		var runs = new ArrayList<String>();
		scheduler.schedule(0, () -> runs.add("now"));

		assertThat(runs).isEmpty();
		scheduler.tick();
		assertThat(runs).containsExactly("now");
	}

	@Test
	void actionsDueOnTheSameTickRunInSchedulingOrder() {
		var scheduler = new TickScheduler();
		var runs = new ArrayList<String>();
		scheduler.schedule(80, () -> runs.add("first"));
		scheduler.schedule(41, () -> runs.add("second"));
		scheduler.schedule(40, () -> runs.add("early"));

		scheduler.tick();
		assertThat(runs).containsExactly("early");
		scheduler.tick();
		assertThat(runs).containsExactly("early", "first", "second");
	}

	@Test
	void anActionCanScheduleAnotherOne() {
		var scheduler = new TickScheduler();
		List<String> runs = new ArrayList<>();
		scheduler.schedule(40, () -> {
			runs.add("first");
			scheduler.schedule(40, () -> runs.add("second"));
		});

		scheduler.tick();
		assertThat(runs).containsExactly("first");
		scheduler.tick();
		assertThat(runs).containsExactly("first", "second");
	}
}
