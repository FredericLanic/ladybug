package com.kycox.game.testutil;

import static com.kycox.game.testutil.GameScript.DOWN;
import static com.kycox.game.testutil.GameScript.LEFT;
import static com.kycox.game.testutil.GameScript.RIGHT;
import static com.kycox.game.testutil.GameScript.UP;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Preuve du déterminisme (étape 3.5.2c, reportée de 3.5.1) : même graine et
 * mêmes touches, même partie, au battement près. C'est la condition du golden
 * master (3.5.2d) et, plus tard, du mode réseau.
 */
class DeterminismTest {
	/** 60 secondes de jeu. */
	private static final int BEATS = 1500;

	@TempDir
	Path saveDirectory;

	private static GameScript script() {
		return GameScript.soloLevelOne()
		        .at(240, LEFT).at(330, UP).at(420, RIGHT).at(520, DOWN).at(600, LEFT)
		        .at(700, UP).at(820, RIGHT).at(900, UP).at(1000, LEFT).at(1150, DOWN).at(1300, RIGHT);
	}

	private String play(long seed, String run) {
		try (var game = GameDriver.start(seed, saveDirectory.resolve(run))) {
			script().play(game, BEATS);
			return game.journal().text();
		}
	}

	@Test
	void sameSeedAndSameKeysGiveTheSameGame() {
		var first = play(42, "first");
		var second = play(42, "second");

		assertThat(second).isEqualTo(first);
		assertThat(first.lines().count()).isGreaterThan(100); // une vraie partie, pas un journal vide
	}

	@Test
	void anotherSeedGivesAnotherGame() {
		assertThat(play(7, "seed7")).isNotEqualTo(play(42, "seed42"));
	}
}
