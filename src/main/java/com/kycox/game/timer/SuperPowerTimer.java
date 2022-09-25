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
package com.kycox.game.timer;

import com.kycox.game.constant.TimerStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Double timer utilisé pour afficher les fantômes qui ont peur et qui
 * clignottent ensuite. 5 Secondes avant la fin, les fantômes vont clignoter
 *
 */
@Component
public class SuperPowerTimer {
	/**
	 * Class TimerTask
	 */
	private class PowerTimer extends TimerTask {
		@Override
		public void run() {
			timer.cancel();
			timer.purge();
			if (numTimer == 1) {
				// Fin du premier timer
				superPowerTimer = new SuperPowerTimer(numTimer + 1);
				superPowerTimer.launch(FLASH_DELAY);
			} else {
				// Fin du second timer
				timerStatus = TimerStatus.STOP;
				superPowerTimer = null;
			}
		}
	}

	// délai en second de clignottements
	private static final int FLASH_DELAY = 5;
	// num�ro du timer
	@Setter
	@Getter
	private int numTimer;
	// objet powerFlashTimer utilisé pour le 2i�me timer
	private SuperPowerTimer superPowerTimer = null;
	// etat du superPower courant (1ier ou 2ieme selon le cas)
	private TimerStatus timerStatus = TimerStatus.STOP;
	// Timer du power
	private Timer timer;

	public SuperPowerTimer() {
		numTimer = 1;
	}

	/**
	 * Constructeur
	 *
	 * @param numTimer : numéro du thread (1: principal, autrement clignottement)
	 */
	public SuperPowerTimer(int numTimer) {
		this.numTimer = numTimer;
	}

	/**
	 * Arrete le super power : le niveau est termin�, ou bien un autre super power a
	 * été pris
	 */
	public void forcedStop() {
		// Arrêt forcé du premier timer
		if (timer != null) {
			timer.cancel();
			timer.purge();
		}
		// Arrêt forcé du second timer s'il existe
		if (superPowerTimer != null) {
			superPowerTimer.forcedStop();
			superPowerTimer = null;
		}
		timerStatus = TimerStatus.STOP;
	}

	/**
	 * Return the status of the super power time : see SuperPowerEnum
	 *
	 * @return
	 */
	public TimerStatus getStatus() {
		// Priorité au deuxième timer s'il existe
		if (superPowerTimer != null) {
			return superPowerTimer.getStatus();
		}
		return timerStatus;
	}

	public boolean isStopped() {
		return getStatus() == TimerStatus.STOP;
	}

	public boolean isStopping() {
		return getStatus() == TimerStatus.STOPPING;
	}

	/**
	 * launch the delay
	 */
	public void launch(int seconds) {
		if (numTimer == 1) {
			// Début du premier timer
			seconds = seconds - FLASH_DELAY;
			timerStatus = TimerStatus.RUN;
		} else {
			// Début du second timer
			timerStatus = TimerStatus.STOPPING;
		}
		// Création du timer
		timer = new Timer(true);
		// lancement du timer après n secondes
		timer.schedule(new PowerTimer(), (long) (seconds) * 1000);
	}
}