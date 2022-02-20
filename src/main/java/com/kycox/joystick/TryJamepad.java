package com.kycox.joystick;

import com.studiohartman.jamepad.ControllerManager;

public class TryJamepad {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		var controllers = new ControllerManager();
		controllers.initSDLGamepad();

		while (true) {
			/*
			 * System.out.println("0:" + controllers.getState(0).isConnected);
			 * System.out.println("1:" + controllers.getState(1).isConnected);
			 * System.out.println("2:" + controllers.getState(2).isConnected);
			 * System.out.println("3:" + controllers.getState(3).isConnected);
			 */
			var currState = controllers.getState(0); // ControllerState

			// test de connexion
			if (!currState.isConnected) {
				System.out.println("break");
				break;
			}

			/* JOYSTICK à GAUCHE */
			if (currState.leftStickX >= 0.5 || currState.leftStickX <= -0.5) {
				System.out.println("leftStickX on " + currState.controllerType + " is pressed" + currState.leftStickX);
			}
			if (currState.leftStickY >= 0.5 || currState.leftStickY <= -0.5) {
				System.out.println("leftStickY on " + currState.controllerType + " is pressed" + currState.leftStickY);
			}
//			position dans l'espace ?
//			if (currState.leftStickAngle >= 0.5 || currState.leftStickAngle <= -0.5) {
//				System.out.println(
//				        "leftStickAngle on " + currState.controllerType + " is pressed : " + currState.leftStickAngle);
//			}
			if (currState.leftStickMagnitude >= 0.5 || currState.leftStickMagnitude <= -0.5) {
				System.out.println("leftStickMagnitude on " + currState.controllerType + " is pressed"
				        + currState.leftStickMagnitude);
			}
//			le click ne semble pas fonctionner
//			if (currState.leftStickClick) {
//				System.out.println("leftStickClick on " + currState.controllerType + " is pressed");
//			}
//			if (currState.leftStickJustClicked) {
//				System.out.println("leftStickJustClicked on " + currState.controllerType + " is pressed");
//			}

			/* JOYSTICK à DROITE */
			if (currState.rightStickX >= 0.5 || currState.rightStickX <= -0.5) {
				System.out
				        .println("rightStickX on " + currState.controllerType + " is pressed" + currState.rightStickX);
			}
			if (currState.rightStickY >= 0.5 || currState.rightStickY <= -0.5) {
				System.out
				        .println("rightStickY on " + currState.controllerType + " is pressed" + currState.rightStickY);
			}
			// position dans l'espace ?
//			if (currState.rightStickAngle >= 0.5 || currState.rightStickAngle <= -0.5) {
//				System.out.println(
//				        "rightStickAngle on " + currState.controllerType + " is pressed" + currState.rightStickAngle);
//			}
			if (currState.rightStickMagnitude >= 0.5 || currState.rightStickMagnitude <= -0.5) {
				System.out.println("rightStickMagnitude on " + currState.controllerType + " is pressed"
				        + currState.rightStickMagnitude);
			}
//			le click ne semble pas fonctionner
//			if (currState.rightStickJustClicked) {
//				System.out.println("rightStickJustClicked on " + currState.controllerType + " is pressed");
//			}
//			if (currState.rightStickClick) {
//				System.out.println("rightStickClick on " + currState.controllerType + " is pressed");
//			}

			/* BUTON DE FACADE A GAUCHE */
			if (currState.leftTrigger >= 0.5 || currState.leftTrigger <= -0.5) {
				System.out
				        .println("leftTrigger on " + currState.controllerType + " is pressed" + currState.leftTrigger);
			}
			if (currState.lb) {
				System.out.println("lb on " + currState.controllerType + " is pressed");
			}
			if (currState.lbJustPressed) {
				System.out.println("lbJustPressed on " + currState.controllerType + " is pressed");
			}

			/* BUTON DE FACADE A DROITE */
			if (currState.rightTrigger >= 0.5 || currState.rightTrigger <= -0.5) {
				System.out.println(
				        "rightTrigger on " + currState.controllerType + " is pressed" + currState.rightTrigger);
			}
			if (currState.rb) {
				System.out.println("rb on " + currState.controllerType + " is pressed");
			}
			if (currState.rbJustPressed) {
				System.out.println("rbJustPressed on " + currState.controllerType + " is pressed");
			}

			// BOUTON A
			if (currState.aJustPressed) {
				System.out.println("aJustPressed on " + currState.controllerType + " is pressed");
			}
			if (currState.a) {
				System.out.println("a on " + currState.controllerType + " is pressed");
			}

			// BOUTON B
			if (currState.bJustPressed) {
				System.out.println("bJustPressed on " + currState.controllerType + " is pressed");
			}
			if (currState.b) {
				System.out.println("b on " + currState.controllerType + " is pressed");
			}

			// BOUTON X
			if (currState.xJustPressed) {
				System.out.println("xJustPressed on " + currState.controllerType + " is pressed");
			}
			if (currState.x) {
				System.out.println("x on " + currState.controllerType + " is pressed");
			}

			// BOUTON Y
			if (currState.yJustPressed) {
				System.out.println("yJustPressed on " + currState.controllerType + " is pressed");
			}
			if (currState.y) {
				System.out.println("y on " + currState.controllerType + " is pressed");
			}

			// BOUTON START
			if (currState.startJustPressed) {
				System.out.println("startJustPressed on " + currState.controllerType + " is pressed");
			}
			if (currState.start) {
				System.out.println("start on " + currState.controllerType + " is pressed");
			}

			// BOUTON BACK
			if (currState.backJustPressed) {
				System.out.println("backJustPressed on " + currState.controllerType + " is pressed");
			}
			if (currState.back) {
				System.out.println("back on " + currState.controllerType + " is pressed");
			}

			// BOUTON GUIDE ??
			if (currState.guideJustPressed) {
				System.out.println("guideJustPressed on " + currState.controllerType + " is pressed");
			}
			if (currState.guide) {
				System.out.println("guide on " + currState.controllerType + " is pressed");
			}

			// CROIX UP
			if (currState.dpadUpJustPressed) {
				System.out.println("dpadUpJustPressed on " + currState.controllerType + " is pressed");
			}
			if (currState.dpadUp) {
				System.out.println("dpadUp on " + currState.controllerType + " is pressed");
			}

			// CROIX DOWN
			if (currState.dpadDownJustPressed) {
				System.out.println("dpadDownJustPressed on " + currState.controllerType + " is pressed");
			}
			if (currState.dpadDown) {
				System.out.println("dpadDown on " + currState.controllerType + " is pressed");
			}

			// CROIX GAUCHE
			if (currState.dpadLeftJustPressed) {
				System.out.println("dpadLeftJustPressed on " + currState.controllerType + " is pressed");
			}
			if (currState.dpadLeft) {
				System.out.println("dpadLeft on " + currState.controllerType + " is pressed");
			}

			// CROIX DROITE
			if (currState.dpadRightJustPressed) {
				System.out.println("dpadRightJustPressed on " + currState.controllerType + " is pressed");
			}
			if (currState.dpadRight) {
				System.out.println("dpadRight on " + currState.controllerType + " is pressed");
			}

		}
	}
}
