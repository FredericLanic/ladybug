package com.kycox.game.input;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kycox.game.constant.game.GameMainConstants;

/**
 * La boîte aux lettres d'un joueur (étape 5.1) : elle garde la dernière
 * direction demandée jusqu'à la suivante.
 */
class PlayerInputTest {
	private final PlayerInput input = new PlayerInput(() -> "Ladybug");

	@Test
	@DisplayName("au départ, aucune direction n'est demandée")
	void noRequestAtFirst() {
		assertThat(input.hasRequest()).isFalse();
		assertThat(input.requested()).isEqualTo(GameMainConstants.POINT_ZERO);
	}

	@Test
	@DisplayName("la dernière demande reste valable jusqu'à la suivante")
	void theLastRequestIsKept() {
		input.request(GameMainConstants.POINT_LEFT);
		input.request(GameMainConstants.POINT_UP);
		assertThat(input.requested()).isEqualTo(GameMainConstants.POINT_UP);
		assertThat(input.hasRequest()).isTrue();
	}

	@Test
	@DisplayName("on sait quel appareil a donné le dernier ordre")
	void theLastSourceIsKnown() {
		assertThat(input.lastSource()).isEmpty();
		input.request(GameMainConstants.POINT_LEFT, InputSource.KEYBOARD_ARROWS);
		assertThat(input.lastSource()).contains(InputSource.KEYBOARD_ARROWS);
		input.request(GameMainConstants.POINT_UP, InputSource.XBOX_PAD_1);
		assertThat(input.lastSource()).contains(InputSource.XBOX_PAD_1);
		// un ordre sans appareil (bot, scénario) ne change pas la source connue
		input.request(GameMainConstants.POINT_DOWN);
		assertThat(input.lastSource()).contains(InputSource.XBOX_PAD_1);
	}

	@Test
	@DisplayName("en début de niveau, la demande est oubliée")
	void resetForgetsTheRequest() {
		input.request(GameMainConstants.POINT_LEFT);
		input.reset();
		assertThat(input.hasRequest()).isFalse();
	}
}
