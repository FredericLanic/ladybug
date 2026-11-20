package com.kycox.game.engine.view;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kycox.game.engine.controller.KeyboardController;
import com.kycox.game.engine.message.LadybugMessagesBundle;
import com.kycox.game.engine.model.EventGameModel;
import com.kycox.game.engine.view.ghost.GhostView;
import com.kycox.game.engine.view.ladybug.LadybugDyingView;
import com.kycox.game.engine.view.ladybug.LadybugView;
import com.kycox.game.engine.view.map.ScreenBlockView;
import com.kycox.game.preferences.LocalPreferences;
import com.kycox.game.testutil.Snapshots;

/**
 * D25 : l'effet « vie gagnée » (murs dorés qui clignotent, texte « +1 VIE ») est
 * lancé à la réception de la photo du battement, et dure le temps du son,
 * compté en battements.
 */
class NewLifeEffectTest {
	/** 400 ms, soit 10 battements de 40 ms. */
	private static final long SOUND_DURATION = 400;

	private CentralView view;

	@BeforeEach
	void createView() {
		view = new CentralView(mock(GhostView.class), mock(KeyboardController.class), mock(LadybugDyingView.class),
		        mock(LadybugView.class), mock(ScreenBlockView.class), mock(LadybugMessagesBundle.class),
		        new LocalPreferences());
		view.setDurationLadybugNewLife(SOUND_DURATION);
	}

	private void beat(boolean newLife) {
		var snapshot = Snapshots.inGame(Snapshots.ladybugWithNewLife(newLife), List.of());
		view.onApplicationEvent(new EventGameModel(new Object(), snapshot));
	}

	@Test
	@DisplayName("sans vie gagnée, pas d'effet")
	void noEffectWithoutNewLife() {
		beat(false);
		assertThat(view.isNewLifeEffectOn()).isFalse();
	}

	@Test
	@DisplayName("un seul battement « vie gagnée » suffit, même si la fenêtre n'est pas redessinée entre-temps")
	void aSingleBeatStartsTheEffect() {
		beat(true);
		assertThat(view.isNewLifeEffectOn()).isTrue();
	}

	@Test
	@DisplayName("l'effet dure le temps du son (10 battements), puis s'arrête")
	void theEffectLastsTheSoundDuration() {
		beat(true);
		for (var i = 0; i < 9; i++) {
			beat(false);
			assertThat(view.isNewLifeEffectOn()).as("battement %d après la vie gagnée", i + 1).isTrue();
		}
		beat(false);
		assertThat(view.isNewLifeEffectOn()).isFalse();
	}

	@Test
	@DisplayName("les murs clignotent : 4 battements dorés, 4 normaux, et ainsi de suite")
	void theWallsBlinkEveryFourBeats() {
		beat(true);
		var highlights = new StringBuilder(view.isWallHighlightOn() ? "X" : ".");
		for (var i = 0; i < 9; i++) {
			beat(false);
			highlights.append(view.isWallHighlightOn() ? "X" : ".");
		}
		assertThat(highlights).hasToString("XXXX....XX");
		beat(false);
		assertThat(view.isWallHighlightOn()).isFalse();
	}

	@Test
	@DisplayName("une nouvelle vie pendant l'effet le relance pour toute sa durée")
	void aNewLifeDuringTheEffectRestartsIt() {
		beat(true);
		for (var i = 0; i < 5; i++) {
			beat(false);
		}
		beat(true);
		for (var i = 0; i < 9; i++) {
			beat(false);
		}
		assertThat(view.isNewLifeEffectOn()).isTrue();
	}
}
