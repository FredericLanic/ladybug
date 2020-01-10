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
import com.kycox.ladybug.engine.model.GameModel;

/**
 * Gestion du son dans le jeu
 *
 */
public class GameSounds implements Observer {
  private Clip    clipBeginning       = null;
  private Clip    clipChomp           = null;
  private Clip    clipDeath           = null;
  private Clip    clipEatFruit        = null;
  private Clip    clipEatGhost        = null;
  private Clip    clipExtraPac        = null;
  private Clip    clipGhostEaten      = null;
  private Clip    clipGhostRegenerate = null;
  private Clip    clipGhostSurvivor   = null;
  private Clip    clipInterMission    = null;
  private Clip    clipSiren           = null;

  private boolean listen              = true;

  private int     sounds;

  public GameSounds() {
    clipBeginning = new ClipGame(SoundsEnum.LADYBUG_BEGINNING.getUrl()).getClip();
    clipChomp = new ClipGame(SoundsEnum.LADYBUG_CHOMP.getUrl()).getClip();
    clipDeath = new ClipGame(SoundsEnum.LADYBUG_IS_DYING.getUrl()).getClip();
    clipEatFruit = new ClipGame(SoundsEnum.LADYBUG_EAT_FRUIT.getUrl()).getClip();
    clipEatGhost = new ClipGame(SoundsEnum.LADYBUG_EAT_GHOST.getUrl()).getClip();
    clipExtraPac = new ClipGame(SoundsEnum.LADYBUG_EXTRA_PAC.getUrl()).getClip();
    clipInterMission = new ClipGame(SoundsEnum.LADYBUG_INTER_MISSION.getUrl()).getClip();
    clipGhostSurvivor = new ClipGame(SoundsEnum.GHOST_SURVIVOR.getUrl()).getClip();
    clipGhostRegenerate = new ClipGame(SoundsEnum.GHOST_REGENERATE.getUrl()).getClip();
    clipGhostEaten = new ClipGame(SoundsEnum.GHOST_EATEN.getUrl()).getClip();
    clipSiren = new ClipGame(SoundsEnum.LADYBUG_SIREN.getUrl()).getClip();
  }

  /**
   * Retourne le temps en millisecondes de la musique de la mort de ladybug Utilisé dans la
   * cinématique KinematicLadybugDeath
   *
   * @return
   */
  public long getMicrosecondLengthLadybugDeath() {
    return clipDeath.getMicrosecondLength() / 1000;
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

    if ((sounds & SoundsEnum.LADYBUG_SIREN.getIndex()) != 0) {
      new ListenSound(clipSiren).start();
    }
  }

  /**
   * Start Jingle. Le jeu est figé le temps que le jingle est lancé
   */
  public void playStartJingle() {
    stopAllSounds();
    // lancement du jingle du d�but
    new ListenSound(clipBeginning).start();
    // On attend le temps du jingle : le jeu est alors bloqué
    try {
      Thread.sleep(clipBeginning.getMicrosecondLength() / 1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Affecte le modèle du jeu
   *
   * @param gameModel
   */
  public void setObservable(Observable gameModel) {
    if (gameModel != null)
      gameModel.addObserver(this);
  }

  public void startStop() {
    listen = !listen;
    if (!listen) {
      stopAllSounds();
    }
  }

  /**
   * Stop tous les sons qui ont pu être lancés.
   */
  public void stopAllSounds() {
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
    clipSiren.stop();
    clipSiren.setFramePosition(0);
  }

  @Override
  public void update(Observable gameModel, Object arg) {
    if (gameModel == null)
      return;

    sounds = ((GameModel) gameModel).getSounds();
    playSound();
  }
}
