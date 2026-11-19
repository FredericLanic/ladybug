package com.kycox.game.testutil;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.kycox.game.constant.game.GameMainConstants;

/**
 * Le journal note ce qui change dans la partie (étape 3.5.2b).
 */
class GameJournalTest {
	@TempDir
	Path saveDirectory;

	@Test
	void journalFollowsTheGame() {
		try (var game = GameDriver.start(42, saveDirectory)) {
			GameDriverTest.startLevelOne(game);
			game.keys().setLadybugRequest(GameMainConstants.POINT_LEFT);
			game.beats(100);

			var lines = game.journal().lines();
			assertThat(lines.get(0)).isEqualTo("SEED 42");
			assertThat(lines).anyMatch(line -> line.endsWith("STATUS PROGRAM_PRESENTATION"))
			        .anyMatch(line -> line.endsWith("STATUS IN_GAME"))
			        .anyMatch(line -> line.contains(" LEVEL 1"))
			        .anyMatch(line -> line.contains(" SCORE "))
			        .anyMatch(line -> line.contains(" STATE ladybug=("));
			// une ligne STATE par seconde de jeu
			var beats = game.beatCount();
			assertThat(lines.stream().filter(line -> line.contains(" STATE ")).count())
			        .isEqualTo(beats / GameJournal.STATE_PERIOD);
			System.out.println(game.journal().text());
		}
	}
}
