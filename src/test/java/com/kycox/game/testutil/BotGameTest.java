package com.kycox.game.testutil;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.kycox.game.constant.game.GameStatus;
import com.kycox.game.constant.sounds.Sounds;

/**
 * Partie jouée par le bot qui vise (étape 3.5.2e) : les règles du jeu sont
 * vérifiées à chaque battement, et la partie de référence atteint le niveau 2
 * (décision C26).
 */
class BotGameTest {
	/** Au plus 5 minutes de jeu pour atteindre le niveau 2. */
	private static final int MAX_BEATS = 5 * 60 * 25;
	/** Après l'arrivée au niveau 2, encore 10 s dans le journal. */
	private static final int BEATS_AFTER_LEVEL_TWO = 250;

	@TempDir
	Path saveDirectory;

	@Test
	void botReachesLevelTwoAndNoRuleIsBroken() {
		var seed = 42L;
		var bot = new AimingBot();
		var rules = new GameRules(seed);
		try (var game = GameDriver.start(seed, saveDirectory)) {
			GameScript.soloLevelOne().play(game, GameScript.START_BEAT + 2);
			var levelTwoBeat = -1;
			while (game.beatCount() < MAX_BEATS) {
				if (game.status() == GameStatus.IN_GAME) {
					var direction = bot.decide(game.model());
					if (direction != null) {
						game.inputs().ladybug().request(direction);
					}
				}
				game.beat();
				rules.check(game);
				if (levelTwoBeat < 0 && game.model().getCurrentProgramStatus().getNumLevel() == 2) {
					levelTwoBeat = game.beatCount();
				}
				if (levelTwoBeat > 0 && game.beatCount() >= levelTwoBeat + BEATS_AFTER_LEVEL_TWO) {
					break;
				}
			}
			assertThat(levelTwoBeat).as("beat where level 2 is reached").isPositive();
			// chaque son du jeu est demandé au moins une fois (3.5.3a), sauf GHOST_SURVIVOR,
			// déclaré mais jamais utilisé dans le code (dette D24)
			var journal = game.journal().text();
			assertThat(Arrays.stream(Sounds.values()).filter(sound -> sound != Sounds.GHOST_SURVIVOR))
			        .allSatisfy(sound -> assertThat(journal).as("sound " + sound).contains(" SOUND " + sound + "\n"));
			GoldenMaster.assertMatches("bot-seed42", game.journal().text());
		}
	}
}
