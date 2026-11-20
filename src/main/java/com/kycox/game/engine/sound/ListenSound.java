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
package com.kycox.game.engine.sound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.Clip;

public class ListenSound extends Thread {
	private static final Logger logger = LoggerFactory.getLogger(ListenSound.class);
	private final Clip clip;
	private long microsecondLength = 0;

	public ListenSound(Clip clip) {
		this.clip = clip;
		microsecondLength = clip.getMicrosecondLength();
	}

	@Override
	public void run() {
		var end = false;
		try {
			// Si le clip n'est pas déjà lancé
			if (!clip.isRunning()) {
				clip.start();
				while (!end) {
					Thread.sleep(1);
					if (clip.getMicrosecondPosition() >= microsecondLength) {
						end = true;
					}
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
