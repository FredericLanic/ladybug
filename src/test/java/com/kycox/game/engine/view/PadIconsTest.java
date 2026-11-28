package com.kycox.game.engine.view;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

/**
 * Les icônes Menu (≡) et Affichage (⧉) de l'aide de la manette (étape 20.1) :
 * la police du jeu n'a pas ces symboles, ils sont dessinés.
 */
class PadIconsTest {
	private static final Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 38);

	private static BufferedImage draw(String line) {
		var image = new BufferedImage(400, 60, BufferedImage.TYPE_INT_RGB); // fond noir
		var g2d = image.createGraphics();
		g2d.setFont(FONT);
		g2d.setColor(Color.WHITE);
		PadIcons.draw(g2d, line, 0, 45, g2d.getFontMetrics());
		g2d.dispose();
		return image;
	}

	private static int whitePixels(BufferedImage image) {
		var count = 0;
		for (var y = 0; y < image.getHeight(); y++) {
			for (var x = 0; x < image.getWidth(); x++) {
				if ((image.getRGB(x, y) & 0xFFFFFF) != 0) {
					count++;
				}
			}
		}
		return count;
	}

	@Test
	void aLineIsCutIntoTextAndIcons() {
		assertThat(PadIcons.parts("{MENU}: pAUSE")).containsExactly("{MENU}", ": pAUSE");
		assertThat(PadIcons.parts("fI OU {VIEW} POUR aIDE")).containsExactly("fI OU ", "{VIEW}", " POUR aIDE");
		assertThat(PadIcons.parts("a: oUI")).containsExactly("a: oUI");
	}

	@Test
	void bothIconsAreDrawnAndDiffer() {
		var menu = draw(PadIcons.MENU);
		var view = draw(PadIcons.VIEW);
		assertThat(whitePixels(menu)).isPositive();
		assertThat(whitePixels(view)).isPositive();
		assertThat(whitePixels(menu)).isNotEqualTo(whitePixels(view));
	}

	@Test
	void theIconTakesTheWordsPlaceInTheWidth() {
		var metrics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).createGraphics().getFontMetrics(FONT);
		var text = ": pAUSE";
		assertThat(PadIcons.width(PadIcons.MENU + text, metrics))
		        .isGreaterThan(metrics.stringWidth(text))
		        .isLessThan(metrics.stringWidth(PadIcons.MENU + text));
	}

	@Test
	void theFourDpadIconsAreDrawnAndAllDiffer() {
		var up = draw(PadIcons.DPAD_UP);
		var down = draw(PadIcons.DPAD_DOWN);
		var left = draw(PadIcons.DPAD_LEFT);
		var right = draw(PadIcons.DPAD_RIGHT);
		for (var image : new BufferedImage[] { up, down, left, right }) {
			assertThat(whitePixels(image)).isPositive();
		}
		// même quantité d'encre (une branche remplie), mais pas au même endroit
		assertThat(sameImage(up, down) || sameImage(up, left) || sameImage(up, right) || sameImage(down, left)
		        || sameImage(down, right) || sameImage(left, right)).isFalse();
	}

	private static boolean sameImage(BufferedImage a, BufferedImage b) {
		for (var y = 0; y < a.getHeight(); y++) {
			for (var x = 0; x < a.getWidth(); x++) {
				if (a.getRGB(x, y) != b.getRGB(x, y)) {
					return false;
				}
			}
		}
		return true;
	}
}
