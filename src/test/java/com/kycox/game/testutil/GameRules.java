package com.kycox.game.testutil;


import com.kycox.game.tools.Position;
import com.kycox.game.constant.game.GameStatus;

/**
 * Règles vérifiées à chaque battement pendant une partie de bot (étape 3.5.2e).
 * En cas de violation, le message donne la graine et le battement, pour rejouer
 * exactement la même partie.
 */
final class GameRules {
	/** Au plus 60 s sans qu'il se passe rien (statut, score ou position). */
	static final int MAX_BEATS_WITHOUT_PROGRESS = 1500;

	private final long seed;
	private int lastScore;
	private String lastFingerprint = "";
	private int lastProgressBeat;

	GameRules(long seed) {
		this.seed = seed;
	}

	void check(GameDriver game) {
		var model = game.model();
		var beat = game.beatCount();
		var status = game.status();
		var score = model.getGameScore().getScore();
		var ladybug = model.getLadybug().getPositionBlock();

		if (status == GameStatus.IN_GAME) {
			if (score < lastScore) {
				fail(beat, "score decreased from " + lastScore + " to " + score);
			}
			var block = model.getMaze().getScreenBlock(ladybug);
			// dette D32 : ne jamais sortir du labyrinthe (une position négative
			// tomberait encore, par arrondi, sur une case du bord)
			var pixels = model.getLadybug().getPosition();
			if (block == null || pixels.x() < 0 || pixels.y() < 0) {
				fail(beat, "Ladybug is outside the maze at " + pixels);
			}
			if (block != null && block.isNotAccessible()) {
				fail(beat, "Ladybug is inside a wall at " + ladybug);
			}
		}
		lastScore = status == GameStatus.IN_GAME ? score : 0;

		var fingerprint = status + "|" + score + "|" + ladybug;
		if (!fingerprint.equals(lastFingerprint)) {
			lastFingerprint = fingerprint;
			lastProgressBeat = beat;
		} else if (beat - lastProgressBeat > MAX_BEATS_WITHOUT_PROGRESS) {
			fail(beat, "nothing happened for " + MAX_BEATS_WITHOUT_PROGRESS + " beats (status " + status + ")");
		}
	}

	private void fail(int beat, String message) {
		throw new AssertionError("Rule broken at beat " + beat + " (replay with seed " + seed + "): " + message);
	}
}
