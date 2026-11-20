package com.kycox.game.engine.sound;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.kycox.game.constant.sounds.Sounds;

/**
 * Durées des sons lues dans les fichiers, sans carte son (étape 3.5.3b).
 */
class SoundDurationsTest {
	private final SoundDurations soundDurations = new SoundDurations();

	@Test
	void levelStartMusicLastsFourSeconds() {
		assertThat(soundDurations.of(Sounds.GAME_BEGIN_LEVEL)).isEqualTo(4216);
	}

	@Test
	void everySoundHasADuration() {
		for (var sound : Sounds.values()) {
			assertThat(soundDurations.of(sound)).as(sound.name()).isPositive();
		}
	}
}
