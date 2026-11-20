package com.kycox.game.engine.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kycox.game.constant.level.LevelRendering;

/**
 * Tests de {@link LevelState}, l'état du niveau décidé par le jeu (étape 4.5).
 */
class LevelStateTest {
	private final LevelState levelState = new LevelState();

	@Test
	@DisplayName("sur l'écran de présentation, les murs ont le style par défaut")
	void presentationUsesTheDefaultRendering() {
		levelState.drawLevelRendering(false);
		assertThat(levelState.getLevelRendering()).isEqualTo(LevelRendering.getDefaultLevelRendering());
	}

	@Test
	@DisplayName("pendant une partie, le style des murs est l'un des styles connus")
	void inGameUsesAKnownRendering() {
		levelState.drawLevelRendering(true);
		assertThat(levelState.getLevelRendering()).isIn((Object[]) LevelRendering.values());
	}

	@Test
	@DisplayName("F6 allume puis éteint la lampe")
	void toggleLitLampMode() {
		var before = levelState.isLitLampMode();
		levelState.toggleLitLampMode();
		assertThat(levelState.isLitLampMode()).isNotEqualTo(before);
		levelState.toggleLitLampMode();
		assertThat(levelState.isLitLampMode()).isEqualTo(before);
	}
}
