package com.kycox.game.preferences;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests de {@link LocalPreferences}, les réglages propres à ce poste (étape 4.5).
 */
class LocalPreferencesTest {
	private final LocalPreferences preferences = new LocalPreferences();

	@Test
	@DisplayName("au démarrage : son actif, ni aide, ni debug, ni manette")
	void defaults() {
		assertThat(preferences.isSoundActive()).isTrue();
		assertThat(preferences.isShowHelpForKeys()).isFalse();
		assertThat(preferences.isShowHelpForXboxes()).isFalse();
		assertThat(preferences.isDebugMode()).isFalse();
		assertThat(preferences.isAtLeastOneXboxOneConnected()).isFalse();
	}

	@Test
	@DisplayName("couper le son renvoie le nouvel état, à annoncer à l'écran")
	void toggleSoundReturnsTheNewState() {
		assertThat(preferences.toggleSound()).isFalse();
		assertThat(preferences.isSoundActive()).isFalse();
		assertThat(preferences.toggleSound()).isTrue();
	}

	@Test
	@DisplayName("Entrée allume puis éteint le mode debug")
	void toggleDebugMode() {
		preferences.toggleDebugMode();
		assertThat(preferences.isDebugMode()).isTrue();
		preferences.toggleDebugMode();
		assertThat(preferences.isDebugMode()).isFalse();
	}
}
