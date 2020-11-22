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
	private Clip						 clipCommonTeleport		 = null;
	private Clip						 clipGameBeginLevel		 = null;
	private Clip						 clipGameSiren0			 = null;
	private Clip						 clipGameSiren1			 = null;
	private Clip						 clipGameSiren2			 = null;
	private Clip						 clipGameSiren3			 = null;
	private Clip						 clipGhostEaten			 = null;
	private Clip						 clipGhostRegenerate	 = null;
	private Clip						 clipGhostSurvivor		 = null;
	private Clip						 clipLadybugChomp		 = null;
	private Clip						 clipLadybugDying		 = null;
	private Clip						 clipLadybugEatFruit	 = null;
	private Clip						 clipLadybugEatGhost	 = null;
	private Clip						 clipLadybugExtraPac	 = null;
	private Clip						 clipLadybugIntermission = null;
	@Setter
	private transient IGameModelForSound gameModel;
	private boolean						 listen					 = true;
	private int							 sounds;

	/**
	 * Retourne le temps en millisecondes de la musique de la mort de ladybug
	 * Utilisé dans la cinématique KinematicLadybugDeath
	 *
	 * @return
	 */
	public long getMicrosecondLengthLadybugDeath() {
		return clipLadybugDying.getMicrosecondLength() / 1000;
	}

	public void init() {
		clipGameBeginLevel		= new ClipGame(SoundsEnum.GAME_BEGIN_LEVEL).getClip();
		clipLadybugChomp		= new ClipGame(SoundsEnum.LADYBUG_CHOMP).getClip();
		clipLadybugDying		= new ClipGame(SoundsEnum.LADYBUG_IS_DYING).getClip();
		clipLadybugEatFruit		= new ClipGame(SoundsEnum.LADYBUG_EAT_FRUIT).getClip();
		clipLadybugEatGhost		= new ClipGame(SoundsEnum.LADYBUG_EAT_GHOST).getClip();
		clipLadybugExtraPac		= new ClipGame(SoundsEnum.LADYBUG_EXTRA_PAC).getClip();
		clipLadybugIntermission	= new ClipGame(SoundsEnum.LADYBUG_INTERMISSION).getClip();
		clipGhostSurvivor		= new ClipGame(SoundsEnum.GHOST_SURVIVOR).getClip();
		clipGhostRegenerate		= new ClipGame(SoundsEnum.GHOST_REGENERATE).getClip();
		clipGhostEaten			= new ClipGame(SoundsEnum.GHOST_EATEN).getClip();
		clipGameSiren0			= new ClipGame(SoundsEnum.GAME_SIREN_0).getClip();
		clipGameSiren1			= new ClipGame(SoundsEnum.GAME_SIREN_1).getClip();
		clipGameSiren2			= new ClipGame(SoundsEnum.GAME_SIREN_2).getClip();
		clipGameSiren3			= new ClipGame(SoundsEnum.GAME_SIREN_3).getClip();
		clipCommonTeleport		= new ClipGame(SoundsEnum.COMMON_TELEPORT).getClip();
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
	public void playSounds() {
		if (!listen)
			return;
		if ((sounds & SoundsEnum.LADYBUG_CHOMP.getIndex()) != 0) {
			new ListenSound(clipLadybugChomp).start();
		}
		if ((sounds & SoundsEnum.LADYBUG_IS_DYING.getIndex()) != 0) {
			stopAllSounds();
			new ListenSound(clipLadybugDying).start();
		}
		if ((sounds & SoundsEnum.LADYBUG_EAT_FRUIT.getIndex()) != 0) {
			new ListenSound(clipLadybugEatFruit).start();
		}
		if ((sounds & SoundsEnum.LADYBUG_EAT_GHOST.getIndex()) != 0) {
			new ListenSound(clipLadybugEatGhost).start();
		}
		if ((sounds & SoundsEnum.LADYBUG_EXTRA_PAC.getIndex()) != 0) {
			new ListenSound(clipLadybugExtraPac).start();
		}
		if ((sounds & SoundsEnum.LADYBUG_INTERMISSION.getIndex()) != 0) {
			new ListenSound(clipLadybugIntermission).start();
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
		if ((sounds & SoundsEnum.GAME_SIREN_0.getIndex()) != 0) {
			new ListenSound(clipGameSiren0).start();
		}
		if ((sounds & SoundsEnum.GAME_SIREN_1.getIndex()) != 0) {
			new ListenSound(clipGameSiren1).start();
		}
		if ((sounds & SoundsEnum.GAME_SIREN_2.getIndex()) != 0) {
			new ListenSound(clipGameSiren2).start();
		}
		if ((sounds & SoundsEnum.GAME_SIREN_3.getIndex()) != 0) {
			new ListenSound(clipGameSiren3).start();
		}
		if ((sounds & SoundsEnum.COMMON_TELEPORT.getIndex()) != 0) {
			new ListenSound(clipCommonTeleport).start();
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
		new ListenSound(clipGameBeginLevel).start();
		// On attend le temps du jingle : le jeu est alors bloqué
		try {
			Thread.sleep(clipGameBeginLevel.getMicrosecondLength() / 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		gameModel.getGameStatus().setInGame();
		gameModel.startGameTimer();
	}

	@Override
	public void update(Observable gameModelForSound, Object arg) {
		gameModel = (IGameModelForSound) gameModelForSound;
		if ((gameModel.getSounds() & SoundsEnum.GAME_BEGIN_LEVEL.getIndex()) != 0) {
			playStartJingle();
		} else if (gameModel.isSoundActive())
			playSounds();
		else
			stopAllSounds();
	}

	/**
	 * Stop tous les sons qui ont pu être lancés.
	 */
	private void stopAllSounds() {
		clipGameBeginLevel.stop();
		clipGameBeginLevel.setFramePosition(0);
		clipLadybugChomp.stop();
		clipLadybugChomp.setFramePosition(0);
		clipLadybugDying.stop();
		clipLadybugDying.setFramePosition(0);
		clipLadybugEatFruit.stop();
		clipLadybugEatFruit.setFramePosition(0);
		clipLadybugEatGhost.stop();
		clipLadybugEatGhost.setFramePosition(0);
		clipLadybugExtraPac.stop();
		clipLadybugExtraPac.setFramePosition(0);
		clipLadybugIntermission.stop();
		clipLadybugIntermission.setFramePosition(0);
		clipGhostSurvivor.stop();
		clipGhostSurvivor.setFramePosition(0);
		clipGhostRegenerate.stop();
		clipGhostRegenerate.setFramePosition(0);
		clipGameSiren0.stop();
		clipGameSiren0.setFramePosition(0);
		clipGameSiren1.stop();
		clipGameSiren1.setFramePosition(0);
		clipGameSiren2.stop();
		clipGameSiren2.setFramePosition(0);
		clipGameSiren3.stop();
		clipGameSiren3.setFramePosition(0);
		clipCommonTeleport.stop();
		clipCommonTeleport.setFramePosition(0);
	}
}
