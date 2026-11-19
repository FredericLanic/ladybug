package com.kycox.game.tools.timer;

/**
 * Cadence de la boucle du jeu « à pas fixe » (étape 3.5.1b, pattern <b>Game
 * Loop</b>).
 *
 * <p>Le {@code javax.swing.Timer} ne réveille pas le jeu exactement toutes les
 * 40 ms : sur le Mac de Frédéric, un battement durait en réalité environ 43 ms.
 * Tant que les attentes étaient en temps réel, cela ne se voyait pas ; une fois
 * comptées en battements, elles duraient 7 % de trop (la musique du début de
 * niveau finissait avant la fin de l'attente et repartait).
 *
 * <p>Le pacer rattrape ce retard : à chaque réveil, il dit combien de battements
 * auraient dû passer depuis le précédent (en général 0 ou 1, parfois 2). Ainsi,
 * le jeu avance bien de 25 battements par seconde, quelle que soit la machine.
 * Après un long arrêt (mise en veille, débogueur), il ne rattrape pas tout :
 * au-delà de {@code maxCatchUp} battements, il repart de l'instant présent.
 */
public class BeatPacer {
	private final long beatNanos;
	private final int maxCatchUp;
	private long nextBeatNanos;
	private boolean started;

	/**
	 * @param beatMilliseconds durée d'un battement ({@code PACE})
	 * @param maxCatchUp       nombre maximum de battements rattrapés d'un coup
	 */
	public BeatPacer(long beatMilliseconds, int maxCatchUp) {
		this.beatNanos = beatMilliseconds * 1_000_000L;
		this.maxCatchUp = maxCatchUp;
	}

	/**
	 * Nombre de battements à jouer maintenant.
	 *
	 * @param nowNanos l'heure courante, en nanosecondes ({@link System#nanoTime()})
	 */
	public int beatsDue(long nowNanos) {
		if (!started) {
			started = true;
			nextBeatNanos = nowNanos;
		}
		var beats = 0;
		while (nowNanos - nextBeatNanos >= 0 && beats < maxCatchUp) {
			beats++;
			nextBeatNanos += beatNanos;
		}
		if (nowNanos - nextBeatNanos >= 0) {
			// trop de retard : on abandonne le rattrapage et on repart de maintenant
			nextBeatNanos = nowNanos + beatNanos;
		}
		return beats;
	}
}
