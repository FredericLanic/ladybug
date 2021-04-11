/**
  Copyright (C) 2019  Frédéric Lanic frederic.lanic@outlook.fr

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

import java.util.Observable;
import java.util.Observer;

import javax.inject.Named;
import javax.sound.sampled.Clip;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kycox.game.constant.Sounds;
import com.kycox.game.contract.IGameModelForGameSounds;
import com.kycox.game.contract.INewSoundsForGameSounds;

/**
 * Gestion du son dans le jeu
 *
 */
@Named("GameSounds")
public class GameSounds implements Observer {
	private static final Log		logger = LogFactory.getLog(GameSounds.class);
	private IGameModelForGameSounds	gameModel;
	private INewSoundsForGameSounds	newSounds;

	/**
	 * Retourne le temps en millisecondes de la musique de la mort de ladybug
	 * Utilisé dans la cinématique KinematicLadybugDeath
	 *
	 * @return
	 */
	public long getMicrosecondLengthLadybugDeath() {
		Clip clipLadybugDying = Sounds.LADYBUG_IS_DYING.getClip();
		return clipLadybugDying.getMicrosecondLength() / 1000;
	}

	/**
	 * Lancement des sons sélectionnés par le modèle
	 */
	public void playSounds() {
		if (newSounds.hasSound(Sounds.LADYBUG_IS_DYING)) {
			stopAllSounds();
		}
		Sounds[] soundsEnums = Sounds.values();
		for (Sounds soundsEnum : soundsEnums) {
			if (newSounds.hasSound(soundsEnum)) {
				new ListenSound(soundsEnum.getClip()).start();
			}
		}
	}

	/**
	 * Start Jingle. Le jeu est figé le temps que le jingle est lancé
	 *
	 * @FIXME : la gestion du timer devrait être gérée ailleurs
	 */
	public void playStartJingle() {
		stopAllSounds();
		gameModel.stopGameTimer();
		Clip clip = Sounds.GAME_BEGIN_LEVEL.getClip();
		// lancement du jingle du début
		new ListenSound(clip).start();
		// On attend le temps du jingle : le jeu est alors bloqué
		try {
			Thread.sleep(clip.getMicrosecondLength() / 1000);
		} catch (InterruptedException e) {
			logger.error("Interrupted - playStartJingle" + e);
			Thread.currentThread().interrupt();
		}
		gameModel.getCurrentGameStatus().setInGame();
		gameModel.startGameTimer();
	}

	@Override
	public void update(Observable gameModelForSound, Object arg) {
		gameModel = (IGameModelForGameSounds) gameModelForSound;
		newSounds = gameModel.getNewSounds();
		if (newSounds.hasSound(Sounds.GAME_BEGIN_LEVEL)) {
			playStartJingle();
		} else if (gameModel.isSoundActive()) {
			playSounds();
		} else {
			stopAllSounds();
		}
	}

	private void stopAllSounds() {
		Sounds[] soundsEnums = Sounds.values();
		for (Sounds soundsEnum : soundsEnums) {
			soundsEnum.getClip().stop();
			soundsEnum.getClip().setFramePosition(0);
		}
	}
}