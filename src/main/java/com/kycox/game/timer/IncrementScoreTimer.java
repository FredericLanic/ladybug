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

import com.kycox.game.score.Message;

/**
 * Timer qui permet d'afficher le score un certain temps à l'écran
 *
 */
public class IncrementScoreTimer {
	/**
	 * Class TimerTask
	 */
	private class WaitTimer extends TimerTask {
		@Override
		public void run() {
			timer.cancel();
			timer.purge();
			// Affecte au ScorePoint le fait qu'il n'est plus affichage
			scoreIncrement.setDying(true);
		}
	}

	private Message scoreIncrement;
	private Timer		   timer = new Timer(true);

	/**
	 * Constructeur
	 *
	 * @param scoreIncremenet : modèle du jeu
	 * @param numThread       : numéro du thread (1: principal, autrement
	 *                        clignottement)
	 */
	public IncrementScoreTimer(Message scoreIncremenet) {
		this.scoreIncrement = scoreIncremenet;
	}

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
	public void launch(int duration) {
		timer.schedule(new WaitTimer(), duration);
	}
}