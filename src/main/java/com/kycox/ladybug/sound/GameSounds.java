package com.kycox.ladybug.sound;

import javax.sound.sampled.Clip;
import javax.swing.JFrame;

import com.kycox.ladybug.constant.KinematicPacmanDeath;
import com.kycox.ladybug.constant.SoundsEnum;
import com.kycox.ladybug.engine.element.ghost.Ghost;
import com.kycox.ladybug.engine.element.ghost.GhostsGroup;
import com.kycox.ladybug.engine.element.ghost.action.AllGhostsActions;
import com.kycox.ladybug.engine.element.ghost.set.GhostStatusEnum;
import com.kycox.ladybug.engine.element.pacman.Pacman;
import com.kycox.ladybug.engine.element.pacman.action.PacmanActions;
import com.kycox.ladybug.engine.element.pacman.set.PacmanStatusEnum;

/**
 * Gestion du son dans le jeu
 *
 */
public class GameSounds {

//  private static GameSounds gameSounds = new GameSounds();
//
//  /**
//   * Retourne le singleton
//   *
//   * @return
//   */
//  public static GameSounds getInstance() {
//    return gameSounds;
//  }

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

  /**
   * Constructeur privé
   */
  public GameSounds() {

    /**
     * Je ne comprend pas pourquoi pour charger les clip, j'ai besoin d'avoir une JFrame. Astuces :
     * créer une JFrame invisible; c'est très moche mais pour l'instant, c'est la solution que je
     * prends.
     */
    JFrame jFrame = new JFrame();
    jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    jFrame.setVisible(false);

    clipBeginning = new ClipGame(SoundsEnum.PACMAN_BEGINNING.getUrl()).getClip();
    clipChomp = new ClipGame(SoundsEnum.PACMAN_CHOMP.getUrl()).getClip();
    clipDeath = new ClipGame(SoundsEnum.PACMAN_IS_DYING.getUrl()).getClip();
    clipEatFruit = new ClipGame(SoundsEnum.PACMAN_EAT_FRUIT.getUrl()).getClip();
    clipEatGhost = new ClipGame(SoundsEnum.PACMAN_EAT_GHOST.getUrl()).getClip();
    clipExtraPac = new ClipGame(SoundsEnum.PACMAN_EXTRA_PAC.getUrl()).getClip();
    clipInterMission = new ClipGame(SoundsEnum.PACMAN_INTER_MISSION.getUrl()).getClip();
    clipGhostSurvivor = new ClipGame(SoundsEnum.GHOST_SURVIVOR.getUrl()).getClip();
    clipGhostRegenerate = new ClipGame(SoundsEnum.GHOST_REGENERATE.getUrl()).getClip();
    clipGhostEaten = new ClipGame(SoundsEnum.GHOST_EATEN.getUrl()).getClip();
    clipSiren = new ClipGame(SoundsEnum.PACMAN_SIREN.getUrl()).getClip();
  }

  /**
   * Ajout du son
   *
   * @param sounds
   */
  public void addSounds(int sounds) {
    this.sounds |= sounds;
  }

  /**
   * Retourne le temps en millisecondes de la musique de la mort de Pacman Utilisé dans la
   * cinématique KinematicPacmanDeath
   *
   * @return
   */
  public long getMicrosecondLengthPacmanDeath() {
    return clipDeath.getMicrosecondLength() / 1000;
  }

  /**
   * Initialise l'objet son. A chaque bip du timer du jeu
   */
  public void init() {
    sounds = 0;
  }

  /**
   * Lancement des sons sélectionnés par le modèle
   */
  public void playSound() {
    if (!listen)
      return;

    if ((sounds & SoundsEnum.PACMAN_CHOMP.getIndex()) != 0) {
      new ListenSound(clipChomp).start();
    }

    if ((sounds & SoundsEnum.PACMAN_IS_DYING.getIndex()) != 0) {
      stopAllSounds();
      new ListenSound(clipDeath).start();
    }

    if ((sounds & SoundsEnum.PACMAN_EAT_FRUIT.getIndex()) != 0) {
      new ListenSound(clipEatFruit).start();
    }

    if ((sounds & SoundsEnum.PACMAN_EAT_GHOST.getIndex()) != 0) {
      new ListenSound(clipEatGhost).start();
    }

    if ((sounds & SoundsEnum.PACMAN_EXTRA_PAC.getIndex()) != 0) {
      new ListenSound(clipExtraPac).start();
    }

    if ((sounds & SoundsEnum.PACMAN_INTER_MISSION.getIndex()) != 0) {
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

    new ListenSound(clipSiren).start();

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
   * Ajoute des sons en fonction de l'état des fantômes
   */
  public void setSound(GhostsGroup groupGhosts, AllGhostsActions ghostsActions, Pacman pacman,
      PacmanActions pacmanActions, KinematicPacmanDeath kinematicPacmanDeath) {

    // Son depuis les objets Fantômes
    for (Ghost ghost : groupGhosts.getLstGhosts()) {
      if (ghost.getStatus().equals(GhostStatusEnum.DYING))
        addSounds(SoundsEnum.GHOST_SURVIVOR.getIndex());

      if (GhostStatusEnum.isScared().test(ghost) || GhostStatusEnum.isFlashing().test(ghost))
        addSounds(SoundsEnum.PACMAN_INTER_MISSION.getIndex());

      if (GhostStatusEnum.isRegenerating().test(ghost))
        addSounds(SoundsEnum.GHOST_REGENERATE.getIndex());

      if (GhostStatusEnum.isDying().test(ghost))
        addSounds(SoundsEnum.GHOST_EATEN.getIndex());
    }

    if (ghostsActions.getNbrEatenGhost() > 0)
      addSounds(SoundsEnum.PACMAN_EAT_GHOST.getIndex());

    // Son depuis l'objet Pacman
    if (pacmanActions.hasEatenAMegaPoint())
      addSounds(SoundsEnum.PACMAN_INTER_MISSION.getIndex());

    if (pacmanActions.hasEatenAPoint())
      addSounds(SoundsEnum.PACMAN_CHOMP.getIndex());

    if (pacman.getStatus().equals(PacmanStatusEnum.DYING) && kinematicPacmanDeath.getBip() == 0) {
      addSounds(SoundsEnum.PACMAN_IS_DYING.getIndex());
    }
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
}
