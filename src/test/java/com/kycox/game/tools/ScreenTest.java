package com.kycox.game.tools;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Rectangle;

import org.junit.jupiter.api.Test;

/**
 * Dimensions de la fenêtre (étape 2.8, dette D18) : le labyrinthe et la zone du
 * bas tiennent dans la place disponible, et le bloc est centré.
 */
class ScreenTest {

	private static double totalHeight(Screen screen) {
		return screen.getTopHeight() + screen.getEdgeGameSide() + screen.getBottomZoneHeight()
		        + screen.getBottomGapHeight();
	}

	@Test
	void macBookUsableAreaFitsTheWholeMazeAndIsCentered() {
		// écran du Mac de Frédéric : 956 px de haut, 33 px de barre de menus
		var screen = new Screen(new Rectangle(0, 33, 1470, 923));

		assertThat(screen.getEdgeGameSide()).isEqualTo(720);
		assertThat(screen.getBottomZoneHeight()).isEqualTo(101);
		assertThat(screen.getTopHeight()).isEqualTo(51);
		assertThat(screen.getBottomGapHeight()).isEqualTo(51);
		assertThat(totalHeight(screen)).isEqualTo(923);
	}

	@Test
	void bottomZoneGrowsWithTheScreenUpToThreeBlocks() {
		// HP 27w : 1 080 px
		var screen = new Screen(new Rectangle(0, 0, 1920, 1080));

		assertThat(screen.getBottomZoneHeight()).isEqualTo(144);
		assertThat(screen.getTopHeight()).isEqualTo(108);
		assertThat(screen.getBottomGapHeight()).isEqualTo(108);
	}

	@Test
	void bottomZoneNeverGoesBelowTwoBlocks() {
		var screen = new Screen(new Rectangle(0, 0, 1366, 768));

		assertThat(screen.getBottomZoneHeight()).isEqualTo(96);
		assertThat(screen.getTopHeight()).isZero();
		assertThat(screen.getBottomGapHeight()).isZero();
	}

	@Test
	void windowGoesOnTheGivenScreen() {
		// second écran au-dessus du premier (comme le HP 27w)
		var secondScreen = new Rectangle(-242, -1080, 1920, 1080);

		assertThat(new Screen(secondScreen).getUsableBounds()).isEqualTo(secondScreen);
	}
}
