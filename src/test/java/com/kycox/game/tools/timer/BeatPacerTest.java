package com.kycox.game.tools.timer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Tests de la cadence à pas fixe (étape 3.5.1b) : 25 battements par seconde,
 * même si le timer Swing se réveille en retard.
 */
class BeatPacerTest {
	private static final long MS = 1_000_000L;

	@Test
	void firstWakeUpPlaysOneBeat() {
		var pacer = new BeatPacer(40, 5);

		assertThat(pacer.beatsDue(1_000 * MS)).isEqualTo(1);
	}

	@Test
	void earlyWakeUpPlaysNoBeat() {
		var pacer = new BeatPacer(40, 5);
		pacer.beatsDue(0);

		assertThat(pacer.beatsDue(20 * MS)).isZero();
		assertThat(pacer.beatsDue(40 * MS)).isEqualTo(1);
	}

	@Test
	void lateWakeUpsAreCaughtUp() {
		// le timer se réveille toutes les 43 ms au lieu de 40 : sur 4,3 s, le jeu
		// doit quand même jouer 4,3 s / 40 ms = 108 battements (107,5 arrondi)
		var pacer = new BeatPacer(40, 5);
		var total = 0;
		for (long t = 0; t <= 4_300; t += 43) {
			total += pacer.beatsDue(t * MS);
		}

		assertThat(total).isEqualTo(108);
	}

	@Test
	void frequentWakeUpsKeepTheSameRate() {
		// réveil toutes les 20 ms : un battement sur deux
		var pacer = new BeatPacer(40, 5);
		var total = 0;
		for (long t = 0; t < 1_000; t += 20) {
			total += pacer.beatsDue(t * MS);
		}

		assertThat(total).isEqualTo(25);
	}

	@Test
	void longPauseIsNotFullyCaughtUp() {
		var pacer = new BeatPacer(40, 5);
		pacer.beatsDue(0);

		// 10 s sans réveil (mise en veille) : 5 battements au plus, puis on repart
		assertThat(pacer.beatsDue(10_000 * MS)).isEqualTo(5);
		assertThat(pacer.beatsDue(10_020 * MS)).isZero();
		assertThat(pacer.beatsDue(10_040 * MS)).isEqualTo(1);
	}
}
