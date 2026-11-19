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
package com.kycox.game.constant.sounds;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enumération des sons à écouter dans le jeu.
 *
 * <p>Chaque son n'est chargé qu'à sa première écoute ({@link #getClip()}), et
 * plus dans le constructeur (étape 3.5.2). Le modèle du jeu utilise ces valeurs
 * comme de simples noms de sons ({@code NewSounds}) et ne charge donc jamais
 * rien. Sans carte son (serveur de CI, poste sans sortie audio), le chargement
 * échoue proprement : le jeu reste muet au lieu de planter.
 */
public enum Sounds {
	COMMON_TELEPORT("common_teleport.wav"), GAME_BEGIN_LEVEL("game_begin_level.wav"), GAME_SIREN_0("game_siren_0.wav"),
	GAME_SIREN_1("game_siren_1.wav"), GAME_SIREN_2("game_siren_2.wav"), GAME_SIREN_3("game_siren_3.wav"),
	GHOST_EATEN("ghost_eaten.wav"), GHOST_REGENERATE("ghost_regenerate.wav"), GHOST_SCARED("ghost_scared.wav"),
	GHOST_SURVIVOR("ghost_survivor.wav"), LADYBUG_CHOMP("ladybug_chomp.wav"), LADYBUG_EAT_FRUIT("ladybug_eatfruit.wav"),
	LADYBUG_EAT_GHOST("ladybug_eatghost.wav"), LADYBUG_EXTRA_PAC("ladybug_extralife.wav"),
	LADYBUG_INTERMISSION("ladybug_intermission.wav"), LADYBUG_IS_DYING("ladybug_death.wav");

	private static final Logger logger = LoggerFactory.getLogger(Sounds.class);

	private final String fileName;
	private Clip clip;
	private boolean loadFailed;

	Sounds(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Le son, chargé à la première demande ; {@code null} si aucune sortie audio
	 * n'est disponible.
	 */
	public synchronized Clip getClip() {
		if (clip == null && !loadFailed) {
			try {
				var url = Sounds.class.getClassLoader().getResource("sound/wav/" + fileName);
				var audioInputStream = AudioSystem.getAudioInputStream(url);
				var newClip = AudioSystem.getClip();
				newClip.open(audioInputStream);
				clip = newClip;
			} catch (Exception e) {
				// IllegalArgumentException sans carte son, LineUnavailableException, IOException…
				loadFailed = true;
				logger.warn("Sound {} unavailable, the game stays silent for it: {}", fileName, e.toString());
			}
		}
		return clip;
	}

	public synchronized void stopSound() {
		if (clip != null) {
			clip.stop();
			clip.setFramePosition(0);
		}
	}
}
