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
package com.kycox.game.constant;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import lombok.Getter;

/**
 * Enumération des sons à écouter dans le jeu
 *
 */
public enum Sounds {
	COMMON_TELEPORT("common_teleport.wav"), GAME_BEGIN_LEVEL("game_begin_level.wav"), GAME_SIREN_0("game_siren_0.wav"),
	GAME_SIREN_1("game_siren_1.wav"), GAME_SIREN_2("game_siren_2.wav"), GAME_SIREN_3("game_siren_3.wav"),
	GHOST_EATEN("ghost_eaten.wav"), GHOST_REGENERATE("ghost_regenerate.wav"), GHOST_SCARED("ghost_scared.wav"),
	GHOST_SURVIVOR("ghost_survivor.wav"), LADYBUG_CHOMP("ladybug_chomp.wav"), LADYBUG_EAT_FRUIT("ladybug_eatfruit.wav"),
	LADYBUG_EAT_GHOST("ladybug_eatghost.wav"), LADYBUG_EXTRA_PAC("ladybug_extralife.wav"),
	LADYBUG_INTERMISSION("ladybug_intermission.wav"), LADYBUG_IS_DYING("ladybug_death.wav");

	@Getter
	private Clip clip = null;

	/**
	 * Constructeur
	 *
	 * @param fileName
	 */
	private Sounds(String fileName) {
		try {
			URL				 url			  = Sounds.class.getClassLoader().getResource("sound/wav/" + fileName);
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
			// Get a sound clip resource.
			clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioInputStream);
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.getStackTrace();
		}
	}

	public void stopSound() {
		clip.stop();
		clip.setFramePosition(0);
	}
}
