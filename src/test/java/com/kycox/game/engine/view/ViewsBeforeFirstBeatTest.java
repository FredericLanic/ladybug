package com.kycox.game.engine.view;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

import com.kycox.game.engine.controller.KeyboardController;
import com.kycox.game.engine.message.LadybugMessagesBundle;
import com.kycox.game.engine.view.map.ScreenBlockView;
import com.kycox.game.engine.view.ghost.GhostView;
import com.kycox.game.engine.view.ladybug.LadybugDyingView;
import com.kycox.game.engine.view.ladybug.LadybugView;

/**
 * Étape 4.1 : la fenêtre est dessinée avant le premier battement du jeu. La vue
 * centrale ne connaît pas encore le modèle ; elle doit peindre son fond noir sans
 * planter. Une exception pendant le dessin interrompait le dessin de toute la
 * fenêtre : les zones du haut et de droite restaient blanches.
 */
class ViewsBeforeFirstBeatTest {

	@Test
	void theCentralViewPaintsABlackBackgroundBeforeTheFirstBeat() {
		var view = new CentralView(mock(GhostView.class), mock(KeyboardController.class),
		        mock(LadybugDyingView.class), mock(LadybugView.class), mock(ScreenBlockView.class),
		        mock(LadybugMessagesBundle.class));
		view.setBackground(Color.BLACK);
		view.setSize(100, 100);
		var image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
		var graphics = image.createGraphics();
		graphics.setColor(Color.WHITE); // une zone « blanche », comme celles vues au lancement
		graphics.fillRect(0, 0, 100, 100);

		assertThatCode(() -> view.paintComponent(graphics)).doesNotThrowAnyException();
		graphics.dispose();
		assertThat(new Color(image.getRGB(50, 50))).isEqualTo(Color.BLACK);
	}
}
