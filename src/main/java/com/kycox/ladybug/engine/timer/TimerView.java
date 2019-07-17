package com.kycox.ladybug.engine.timer;

import java.awt.event.ActionListener;

import javax.swing.Timer;

public abstract class TimerView {

  public Timer createTimer(int mms) {
    ActionListener action = event -> {
      doAction();
    };

    // Création d'un timer qui génère un tic
    return new Timer(mms, action);
  }

  public abstract void doAction();
}
