package com.kycox.game.tools;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

/**
 * Outils d'images (étape 3.5.5) : redimensionner, superposer, pivoter.
 */
class ImageUtilsTest {

	private static BufferedImage image(int width, int height, Color color) {
		var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		var g = image.createGraphics();
		g.setColor(color);
		g.fillRect(0, 0, width, height);
		g.dispose();
		return image;
	}

	@Test
	void resizeGivesTheRequestedSize() {
		var resized = (BufferedImage) ImageUtils.resizeImage(image(48, 48, Color.RED), 18, 30);

		assertThat(resized.getWidth()).isEqualTo(18);
		assertThat(resized.getHeight()).isEqualTo(30);
		assertThat(new Color(resized.getRGB(9, 15))).isEqualTo(Color.RED);
	}

	@Test
	void appendDrawsTheSecondImageOverTheFirst() {
		var top = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
		top.setRGB(1, 1, Color.BLUE.getRGB());

		var merged = (BufferedImage) ImageUtils.appendImages(image(4, 4, Color.RED), top);

		assertThat(merged.getWidth()).isEqualTo(4);
		assertThat(new Color(merged.getRGB(1, 1))).isEqualTo(Color.BLUE);
		assertThat(new Color(merged.getRGB(3, 3))).isEqualTo(Color.RED);
	}

	@Test
	void rotateAQuarterTurnSwapsWidthAndHeight() {
		var source = image(4, 2, Color.GREEN);

		var rotated = (BufferedImage) ImageUtils.rotateImage(source, 90);

		assertThat(rotated.getWidth()).isEqualTo(2);
		assertThat(rotated.getHeight()).isEqualTo(4);
		assertThat(new Color(rotated.getRGB(1, 2), true).getAlpha()).isPositive();
	}

	@Test
	void rotateAHalfTurnKeepsTheSizeAndMovesThePixels() {
		var source = image(3, 3, Color.RED);
		source.setRGB(0, 0, Color.BLUE.getRGB());

		var rotated = (BufferedImage) ImageUtils.rotateImage(source, 180);

		assertThat(rotated.getWidth()).isEqualTo(3);
		assertThat(new Color(rotated.getRGB(2, 2))).isEqualTo(Color.BLUE);
		assertThat(new Color(rotated.getRGB(0, 0))).isEqualTo(Color.RED);
	}
}
