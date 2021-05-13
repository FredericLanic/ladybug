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
package com.kycox.game.sound;

import javax.sound.sampled.Clip;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Processus pour écouter le clip
 *
 * @see Thread
 */
public class ListenSound extends Thread {
	private static final Log logger = LogFactory.getLog(ListenSound.class);
	// clip partagé par les Threads
	private Clip clip = null;
	private long microsecondLength = 0;

	/**
	 * Constructeur
	 *
	 * @param clip
	 */
	public ListenSound(Clip clip) {
		this.clip = clip;
		microsecondLength = clip.getMicrosecondLength();
	}

	/**
	 * The sound is load and play in a thread no slow down the engine.
	 */
	@Override
	public void run() {
		boolean end = false;
		try {
			// Si le clip n'est pas déjà lancé
			if (!clip.isRunning()) {
				clip.start();
				while (!end) {
					Thread.sleep(1);
					if (clip.getMicrosecondPosition() >= microsecondLength)
						end = true;
				}
				// Réinitialisation du clip pour une prochaine fois
				if (clip.isActive()) {
					clip.flush();
					clip.stop();
				}
				clip.setFramePosition(0);
			}
		} catch (InterruptedException e) {
			logger.error("Interrupted - playStartJingle" + e);
			Thread.currentThread().interrupt();
		}
	}
}
