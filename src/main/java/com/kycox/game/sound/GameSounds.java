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
import com.kycox.game.constant.contract.GameModelForSounds;
import com.kycox.game.constant.contract.NewSoundsForGameSounds;
import com.kycox.game.model.EventGameModel;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Gestion du son dans le jeu
 *
 */
@Component
public class GameSounds implements ApplicationListener<EventGameModel> {
	private NewSoundsForGameSounds newSounds;

	public long getLadybugAgonyDuration() {
		var clipLadybugDying = Sounds.LADYBUG_IS_DYING.getClip();
		return clipLadybugDying.getMicrosecondLength() / 1000;
	}

	public long getNewLifeFunDuration() {
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

		Arrays.stream(Sounds.values()).filter(soundsEnum -> newSounds.hasSound(soundsEnum))
				.forEach(sounds -> new ListenSound(sounds.getClip()).start());
	}

	private void stopAllSounds() {
		Arrays.stream(Sounds.values()).forEach(Sounds::stopSound);
	}

	private void stopAllSoundsExcept(Sounds exceptSound) {
		Arrays.stream(Sounds.values())
				.filter(sound -> !sound.equals(exceptSound))
				.forEach(Sounds::stopSound);
	}

	@Override
	public void onApplicationEvent(EventGameModel event) {
		Object obj = event.getSource();
		if (obj instanceof GameModelForSounds gameModelForSounds) {
			newSounds = gameModelForSounds.getNewSounds();
			if (gameModelForSounds.isSoundActive()) {
				playSounds();
			} else {
				stopAllSounds();
			}
		}
	}
}
