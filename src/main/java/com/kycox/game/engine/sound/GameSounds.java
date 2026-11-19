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

import com.kycox.game.constant.sounds.Sounds;
import com.kycox.game.constant.contract.GameModelForSounds;
import com.kycox.game.constant.contract.NewSoundsForGameSounds;
import com.kycox.game.engine.model.EventGameModel;
import org.springframework.context.ApplicationListener;
import com.kycox.game.launcher.GuiComponent;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.Objects;

/**
 * Gestion du son dans le jeu
 *
 */
@GuiComponent
public class GameSounds implements ApplicationListener<EventGameModel> {
	private static final Logger logger = LoggerFactory.getLogger(GameSounds.class);
	private NewSoundsForGameSounds newSounds;

	/**
	 * Précharge tous les sons au démarrage du jeu, pour qu'aucun chargement ne
	 * provoque de saccade pendant la partie (choix d'origine de Frédéric). Fait ici,
	 * dans l'interface (profil « gui »), et non dans {@link Sounds} : le modèle et
	 * les tests n'ont ainsi jamais besoin de carte son.
	 */
	@PostConstruct
	void preloadSounds() {
		var loaded = Arrays.stream(Sounds.values()).map(Sounds::getClip).filter(Objects::nonNull).count();
		logger.info("Sounds preloaded: {}/{}", loaded, Sounds.values().length);
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
				.map(Sounds::getClip)
				.filter(Objects::nonNull) // pas de sortie audio : on reste muet
				.forEach(clip -> new ListenSound(clip).start());
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
