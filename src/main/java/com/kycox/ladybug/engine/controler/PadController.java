package com.kycox.ladybug.engine.controler;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.engine.model.GameModel;
import com.studiohartman.jamepad.ControllerIndex;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.constants.ControllerAxisEnum;
import com.studiohartman.jamepad.exception.ControllerUnpluggedException;

public class PadController extends KeyGameController {

  class PadControllerThread extends Thread {
    @Override
    public void run() {
      try {
        while (true) {
          // on attend une demie période
          sleep(Constants.PACE / 2);

          controllers.update();

          int currentRightX = (int) controllerIndex.getAxisState(ControllerAxisEnum.RIGHTX);
          int currentRightY = (int) controllerIndex.getAxisState(ControllerAxisEnum.RIGHTY);

          if (currentRightY == 1) {
            pacmanMove(Constants.POINT_UP);
          }

          if (currentRightY == -1)
            pacmanMove(Constants.POINT_DOWN);

          if (currentRightX == 1)
            pacmanMove(Constants.POINT_RIGHT);

          if (currentRightX == -1)
            pacmanMove(Constants.POINT_LEFT);
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (ControllerUnpluggedException e) {
        System.out.println("Aucune manette n'est détectée. Veuillez jouer avec les touches...");
      }
    }
  }

  private ControllerIndex     controllerIndex;

  private ControllerManager   controllers         = new ControllerManager(1);
  private PadControllerThread padControllerThread = new PadControllerThread();

  public PadController(GameModel pacmanModel) {
    super(pacmanModel);
    controllers.initSDLGamepad();
    controllerIndex = controllers.getControllerIndex(0);
    padControllerThread.start();
  }

//  private void doVibration(int mms) {
//    if (controllerIndex.isConnected()) {
//      try {
//        controllerIndex.doVibration(1, 1, mms);
//      } catch (ControllerUnpluggedException e) {
//        e.printStackTrace();
//      }
//    }
//  }

  public void run() {
    padControllerThread.start();
  }

//  public void setPadAction() {
//    long nbr = GroupGhosts.getInstance().getLstGhosts().stream().filter(g -> GhostStatusEnum.isFlashing().test(g))
//        .count();
//
//    if (nbr > 0)
//      doVibration(200);
//  }
}
