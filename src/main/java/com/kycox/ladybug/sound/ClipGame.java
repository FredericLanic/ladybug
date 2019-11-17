package com.kycox.ladybug.sound;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Chargement des clip en mémoire
 *
 */
public class ClipGame {

  private AudioInputStream audioInputStream = null;

  private Clip             clip             = null;

  /**
   * Constructeur
   *
   * @param url
   */
  public ClipGame(URL url) {
    try {
      audioInputStream = AudioSystem.getAudioInputStream(url);
      // Get a sound clip resource.
      clip = AudioSystem.getClip();
      // Open audio clip and load samples from the audio input stream.
      clip.open(audioInputStream);
    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
      e.printStackTrace();
    }
  }

  /**
   * Retourne le clip
   *
   * @return
   */
  public Clip getClip() {
    return clip;
  }

}
