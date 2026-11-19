/**
  Copyright (C) 2021  Frédéric Lanic frederic.lanic@outlook.fr

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.kycox.game.tools.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.kycox.game.constant.TimerStatus;
import com.kycox.game.constant.game.GameMainConstants;

/**
 * Durée du super-pouvoir de Ladybug, après une méga-gomme : les fantômes ont
 * peur, puis clignotent pendant les {@value #FLASH_DELAY} dernières secondes.
 *
 * <p>Étape 3.5.1c : le décompte se fait en battements de la boucle du jeu
 * ({@link TickCountdown}), et non plus avec deux {@code java.util.Timer} en
 * chaîne dans d'autres fils d'exécution. {@code GameModel} appelle
 * {@link #tick()} à chaque battement (pattern <b>Game Loop / Update Method</b>).
 * Pour 15 s : 375 battements, dont 125 de clignotement.
 *
 * <pre>
 *   RUN (peur)            STOPPING (clignotement)    STOP
 *   |---- 250 battements ----|---- 125 battements ----|
 * </pre>
 */
@Component
public class SuperPowerTimer {
	private static final Logger logger = LoggerFactory.getLogger(SuperPowerTimer.class);
	/** Délai de clignotement, en secondes, à la fin du super-pouvoir. */
	private static final int FLASH_DELAY = 5;
	private static final int FLASH_TICKS = TickCountdown.ticksFor(FLASH_DELAY * 1000L);

	private final TickCountdown countdown = new TickCountdown();

	/**
	 * Arrête le super-pouvoir : le niveau est terminé, ou bien un autre
	 * super-pouvoir a été pris.
	 */
	public void forcedStop() {
		countdown.stop();
	}

	/**
	 * Etat du super-pouvoir : {@code RUN} (peur), {@code STOPPING} (clignotement)
	 * ou {@code STOP}.
	 */
	public TimerStatus getStatus() {
		if (!countdown.isRunning()) {
			return TimerStatus.STOP;
		}
		return countdown.getRemainingTicks() > FLASH_TICKS ? TimerStatus.RUN : TimerStatus.STOPPING;
	}

	public boolean isStopped() {
		return getStatus() == TimerStatus.STOP;
	}

	public boolean isStopping() {
		return getStatus() == TimerStatus.STOPPING;
	}

	/**
	 * Lance le super-pouvoir pour une durée en secondes (clignotement compris).
	 */
	public void launch(int seconds) {
		countdown.start(seconds * 1000L);
	}

	/**
	 * Avance d'un battement ({@value GameMainConstants#PACE} ms). Sondes de log
	 * (comme en 3.4) au début du clignotement et à la fin du super-pouvoir.
	 */
	public void tick() {
		if (countdown.tick()) {
			logger.info("Super power is over");
		} else if (countdown.getRemainingTicks() == FLASH_TICKS) {
			logger.info("Super power is flashing");
		}
	}
}
