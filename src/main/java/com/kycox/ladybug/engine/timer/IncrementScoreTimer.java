package com.kycox.ladybug.engine.timer;

import java.util.Timer;
import java.util.TimerTask;

import com.kycox.ladybug.score.IncrementScore;

/**
 * Timer qui permet d'afficher le score un certain temps � l'�cran
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
   * @param scoreIncremenet : mod�le du jeu
   * @param numThread       : num�ro du thread (1: principal, autrement
   *                        clignottement)
   */
  public IncrementScoreTimer(IncrementScore scoreIncremenet) {
    this.scoreIncrement = scoreIncremenet;
  }

  /**
   * Arrete le super power : le niveau est termin�, ou bien un autre super power a
   * �t� pris
   */
  public void forcedStop() {
    if (timer != null) {
      timer.cancel();
      timer.purge();
    }
  }

  /**
   * Lance le timer du score incr�ment
   * 
   * @param duration temps en milli secondes
   */
  public void launch(int duration) {
    timer.schedule(new WaitTimer(), duration);
  }
}