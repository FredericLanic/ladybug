package com.kycox.game.engine.view.down;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.awt.image.BufferedImage;
import java.util.List;

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
	void theBandCanBeDrawnBeforeTheFirstBeat() {
		// étape 4.1 : la fenêtre est dessinée avant que la boucle du jeu ne démarre ;
		// le bandeau n'a alors reçu ni message ni image (NullPointerException vue au lancement)
		var view = new StatusGameView();
		view.setSize(672, 50);
		var image = new BufferedImage(672, 50, BufferedImage.TYPE_INT_RGB);
		var graphics = image.createGraphics();

		assertThatCode(() -> view.display(graphics)).doesNotThrowAnyException();
		graphics.dispose();
	}

	@Test
	void imageNeverExceedsOneBlock() {
		// zone de 144 px sur le HP : bande de 72 px, mais dessin plafonné à 48
		assertThat(StatusGameView.imageSize(72, 2)).isEqualTo(StatusGameView.MAX_IMAGE_SIZE).isEqualTo(48);
	}

	@Test
	void ghostsAreDrawnOneAfterTheOtherAfterLadybug() {
		// 20.4.3 : Ladybug en premier, puis chaque fantôme humain, au même écart que l'ancien fantôme unique
		assertThat(StatusGameView.slotX(0, 46, 2)).isEqualTo(2);
		assertThat(StatusGameView.slotX(1, 46, 2)).isEqualTo(2 + 46 + StatusGameView.SLOT_GAP);
		assertThat(StatusGameView.slotX(3, 46, 2)).isEqualTo(2 + 3 * (46 + StatusGameView.SLOT_GAP));
	}

	@Test
	void theBandCanDrawThreeHumanGhosts() {
		var view = new StatusGameView();
		view.setSize(672, 50);
		var sprite = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
		view.setImageLadybugPlayer(sprite);
		view.setGhostPlayers(List.of(new StatusGameView.PlayerLives(sprite, 3),
		        new StatusGameView.PlayerLives(sprite, 2), new StatusGameView.PlayerLives(sprite, 1)));
		var image = new BufferedImage(672, 50, BufferedImage.TYPE_INT_RGB);
		var graphics = image.createGraphics();

		assertThatCode(() -> view.display(graphics)).doesNotThrowAnyException();
		graphics.dispose();
	}
}
