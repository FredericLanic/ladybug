package com.kycox.game.engine.view;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import com.kycox.game.snapshot.LobbySnapshot;
import com.kycox.game.snapshot.LobbySnapshot.RowSnapshot;

/**
 * Le dessin du salon (étape 20.3) : dessiné hors du jeu, sur une image, pour
 * vérifier qu'il ne plante pas et que les états se voient.
 */
class LobbyDrawingTest {
	private static final int WIDTH = 600;
	private static final int HEIGHT = 700;
	private static final Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 38);
	/** Le texte du jeu est la clé elle-même : suffisant pour dessiner. */
	private static final Function<String, String> MESSAGES = key -> key;

	private static BufferedImage draw(LobbySnapshot lobby, Function<String, Image> images) {
		var image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB); // fond noir
		var g2d = image.createGraphics();
		g2d.setFont(FONT);
		LobbyDrawing.draw(g2d, lobby, MESSAGES, images, WIDTH, HEIGHT, g2d.getFontMetrics());
		g2d.dispose();
		return image;
	}

	private static long colouredPixels(BufferedImage image) {
		var count = 0L;
		for (var y = 0; y < image.getHeight(); y++) {
			for (var x = 0; x < image.getWidth(); x++) {
				if ((image.getRGB(x, y) & 0xFFFFFF) != 0) {
					count++;
				}
			}
		}
		return count;
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

	private static Function<String, Image> sprite() {
		var image = new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
		var g2d = image.createGraphics();
		g2d.setColor(Color.RED);
		g2d.fillRect(0, 0, 20, 20);
		g2d.dispose();
		return name -> image;
	}

	private static LobbySnapshot lobby(List<RowSnapshot> rows, int activeRow, String column, boolean ready) {
		return new LobbySnapshot(rows, activeRow, column, ready);
	}

	@Test
	void anEmptyLobbyStillShowsItsTitleAndButtons() {
		assertThat(colouredPixels(draw(LobbySnapshot.empty(), name -> null))).isPositive();
	}

	@Test
	void aRowWithoutCharacterOrDeviceIsDrawn() {
		var rows = List.of(new RowSnapshot(null, null, false), new RowSnapshot("Ladybug", "ARROWS", false),
		        new RowSnapshot("Inky", null, true));
		assertThat(colouredPixels(draw(lobby(rows, 0, "CHARACTER", false), sprite()))).isPositive();
	}

	@Test
	void theActiveCellsChangeTheDrawing() {
		var rows = List.of(new RowSnapshot("Ladybug", "ARROWS", false), new RowSnapshot("Inky", "ZQSD", false));
		var character = draw(lobby(rows, 0, "CHARACTER", true), sprite());
		var controls = draw(lobby(rows, 0, "CONTROLS", true), sprite());
		var otherRow = draw(lobby(rows, 1, "CHARACTER", true), sprite());
		assertThat(sameImage(character, controls)).isFalse();
		assertThat(sameImage(character, otherRow)).isFalse();
	}

	@Test
	void theButtonsAreFocusedOnTheirOwnRow() {
		var rows = List.of(new RowSnapshot("Ladybug", "ARROWS", false));
		var none = draw(lobby(rows, 0, "CHARACTER", true), sprite());
		var play = draw(lobby(rows, 1, "PLAY", true), sprite());
		var back = draw(lobby(rows, 1, "BACK", true), sprite());
		assertThat(sameImage(none, play)).isFalse();
		assertThat(sameImage(play, back)).isFalse();
	}

	@Test
	void theMessageTellsWhatIsStillMissing() {
		var withoutLadybug = List.of(new RowSnapshot("Inky", "ZQSD", false));
		var withoutDevice = List.of(new RowSnapshot("Ladybug", null, false));
		var ready = List.of(new RowSnapshot("Ladybug", "ARROWS", false));
		var needLadybug = draw(lobby(withoutLadybug, 0, "CHARACTER", false), sprite());
		var needControls = draw(lobby(withoutDevice, 0, "CHARACTER", false), sprite());
		var allSet = draw(lobby(ready, 0, "CHARACTER", true), sprite());
		assertThat(sameImage(needLadybug, needControls)).isFalse();
		assertThat(sameImage(needControls, allSet)).isFalse();
	}

	@Test
	void aMissingPictureIsSkipped() {
		var rows = List.of(new RowSnapshot("Ladybug", "ARROWS", false));
		var withPicture = draw(lobby(rows, 0, "CHARACTER", true), sprite());
		var withoutPicture = draw(lobby(rows, 0, "CHARACTER", true), name -> null);
		assertThat(sameImage(withPicture, withoutPicture)).isFalse();
	}

	@Test
	void aGhostNameIsWrittenLikeTheGameFont() {
		assertThat(LobbyDrawing.label("Clyde", MESSAGES)).isEqualTo("cLYDE");
		assertThat(LobbyDrawing.label("Ladybug", MESSAGES)).isEqualTo("lobby.ladybug");
	}
}
