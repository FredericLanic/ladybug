package com.kycox.game.testutil;

import static com.kycox.game.testutil.GameScript.DOWN;
import static com.kycox.game.testutil.GameScript.GHOST_DOWN;
import static com.kycox.game.testutil.GameScript.GHOST_LEFT;
import static com.kycox.game.testutil.GameScript.GHOST_RIGHT;
import static com.kycox.game.testutil.GameScript.GHOST_UP;
import static com.kycox.game.testutil.GameScript.LEFT;
import static com.kycox.game.testutil.GameScript.RIGHT;
import static com.kycox.game.testutil.GameScript.UP;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Golden master du jeu (étape 3.5.2d) : deux parties écrites à l'avance, dont
 * le journal doit rester identique à la référence validée par Frédéric.
 * Un écart signale un changement de comportement : bug à corriger, ou nouvelle
 * référence à valider ({@code -Dgolden.update=true}).
 */
class GoldenMasterGameTest {
	/** Deux minutes de jeu. */
	private static final int BEATS = 3000;

	@TempDir
	Path saveDirectory;

	@Test
	void soloGame() {
		var script = GameScript.soloLevelOne()
		        .at(240, LEFT).at(330, UP).at(420, RIGHT).at(520, DOWN).at(600, LEFT).at(700, UP)
		        .at(820, RIGHT).at(900, UP).at(1000, LEFT).at(1150, DOWN).at(1300, RIGHT).at(1450, UP)
		        .at(1600, LEFT).at(1750, DOWN).at(1900, RIGHT).at(2050, UP).at(2200, LEFT).at(2400, DOWN)
		        .at(2600, RIGHT).at(2800, UP);
		try (var game = GameDriver.start(42, saveDirectory)) {
			script.play(game, BEATS);
			GoldenMaster.assertMatches("solo-seed42", game.journal().text());
		}
	}

	@Test
	void twoPlayersGame() {
		var script = GameScript.twoPlayersLevelOne()
		        .at(240, LEFT).at(250, GHOST_DOWN).at(330, UP).at(400, GHOST_LEFT).at(420, RIGHT)
		        .at(520, DOWN).at(560, GHOST_DOWN).at(600, LEFT).at(700, UP).at(720, GHOST_RIGHT)
		        .at(820, RIGHT).at(880, GHOST_UP).at(900, UP).at(1000, LEFT).at(1040, GHOST_LEFT)
		        .at(1150, DOWN).at(1200, GHOST_DOWN).at(1300, RIGHT).at(1450, UP).at(1500, GHOST_RIGHT)
		        .at(1600, LEFT).at(1750, DOWN).at(1800, GHOST_UP).at(1900, RIGHT).at(2050, UP)
		        .at(2100, GHOST_LEFT).at(2200, LEFT).at(2400, DOWN).at(2450, GHOST_DOWN).at(2600, RIGHT)
		        .at(2800, UP);
		try (var game = GameDriver.start(42, saveDirectory)) {
			script.play(game, BEATS);
			GoldenMaster.assertMatches("two-players-seed42", game.journal().text());
		}
	}
}
