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

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import com.kycox.game.model.CurrentGameStatus;

/**
 * Timer qui permet d'afficher le score un certain temps à l'écran
 *
 */
public class BeginingTimer {
	
	@Inject
	private CurrentGameStatus currentGameStatus;
	/**
	 * Class TimerTask
	 */
	private class WaitTimer extends TimerTask {
		@Override
		public void run() {
			timer.cancel();
			timer.purge();
			currentGameStatus.setInGame();
		}
	}

	private Timer		   timer = new Timer(true);

	public BeginingTimer() {}

	/**
	 * Arrete le super power : le niveau est terminé, ou bien un autre super power a
	 * été pris
	 */
	public void forcedStop() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
		}
	}

	/**
	 * Lance le timer du score incrément
	 *
	 * @param duration temps en milli secondes
	 */
	public void launch(long duration, CurrentGameStatus currentGameStatus) {
		this.currentGameStatus = currentGameStatus;
		timer.schedule(new WaitTimer(), duration);
	}
}