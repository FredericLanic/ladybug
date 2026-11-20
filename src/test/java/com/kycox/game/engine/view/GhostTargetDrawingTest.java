package com.kycox.game.engine.view;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kycox.game.body.ghost.Ghost.MoveStyle;
import com.kycox.game.body.ghost.GhostColor;
import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.constant.ghost.GhostStatus;
import com.kycox.game.engine.controller.KeyboardController;
import com.kycox.game.engine.message.LadybugMessagesBundle;
import com.kycox.game.engine.view.ghost.GhostView;
import com.kycox.game.engine.view.ladybug.LadybugDyingView;
import com.kycox.game.engine.view.ladybug.LadybugView;
import com.kycox.game.engine.view.map.ScreenBlockView;
import com.kycox.game.preferences.LocalPreferences;
import com.kycox.game.snapshot.GhostSnapshot;
import com.kycox.game.tools.Position;

/**
 * Mode debug : un petit fantôme de la bonne couleur est dessiné sur la case
 * visée, relié au fantôme par un trait pointillé.
 */
class GhostTargetDrawingTest {
	private static final int BLOCK = GameMainConstants.BLOCK_SIZE;
	private static final Position TARGET = new Position(4, 1);

	private final CentralView view = new CentralView(mock(GhostView.class), mock(KeyboardController.class),
	        mock(LadybugDyingView.class), mock(LadybugView.class), mock(ScreenBlockView.class),
	        mock(LadybugMessagesBundle.class), new LocalPreferences());

	private static GhostSnapshot redGhostAtOrigin(Position target) {
		var origin = new Position(0, 0);
		return new GhostSnapshot("Blinky", GhostColor.RED, origin, origin, origin, GhostStatus.NORMAL, true, false,
		        false, false, 0, MoveStyle.MOVE_TO, 0, target);
	}

	private static BufferedImage draw(CentralView view, GhostSnapshot ghost) {
		var image = new BufferedImage(6 * BLOCK, 3 * BLOCK, BufferedImage.TYPE_INT_RGB); // fond noir
		var g2d = image.createGraphics();
		view.drawGhostTargets(g2d, List.of(ghost));
		g2d.dispose();
		return image;
	}

	/** Pixels rouges dans le carré du petit fantôme (une demi-case, centrée sur la case visée). */
	private static int redPixelsOnTarget(BufferedImage image) {
		var centerX = TARGET.x() * BLOCK + BLOCK / 2;
		var centerY = TARGET.y() * BLOCK + BLOCK / 2;
		var count = 0;
		for (var y = centerY - BLOCK / 4; y < centerY + BLOCK / 4; y++) {
			for (var x = centerX - BLOCK / 4; x < centerX + BLOCK / 4; x++) {
				var c = new Color(image.getRGB(x, y));
				if (c.getRed() > c.getBlue() + 50 && c.getRed() > c.getGreen() + 50) {
					count++;
				}
			}
		}
		return count;
	}

	@Test
	@DisplayName("un petit fantôme rouge est posé au centre de la case visée")
	void aSmallGhostIsDrawnOnTheTarget() {
		// le trait pointillé n'en colore qu'une trentaine : au-delà, c'est le petit fantôme
		assertThat(redPixelsOnTarget(draw(view, redGhostAtOrigin(TARGET)))).isGreaterThan(150);
	}

	@Test
	@DisplayName("sans destination, rien n'est dessiné")
	void nothingWithoutTarget() {
		var image = draw(view, redGhostAtOrigin(null));
		for (var y = 0; y < image.getHeight(); y++) {
			for (var x = 0; x < image.getWidth(); x++) {
				assertThat(image.getRGB(x, y) & 0xFFFFFF).isZero();
			}
		}
	}
}
