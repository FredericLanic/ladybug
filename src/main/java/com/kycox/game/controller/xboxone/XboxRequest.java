package com.kycox.game.controller.xboxone;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.studiohartman.jamepad.ControllerState;

import lombok.Getter;

@Named("XboxRequest")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class XboxRequest {
	@Getter
	private boolean aButton;
	@Getter
	private boolean backButton;
	@Getter
	private boolean bButton;
	private ControllerState controllerState;
	@Getter
	private boolean dpadDown;
	@Getter
	private boolean dpadLeft;
	@Getter
	private boolean dpadRight;
	@Getter
	private boolean dpadUp;
	private int nbrXboxRequest;
	private float rightStickX;
	private float rightStickY;
	@Inject
	private XBoxOneControllerManager xBoxOneControllerManager;
	@Getter
	private boolean xButton;
	@Getter
	private boolean yButton;

	private int getDirection(float value) {
		return value > 0 ? ConstanteXboxOne.UP_VALUE : ConstanteXboxOne.LOW_VALUE;
	}

	public boolean isConnected() {
		return controllerState.isConnected;
	}

	public boolean isDownRightStick() {
		return (isRightStickY() && getDirection(rightStickY) == ConstanteXboxOne.LOW_VALUE);
	}

	public boolean isLeftRightStick() {
		return (isRightStickX() && getDirection(rightStickX) == ConstanteXboxOne.LOW_VALUE);
	}

	public boolean isRighRightStickt() {
		return (isRightStickX() && getDirection(rightStickX) == ConstanteXboxOne.UP_VALUE);
	}

	private boolean isRightStickX() {
		return (rightStickX >= ConstanteXboxOne.SENSITIVE || rightStickX <= -ConstanteXboxOne.SENSITIVE);
	}

	public boolean isRightStickY() {
		return (rightStickY >= ConstanteXboxOne.SENSITIVE || rightStickY <= -ConstanteXboxOne.SENSITIVE);
	}

	public boolean isUpRightStick() {
		return (isRightStickY() && getDirection(rightStickY) == ConstanteXboxOne.UP_VALUE);
	}

	public void readCurrentState() {
		controllerState = xBoxOneControllerManager.getControllerManager().getState(nbrXboxRequest);
		rightStickX = controllerState.rightStickX;
		rightStickY = controllerState.rightStickY;
		aButton = controllerState.a;
		bButton = controllerState.b;
		xButton = controllerState.xJustPressed;
		yButton = controllerState.yJustPressed;
		backButton = controllerState.back;
		dpadDown = controllerState.dpadDownJustPressed;
		dpadUp = controllerState.dpadUpJustPressed;
		dpadLeft = controllerState.dpadLeftJustPressed;
		dpadRight = controllerState.dpadRightJustPressed;
	}

	public void setNbrXboxRequest(int nbrXboxRequest) {
		this.nbrXboxRequest = nbrXboxRequest;
	}
}
