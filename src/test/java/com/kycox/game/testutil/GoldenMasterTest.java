package com.kycox.game.testutil;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Le rapport d'écart du golden master montre la première ligne qui diffère.
 */
class GoldenMasterTest {

	@Test
	void reportShowsTheFirstDifferentLineWithContext() {
		var expected = List.of("SEED 42", "b0001 STATUS PROGRAM_STARTING", "b0235 SCORE 1 (+1)", "b0244 SCORE 2 (+1)");
		var actual = List.of("SEED 42", "b0001 STATUS PROGRAM_STARTING", "b0235 SCORE 1 (+1)", "b0244 SCORE 3 (+2)");

		var report = GoldenMaster.firstDifference(expected, actual);

		assertThat(report).contains("line 4").contains("b0235 SCORE 1 (+1)")
		        .contains("- expected: b0244 SCORE 2 (+1)").contains("+ actual:   b0244 SCORE 3 (+2)");
	}

	@Test
	void reportHandlesAShorterJournal() {
		var report = GoldenMaster.firstDifference(List.of("SEED 42", "b0001 STATUS X"), List.of("SEED 42"));

		assertThat(report).contains("+ actual:   (end of journal)");
	}
}
