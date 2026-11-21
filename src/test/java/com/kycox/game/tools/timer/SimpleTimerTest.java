package com.kycox.game.tools.timer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

/**
 * Une attente interrompue s'arrête proprement et rend le signal d'interruption
 * (règle S2142) au lieu de l'avaler.
 */
class SimpleTimerTest {

	@Test
	void anInterruptedWaitStopsAndKeepsTheInterruptFlag() throws InterruptedException {
		var waiter = new SimpleTimer.WaiterTimer(60_000);
		var flagKept = new AtomicBoolean();
		var thread = new Thread(() -> {
			waiter.run();
			flagKept.set(Thread.currentThread().isInterrupted());
		});

		thread.start();
		thread.interrupt();
		thread.join(2_000);

		assertThat(thread.isAlive()).isFalse();
		assertThat(flagKept).isTrue();
		assertThat(waiter.isRunning()).isFalse();
	}
}
