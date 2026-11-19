package com.kycox.game.tools.timer;

import com.kycox.game.constant.game.GameMainConstants;

/**
 * Compte à rebours en battements de la boucle du jeu (étape 3.5.1).
 *
 * <p>Un battement dure {@link GameMainConstants#PACE} ms. Au lieu de lancer une
 * minuterie dans un autre fil d'exécution (« réveille-moi dans 2,5 s »), on
 * demande « préviens-moi dans 63 battements » et c'est la boucle du jeu qui
 * décompte, en appelant {@link #tick()} à chaque battement. C'est le pattern
 * <b>Game Loop / Update Method</b> : une seule horloge fait avancer tout le monde.
 *
 * <p>Fiche : {@code doc/patterns/game-loop.md}.
 *
 * <p>Même idée que {@code LadybugDying}, qui compte déjà l'agonie de Ladybug en
 * battements.
 */
public class TickCountdown {
	private int remainingTicks;

	/**
	 * Nombre de battements qui couvre une durée en millisecondes, arrondi au
	 * battement supérieur : 2 500 ms → 63 battements de 40 ms. C'est aussi ce que
	 * faisait l'ancienne minuterie : son effet n'était vu qu'au battement suivant.
	 */
	public static int ticksFor(long milliseconds) {
		if (milliseconds <= 0) {
			return 0;
		}
		return (int) ((milliseconds + GameMainConstants.PACE - 1) / GameMainConstants.PACE);
	}

	/** Lance le compte à rebours pour une durée en millisecondes. */
	public void start(long milliseconds) {
		startTicks(ticksFor(milliseconds));
	}

	/** Lance le compte à rebours pour un nombre de battements. */
	public void startTicks(int ticks) {
		remainingTicks = Math.max(ticks, 0);
	}

	/** Arrête le compte à rebours sans qu'il arrive à son terme. */
	public void stop() {
		remainingTicks = 0;
	}

	public boolean isRunning() {
		return remainingTicks > 0;
	}

	public int getRemainingTicks() {
		return remainingTicks;
	}

	/**
	 * Avance d'un battement.
	 *
	 * @return vrai uniquement au battement où le compte à rebours arrive à zéro
	 */
	public boolean tick() {
		if (remainingTicks <= 0) {
			return false;
		}
		remainingTicks--;
		return remainingTicks == 0;
	}
}
