package com.kycox.game.engine.view.down;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Taille du dessin de Ladybug à côté des vies (étape 2.8) : toute la hauteur de
 * la bande, au plus une case (48 px).
 */
class StatusGameViewTest {

	@Test
	void imageUsesTheWholeBandOnTheMac() {
		// zone de 101 px sur le Mac : bande du score de 50 px
		assertThat(StatusGameView.imageSize(50, 2)).isEqualTo(46);
	}

	@Test
	void imageNeverExceedsOneBlock() {
		// zone de 144 px sur le HP : bande de 72 px, mais dessin plafonné à 48
		assertThat(StatusGameView.imageSize(72, 2)).isEqualTo(StatusGameView.MAX_IMAGE_SIZE).isEqualTo(48);
	}
}
