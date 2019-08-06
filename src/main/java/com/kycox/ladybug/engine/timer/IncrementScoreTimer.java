package com.kycox.ladybug.engine.timer;

import java.util.Timer;
import java.util.TimerTask;

import com.kycox.ladybug.score.IncrementScore;

/**
 * Timer qui permet d'afficher le score un certain temps à l'écran
 *
 */
public class IncrementScoreTimer {
  /**
   * Class TimerTask
   */
  private class WaitTimer extends TimerTask {
    @Override
    public void run() {
      timer.cancel();
      timer.purge();
      // Affecte au ScorePoint le fait qu'il n'est plus affichage
      scoreIncrement.setDying(true);
    }
  }

  private IncrementScore scoreIncrement;
  private Timer          timer = new Timer(true);

  /**
   * Constructeur
   * 
   * @param scoreIncremenet : modèle du jeu
   * @param numThread       : numéro du thread (1: principal, autrement
   *                        clignottement)
   */
  public IncrementScoreTimer(IncrementScore scoreIncremenet) {
    this.scoreIncrement = scoreIncremenet;
  }

  /**
   * Arrete le super power : le niveau est terminé, ou bien un autre super power a
   * été pris
   */
  public void forcedStop() {
    if (timer != null) {
      timer.cancel();
      timer.purge();
    }
  }

  /**
   * Lance le timer du score incrément
   * 
   * @param duration temps en milli secondes
   */
  public void launch(int duration) {
    timer.schedule(new WaitTimer(), duration);
  }
}