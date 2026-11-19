package com.kycox.game.tools.timer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * Planificateur d'actions en battements (étape 3.5.1b).
 *
 * <p>Remplace {@link WaitAndDoActionAfterTimer} dans le modèle. L'ancienne
 * minuterie exécutait son action dans son propre fil d'exécution, à n'importe
 * quel moment, même au milieu d'un battement ou en même temps qu'une touche du
 * clavier (dette D22 : {@code GAME_START} et {@code LEVEL_START} dans le
 * désordre). Ici, l'action s'exécute dans la boucle du jeu, au début du
 * battement, sur le fil Swing : plus de concurrence.
 *
 * <p>Pattern <b>Game Loop / Update Method</b> : {@code GameModel} appelle
 * {@link #tick()} à chaque battement. Fiche : {@code doc/patterns/game-loop.md}.
 *
 * <pre>
 * // avant : un fil caché change le statut quand il veut
 * new WaitAndDoActionAfterTimer().launch(2500, currentGameStatus, CurrentProgramStatus.TO_LEVEL_START);
 * // après : la boucle décompte 63 battements, puis exécute l'action
 * tickScheduler.schedule(2500, currentGameStatus::setLevelStart);
 * </pre>
 */
@Component
public class TickScheduler {
	private record ScheduledAction(TickCountdown countdown, Runnable action) {
	}

	private final List<ScheduledAction> scheduledActions = new ArrayList<>();

	/**
	 * Planifie une action après une durée en millisecondes, convertie en battements
	 * ({@link TickCountdown#ticksFor(long)}). Une durée nulle exécute l'action au
	 * battement suivant.
	 */
	public void schedule(long milliseconds, Runnable action) {
		var countdown = new TickCountdown();
		countdown.startTicks(Math.max(TickCountdown.ticksFor(milliseconds), 1));
		scheduledActions.add(new ScheduledAction(countdown, action));
	}

	/**
	 * Avance d'un battement et exécute, dans l'ordre où elles ont été planifiées,
	 * les actions arrivées à échéance.
	 */
	public void tick() {
		// copie : une action peut en planifier une autre pendant la boucle
		for (var scheduled : List.copyOf(scheduledActions)) {
			if (scheduled.countdown().tick()) {
				scheduledActions.remove(scheduled);
				scheduled.action().run();
			}
		}
	}

	/** Nombre d'actions en attente (utile aux tests). */
	public int pendingCount() {
		return scheduledActions.size();
	}
}
