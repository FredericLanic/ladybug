package com.kycox.game.tools;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Point;

import org.junit.jupiter.api.Test;

/**
 * {@link Position} (étape 4.3) : une valeur immuable, qui calcule comme
 * {@code java.awt.Point} pour que le jeu ne change pas.
 */
class PositionTest {

	@Test
	void translateReturnsANewPositionAndLeavesTheOriginalUntouched() {
		var zero = new Position(0, 0);

		var moved = zero.translate(3, -2);

		assertThat(moved).isEqualTo(new Position(3, -2));
		assertThat(zero).isEqualTo(new Position(0, 0)); // D21 : la constante ne bouge plus
	}

	@Test
	void twoPositionsWithTheSameCoordinatesAreEqual() {
		assertThat(new Position(6, 4)).isEqualTo(new Position(6, 4)).hasSameHashCodeAs(new Position(6, 4));
	}

	@Test
	void distanceIsComputedExactlyLikeAwtPoint() {
		// les fantômes comparent des distances : le moindre écart changerait leurs choix
		int[][] cases = { { 0, 0, 3, 4 }, { 48, 96, 240, 0 }, { -5, 7, 13, -11 }, { 1, 1, 1, 1 } };
		for (int[] c : cases) {
			var expected = new Point(c[0], c[1]).distance(new Point(c[2], c[3]));

			assertThat(new Position(c[0], c[1]).distance(new Position(c[2], c[3]))).isEqualTo(expected);
		}
	}
}
