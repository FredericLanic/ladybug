package com.kycox.ladybug.engine.timer;

import java.util.Timer;
import java.util.TimerTask;

import com.kycox.ladybug.constant.SuperPowerTimerEnum;

/**
 * Double timer utilisé pour afficher les fantômes qui ont peur et qui
 * clignottent ensuite. 5 Secondes avant la fin, les fantômes vont clignoter
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

  // délai en second de clignottements
  private static final int    FLASH_DELAY           = 5;

  // numéro du timer
  private int                 numTimer;
  // objet powerFlashTimer utilisé pour le 2ième timer
  private SuperPowerTimer     superPowerTimer       = null;
  // etat du superPower courant (1ier ou 2ieme selon le cas)
  private SuperPowerTimerEnum superPowerTimerStatus = SuperPowerTimerEnum.STOP;
  // Timer du power
  private Timer               timer;

  /**
   * Constructeur
   * 
   * @param numThread : numéro du thread (1: principal, autrement clignottement)
   */
  public SuperPowerTimer(int numThread) {
    this.numTimer = numThread;
  }

  /**
   * Arrete le super power : le niveau est terminé, ou bien un autre super power a
   * été pris
   */
  public void forcedStop() {
    // Arrêt forcé du premier timer
    if (timer != null) {
      timer.cancel();
      timer.purge();
    }
    // Arrêt forcé du second timer s'il existe
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
    // Priorité au deuxième timer s'il existe
    if (superPowerTimer != null)
      return superPowerTimer.getStatus();
    return superPowerTimerStatus;
  }

  /**
   * launch the delay
   */
  public void launch(int seconds) {
    if (numTimer == 1) {
      // Début du premier timer
      seconds = seconds - FLASH_DELAY;
      superPowerTimerStatus = SuperPowerTimerEnum.RUN;
    } else {
      // Début du second timer
      superPowerTimerStatus = SuperPowerTimerEnum.STOPPING;
    }

    // Création du timer
    timer = new Timer(true);
    // lancement du timer après n secondes
    timer.schedule(new PowerTimer(), (long) (seconds) * 1000);
  }
}