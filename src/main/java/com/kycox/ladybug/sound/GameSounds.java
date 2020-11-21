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
package com.kycox.ladybug.sound;

import java.util.Observable;
import java.util.Observer;

import javax.sound.sampled.Clip;

import com.kycox.ladybug.constant.SoundsEnum;
import com.kycox.ladybug.contract.IGameModelForSound;

import lombok.Setter;

/**
 * Gestion du son dans le jeu
 *
 */
public class GameSounds implements Observer {
	private Clip						 clipBeginning		 = null;
	private Clip						 clipChomp			 = null;
	private Clip						 clipDeath			 = null;
	private Clip						 clipEatFruit		 = null;
	private Clip						 clipEatGhost		 = null;
	private Clip						 clipExtraPac		 = null;
	private Clip						 clipGhostEaten		 = null;
	private Clip						 clipGhostRegenerate = null;
	private Clip						 clipGhostSurvivor	 = null;
	private Clip						 clipInterMission	 = null;
	private Clip						 clipSiren0			 = null;
	private Clip						 clipSiren1			 = null;
	private Clip						 clipSiren2			 = null;
	private Clip						 clipSiren3			 = null;
	@Setter
	private transient IGameModelForSound gameModel;
	private boolean						 listen				 = true;
	private int							 sounds;

	/**
	 * Retourne le temps en millisecondes de la musique de la mort de ladybug
	 * Utilisé dans la cinématique KinematicLadybugDeath
	 *
	 * @return
	 */
	public long getMicrosecondLengthLadybugDeath() {
		return clipDeath.getMicrosecondLength() / 1000;
	}

	public void init() {
		// 1
		clipBeginning = new ClipGame(SoundsEnum.LADYBUG_LEVEL_BEGINNING).getClip();
		// 2
		clipChomp = new ClipGame(SoundsEnum.LADYBUG_CHOMP).getClip();
		// 4
		clipDeath = new ClipGame(SoundsEnum.LADYBUG_IS_DYING).getClip();
		// 8
		clipEatFruit = new ClipGame(SoundsEnum.LADYBUG_EAT_FRUIT).getClip();
		// 16
		clipEatGhost = new ClipGame(SoundsEnum.LADYBUG_EAT_GHOST).getClip();
		// 32
		clipExtraPac = new ClipGame(SoundsEnum.LADYBUG_EXTRA_PAC).getClip();
		// 64
		clipInterMission = new ClipGame(SoundsEnum.LADYBUG_INTER_MISSION).getClip();
		// 128
		clipGhostSurvivor = new ClipGame(SoundsEnum.GHOST_SURVIVOR).getClip();
		// 256
		clipGhostRegenerate = new ClipGame(SoundsEnum.GHOST_REGENERATE).getClip();
		// 512
		clipGhostEaten = new ClipGame(SoundsEnum.GHOST_EATEN).getClip();
		// 1024
		clipSiren0 = new ClipGame(SoundsEnum.LADYBUG_SIREN_0).getClip();
		clipSiren1 = new ClipGame(SoundsEnum.LADYBUG_SIREN_1).getClip();
		clipSiren2 = new ClipGame(SoundsEnum.LADYBUG_SIREN_2).getClip();
		clipSiren3 = new ClipGame(SoundsEnum.LADYBUG_SIREN_3).getClip();
	}

	/**
	 * Initialise l'objet son. A chaque bip du timer du jeu
	 */
	public void initSounds() {
		sounds = 0;
	}

	/**
	 * Lancement des sons sélectionnés par le modèle
	 */
	public void playSound() {
		if (!listen)
			return;
		if ((sounds & SoundsEnum.LADYBUG_CHOMP.getIndex()) != 0) {
			new ListenSound(clipChomp).start();
		}
		if ((sounds & SoundsEnum.LADYBUG_IS_DYING.getIndex()) != 0) {
			stopAllSounds();
			new ListenSound(clipDeath).start();
		}
		if ((sounds & SoundsEnum.LADYBUG_EAT_FRUIT.getIndex()) != 0) {
			new ListenSound(clipEatFruit).start();
		}
		if ((sounds & SoundsEnum.LADYBUG_EAT_GHOST.getIndex()) != 0) {
			new ListenSound(clipEatGhost).start();
		}
		if ((sounds & SoundsEnum.LADYBUG_EXTRA_PAC.getIndex()) != 0) {
			new ListenSound(clipExtraPac).start();
		}
		if ((sounds & SoundsEnum.LADYBUG_INTER_MISSION.getIndex()) != 0) {
			new ListenSound(clipInterMission).start();
		}
		if ((sounds & SoundsEnum.GHOST_SURVIVOR.getIndex()) != 0) {
			new ListenSound(clipGhostSurvivor).start();
		}
		if ((sounds & SoundsEnum.GHOST_REGENERATE.getIndex()) != 0) {
			new ListenSound(clipGhostRegenerate).start();
		}
		if ((sounds & SoundsEnum.GHOST_EATEN.getIndex()) != 0) {
			new ListenSound(clipGhostEaten).start();
		}
		if ((sounds & SoundsEnum.LADYBUG_SIREN_0.getIndex()) != 0) {
			new ListenSound(clipSiren0).start();
		}
		if ((sounds & SoundsEnum.LADYBUG_SIREN_1.getIndex()) != 0) {
			new ListenSound(clipSiren1).start();
		}
		if ((sounds & SoundsEnum.LADYBUG_SIREN_2.getIndex()) != 0) {
			new ListenSound(clipSiren2).start();
		}
		if ((sounds & SoundsEnum.LADYBUG_SIREN_3.getIndex()) != 0) {
			new ListenSound(clipSiren3).start();
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
		// lancement du jingle du d�but
		new ListenSound(clipBeginning).start();
		// On attend le temps du jingle : le jeu est alors bloqué
		try {
			Thread.sleep(clipBeginning.getMicrosecondLength() / 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		gameModel.getGameStatus().setInGame();
		gameModel.startGameTimer();
	}

	@Override
	public void update(Observable gameModel, Object arg) {
		this.gameModel = (IGameModelForSound) gameModel;
		sounds		   = this.gameModel.getNewSounds();
		listen		   = this.gameModel.isSoundActive();
		if ((sounds & SoundsEnum.LADYBUG_LEVEL_BEGINNING.getIndex()) != 0) {
			playStartJingle();
		} else if (listen)
			playSound();
		else
			stopAllSounds();
	}

	/**
	 * Stop tous les sons qui ont pu être lancés.
	 */
	private void stopAllSounds() {
		clipBeginning.stop();
		clipBeginning.setFramePosition(0);
		clipChomp.stop();
		clipChomp.setFramePosition(0);
		clipDeath.stop();
		clipDeath.setFramePosition(0);
		clipEatFruit.stop();
		clipEatFruit.setFramePosition(0);
		clipEatGhost.stop();
		clipEatGhost.setFramePosition(0);
		clipExtraPac.stop();
		clipExtraPac.setFramePosition(0);
		clipInterMission.stop();
		clipInterMission.setFramePosition(0);
		clipGhostSurvivor.stop();
		clipGhostSurvivor.setFramePosition(0);
		clipGhostRegenerate.stop();
		clipGhostRegenerate.setFramePosition(0);
		clipSiren0.stop();
		clipSiren0.setFramePosition(0);
		clipSiren1.stop();
		clipSiren1.setFramePosition(0);
		clipSiren2.stop();
		clipSiren2.setFramePosition(0);
		clipSiren3.stop();
		clipSiren3.setFramePosition(0);
	}
}
