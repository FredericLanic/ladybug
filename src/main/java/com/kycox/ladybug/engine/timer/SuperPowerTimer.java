package com.kycox.ladybug.engine.timer;

import java.util.Timer;
import java.util.TimerTask;

import com.kycox.ladybug.constant.SuperPowerTimerEnum;

/**
 * Double timer utilis� pour afficher les fant�mes qui ont peur et qui
 * clignottent ensuite. 5 Secondes avant la fin, les fant�mes vont clignoter
 *
 */
public class SuperPowerTimer {

  /**
   * Class TimerTask
   */
  private class PowerTimer extends TimerTask {
    @Override
    public void run() {
      timer.cancel();
      timer.purge();
      if (numTimer == 1) {
        // Fin du premier timer
        superPowerTimer = new SuperPowerTimer(numTimer + 1);
        superPowerTimer.launch(FLASH_DELAY);
      } else {
        // Fin du second timer
        superPowerTimerStatus = SuperPowerTimerEnum.STOP;
        superPowerTimer = null;
      }
    }
  }

  // d�lai en second de clignottements
  private static final int    FLASH_DELAY           = 5;

  // num�ro du timer
  private int                 numTimer;
  // objet powerFlashTimer utilis� pour le 2i�me timer
  private SuperPowerTimer     superPowerTimer       = null;
  // etat du superPower courant (1ier ou 2ieme selon le cas)
  private SuperPowerTimerEnum superPowerTimerStatus = SuperPowerTimerEnum.STOP;
  // Timer du power
  private Timer               timer;

  /**
   * Constructeur
   * 
   * @param numThread : num�ro du thread (1: principal, autrement clignottement)
   */
  public SuperPowerTimer(int numThread) {
    this.numTimer = numThread;
  }

  /**
   * Arrete le super power : le niveau est termin�, ou bien un autre super power a
   * �t� pris
   */
  public void forcedStop() {
    // Arr�t forc� du premier timer
    if (timer != null) {
      timer.cancel();
      timer.purge();
    }
    // Arr�t forc� du second timer s'il existe
    if (superPowerTimer != null) {
      superPowerTimer.forcedStop();
      superPowerTimer = null;
    }
    superPowerTimerStatus = SuperPowerTimerEnum.STOP;
  }

  /**
   * Return the status of the super power time : see SuperPowerEnum
   * 
   * @return
   */
  public SuperPowerTimerEnum getStatus() {
    // Priorit� au deuxi�me timer s'il existe
    if (superPowerTimer != null)
      return superPowerTimer.getStatus();
    return superPowerTimerStatus;
  }

  /**
   * launch the delay
   */
  public void launch(int seconds) {
    if (numTimer == 1) {
      // D�but du premier timer
      seconds = seconds - FLASH_DELAY;
      superPowerTimerStatus = SuperPowerTimerEnum.RUN;
    } else {
      // D�but du second timer
      superPowerTimerStatus = SuperPowerTimerEnum.STOPPING;
    }

    // Cr�ation du timer
    timer = new Timer(true);
    // lancement du timer apr�s n secondes
    timer.schedule(new PowerTimer(), (long) (seconds) * 1000);
  }
}