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

import com.kycox.game.constant.Sounds;
import com.kycox.game.contract.GameModelForSounds;
import com.kycox.game.contract.NewSoundsForGameSounds;
import com.kycox.game.model.EventGameModel;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Observable;
import java.util.Observer;

/**
 * Gestion du son dans le jeu
 *
 */
@Component
public class GameSounds implements ApplicationListener<EventGameModel> {
//public class GameSounds implements ApplicationListener<EventGameModel> {
	private NewSoundsForGameSounds newSounds;

	/**
	 * Retourne le temps en millisecondes de la musique de la mort de ladybug
	 * Utilisé dans la cinématique KinematicLadybugDeath
	 *
	 * @return
	 */
	public long getLadybugAgonyDuration() {
		var clipLadybugDying = Sounds.LADYBUG_IS_DYING.getClip();
		return clipLadybugDying.getMicrosecondLength() / 1000;
	}

	public long getNewLifePocDuration() {
		var clipNewLife = Sounds.LADYBUG_EXTRA_PAC.getClip();
		return clipNewLife.getMicrosecondLength() / 1000;
	}

	public long getBeginningDuration() {
		var clipBeginning = Sounds.GAME_BEGIN_LEVEL.getClip();
		return clipBeginning.getMicrosecondLength() / 1000;
	}

	public long getIntermissionDuration() {
		var clipBeginning = Sounds.LADYBUG_INTERMISSION.getClip();
		return clipBeginning.getMicrosecondLength() / 1000;
	}

	/**
	 * Lancement des sons sélectionnés par le modèle
	 */
	public void playSounds() {
		if (newSounds.hasSound(Sounds.LADYBUG_IS_DYING)) {
			stopAllSounds();
		}
		// FIXME : je pense qu'on n'en a plus besoin
		if (newSounds.hasSound(Sounds.GAME_BEGIN_LEVEL)) {
			stopAllSoundsExcept(Sounds.GAME_BEGIN_LEVEL);
		}
		if (newSounds.hasSound(Sounds.LADYBUG_INTERMISSION)) {
			stopAllSoundsExcept(Sounds.LADYBUG_INTERMISSION);
		}
		var soundsEnums = Sounds.values();
		for (Sounds soundsEnum : soundsEnums) {
			if (newSounds.hasSound(soundsEnum)) {
				new ListenSound(soundsEnum.getClip()).start();
			}
		}
	}

	private void stopAllSounds() {
		var soundsEnums = Sounds.values();
		for (Sounds soundsEnum : soundsEnums) {
			soundsEnum.stopSound();
		}
	}

	private void stopAllSoundsExcept(Sounds sound) {
		var soundsEnums = Sounds.values();
		for (Sounds soundsEnum : soundsEnums) {
			if (!soundsEnum.equals(sound)) {
				soundsEnum.stopSound();
			}
		}
	}

	@Override
	public void onApplicationEvent(EventGameModel event) {
		GameModelForSounds gameModelForSounds = event.getGameModel();
		newSounds = gameModelForSounds.getNewSounds();
		if (gameModelForSounds.isSoundActive()) {
			playSounds();
		} else {
			stopAllSounds();
		}
	}
}
