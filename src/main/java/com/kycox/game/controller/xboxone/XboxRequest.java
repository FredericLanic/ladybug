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
	@Getter
	private boolean hasBeenConnected = false;
	@Getter
	private boolean hasBeenDisConnected = false;
	private int nbrXboxRequest;
	@Getter
	private boolean start;
	private boolean wasConnected = false;
	@Inject
	private XBoxOneControllerManager xBoxOneControllerManager;
	@Getter
	private boolean xButton;
	private float xLeftStick;
	private float xRightStick;
	@Getter
	private boolean yButton;

	private float yLeftStick;
	private float yRightStick;

	private int getDirection(float value) {
		return value > 0 ? ConstantXboxOne.UP_VALUE : ConstantXboxOne.LOW_VALUE;
	}

	public boolean isConnected() {
		return controllerState != null && controllerState.isConnected;
	}

	public boolean isDownLeftStick() {
		return (isYLeftStick() && getDirection(yLeftStick) == ConstantXboxOne.LOW_VALUE);
	}

	public boolean isDownRightStick() {
		return (isYRightStick() && getDirection(yRightStick) == ConstantXboxOne.LOW_VALUE);
	}

	public boolean isLeftLeftStick() {
		return (isXLeftStick() && getDirection(xLeftStick) == ConstantXboxOne.LOW_VALUE);
	}

	public boolean isLeftRightStick() {
		return (isXRightStick() && getDirection(xRightStick) == ConstantXboxOne.LOW_VALUE);
	}

	public boolean isRightLeftStick() {
		return (isXLeftStick() && getDirection(xLeftStick) == ConstantXboxOne.UP_VALUE);
	}

	public boolean isRightRightStick() {
		return (isXRightStick() && getDirection(xRightStick) == ConstantXboxOne.UP_VALUE);
	}

	public boolean isUpLeftStick() {
		return (isYLeftStick() && getDirection(yLeftStick) == ConstantXboxOne.UP_VALUE);
	}

	public boolean isUpRightStick() {
		return (isYRightStick() && getDirection(yRightStick) == ConstantXboxOne.UP_VALUE);
	}

	private boolean isXLeftStick() {
		return (xLeftStick >= ConstantXboxOne.SENSITIVE || xLeftStick <= -ConstantXboxOne.SENSITIVE);
	}

	private boolean isXRightStick() {
		return (xRightStick >= ConstantXboxOne.SENSITIVE || xRightStick <= -ConstantXboxOne.SENSITIVE);
	}

	private boolean isYLeftStick() {
		return (yLeftStick >= ConstantXboxOne.SENSITIVE || yLeftStick <= -ConstantXboxOne.SENSITIVE);
	}

	private boolean isYRightStick() {
		return (yRightStick >= ConstantXboxOne.SENSITIVE || yRightStick <= -ConstantXboxOne.SENSITIVE);
	}

	public void readCurrentState() {
		setEventOnConnection();
		controllerState = xBoxOneControllerManager.getControllerManager().getState(nbrXboxRequest);
		xRightStick = controllerState.rightStickX;
		yRightStick = controllerState.rightStickY;
		xLeftStick = controllerState.leftStickX;
		yLeftStick = controllerState.leftStickY;
		aButton = controllerState.a;
		bButton = controllerState.b;
		xButton = controllerState.xJustPressed;
		yButton = controllerState.yJustPressed;
		backButton = controllerState.back;
		dpadDown = controllerState.dpadDownJustPressed;
		dpadUp = controllerState.dpadUpJustPressed;
		dpadLeft = controllerState.dpadLeftJustPressed;
		dpadRight = controllerState.dpadRightJustPressed;
		start = controllerState.startJustPressed;
	}

	private void setEventOnConnection() {
		var isConnected = isConnected();
		hasBeenConnected = false;
		hasBeenDisConnected = false;

		if (wasConnected != isConnected) {
			if (isConnected) {
				hasBeenConnected = true;
			} else {
				hasBeenDisConnected = true;
			}
		}
		wasConnected = isConnected;
	}

	public void setNbrXboxRequest(int nbrXboxRequest) {
		this.nbrXboxRequest = nbrXboxRequest;
	}
}
