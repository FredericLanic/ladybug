package com.kycox.ladybug.sound;

import javax.sound.sampled.Clip;

/**
 * Processus pour écouter le clip
 * 
 * @see Thread
 */
public class ListenSound extends Thread {

  // clip partagé par les Threads
  private Clip clip = null;

  /**
   * Constructeur
   * 
   * @param clip
   */
  public ListenSound(Clip clip) {
    this.clip = clip;
  }

  /**
   * The sound is load and play in a thread no slow down the engine.
   */
  @Override
  public void run() {
    boolean end = false;

    try {
      // Si le clip n'est pas déjà lancé
      if (!clip.isRunning()) {
        clip.start();

        while (!end) {
          Thread.sleep(10);
          if (clip.getMicrosecondPosition() >= clip.getMicrosecondLength())
            end = true;
        }

        // Réinitialisation du clip pour une prochaine fois
        if (clip.isActive()) {
          clip.flush();
          clip.stop();
        }
        clip.setFramePosition(0);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
