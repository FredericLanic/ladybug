package com.kycox.game.engine.view.map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.kycox.game.constant.fruits.Fruits;
import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.constant.level.LevelRendering;
import com.kycox.game.engine.view.map.rendering.DisplayRendering;
import com.kycox.game.engine.view.map.rendering.LinesRendering;
import com.kycox.game.engine.view.map.rendering.TilesRendering492;
import com.kycox.game.engine.view.map.rendering.TilesRendering584;
import com.kycox.game.engine.view.map.rendering.TilesRenderingRedLines;
import com.kycox.game.snapshot.CellSnapshot;
import com.kycox.game.snapshot.MazeSnapshot;
import com.kycox.game.tools.Position;

/**
 * D25 : pendant les phases allumées de l'effet « vie gagnée », les murs sont
 * dorés, y compris quand ils sont dessinés en images, en gardant leur texture
 * (une pierre reste une pierre, mais dorée) ; les couloirs restent noirs.
 */
class ScreenBlockViewTest {
	private static final int SIZE = GameMainConstants.BLOCK_SIZE;

	private final ScreenBlockView view = new ScreenBlockView(mock(Fruits.class),
	        new DisplayRendering(new MazeOutline(), new LinesRendering(), new TilesRendering492(),
	                new TilesRendering584(), new TilesRenderingRedLines()));

	private static MazeSnapshot oneCell(boolean wall) {
		var cell = new CellSnapshot(new Position(0, 0), false, false, false, false, wall, false, false, false, false,
		        GameMainConstants.NOFRUITID);
		return new MazeSnapshot(1, 1, 1, List.of(cell));
	}

	private BufferedImage draw(MazeSnapshot maze, LevelRendering rendering, boolean goldenWalls) {
		var image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB); // fond noir
		var g2d = image.createGraphics();
		view.display(g2d, maze, rendering, 0, 0, goldenWalls);
		g2d.dispose();
		return image;
	}

	private Color centerAfterDrawing(MazeSnapshot maze, boolean goldenWalls) {
		return new Color(draw(maze, LevelRendering.TILES_RENDERING_492, goldenWalls).getRGB(SIZE / 2, SIZE / 2));
	}

	private static boolean isGolden(Color color) {
		return color.getRed() >= color.getGreen() && color.getGreen() > color.getBlue()
		        && color.getRed() > color.getBlue() + 60;
	}

	/** Écart type de la luminosité des pixels : mesure le contraste, donc la texture. */
	private static double luminanceSpread(BufferedImage image) {
		var n = image.getWidth() * image.getHeight();
		var sum = 0.0;
		var sumOfSquares = 0.0;
		for (var y = 0; y < image.getHeight(); y++) {
			for (var x = 0; x < image.getWidth(); x++) {
				var c = new Color(image.getRGB(x, y));
				var l = 0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue();
				sum += l;
				sumOfSquares += l * l;
			}
		}
		var mean = sum / n;
		return Math.sqrt(Math.max(0, sumOfSquares / n - mean * mean));
	}

	@ParameterizedTest(name = "{0}")
	@EnumSource(value = LevelRendering.class, names = { "TILES_RENDERING_492", "TILES_RENDERING_584",
	        "TILES_RENDERING_REDLINES" })
	@DisplayName("un mur doré garde sa texture : au moins la moitié de son contraste")
	void aGoldenWallKeepsItsTexture(LevelRendering rendering) {
		var normal = luminanceSpread(draw(oneCell(true), rendering, false));
		var golden = luminanceSpread(draw(oneCell(true), rendering, true));
		assertThat(golden).as("contraste avant %.1f, après %.1f", normal, golden).isGreaterThanOrEqualTo(normal / 2 - 0.5);
	}

	@Test
	@DisplayName("un pixel plus clair reste plus clair une fois doré")
	void brighterStaysBrighter() {
		var dark = new Color(ScreenBlockView.toGold(new Color(60, 60, 60).getRGB()));
		var bright = new Color(ScreenBlockView.toGold(new Color(200, 200, 200).getRGB()));
		assertThat(bright.getRed() + bright.getGreen()).isGreaterThan(dark.getRed() + dark.getGreen());
		assertThat(isGolden(dark)).isTrue();
		assertThat(isGolden(bright)).isTrue();
	}

	@Test
	@DisplayName("un pixel transparent reste transparent (les couloirs ne bougent pas)")
	void transparentStaysTransparent() {
		assertThat(ScreenBlockView.toGold(0x00123456)).isEqualTo(0x00123456);
	}

	@Test
	@DisplayName("un mur dessiné en image devient doré pendant l'effet")
	void anImageWallTurnsGolden() {
		var normal = centerAfterDrawing(oneCell(true), false);
		var golden = centerAfterDrawing(oneCell(true), true);

		assertThat(normal).isNotEqualTo(Color.BLACK);
		assertThat(isGolden(normal)).isFalse();
		assertThat(isGolden(golden)).as("couleur obtenue : %s", golden).isTrue();
	}

	@Test
	@DisplayName("un couloir reste noir pendant l'effet")
	void aCorridorStaysBlack() {
		assertThat(centerAfterDrawing(oneCell(false), true)).isEqualTo(Color.BLACK);
	}
}
