package com.kycox.game.engine.controller.xboxone;

import com.kycox.game.constant.xbox.XboxOneConstants;
import lombok.Getter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import com.kycox.game.launcher.GuiComponent;

@GuiComponent
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class XboxRequest {
	@Getter
	private boolean aButton;
	@Getter
	private boolean backButton;
	@Getter
	private boolean bButton;
	private PadState controllerState;
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
	@Getter
	private boolean leftStick;
	@Getter
	private boolean rightStick;
	@Getter
	private boolean start;
	private boolean wasConnected = false;
	/** Back vient d'être appuyé (en jeu : « quitter la partie ? ») ; {@code backButton} reste « tenu ». */
	@Getter
	private boolean backPressed;
	/** État brut du battement précédent, pour reconnaître un appui. */
	private PadState previous;
	@Getter
	private boolean xButton;
	private float xLeftStick;
	private float xRightStick;
	@Getter
	private boolean yButton;

	private float yLeftStick;
	private float yRightStick;

	private int getDirection(float value) {
		return value > 0 ? XboxOneConstants.UP_VALUE : XboxOneConstants.LOW_VALUE;
	}

	public boolean isConnected() {
		return controllerState != null && controllerState.connected();
	}

	public boolean isDownLeftStick() {
		return (isYLeftStick() && getDirection(yLeftStick) == XboxOneConstants.LOW_VALUE);
	}

	public boolean isDownRightStick() {
		return (isYRightStick() && getDirection(yRightStick) == XboxOneConstants.LOW_VALUE);
	}

	public boolean isLeftLeftStick() {
		return (isXLeftStick() && getDirection(xLeftStick) == XboxOneConstants.LOW_VALUE);
	}

	public boolean isLeftRightStick() {
		return (isXRightStick() && getDirection(xRightStick) == XboxOneConstants.LOW_VALUE);
	}

	public boolean isRightLeftStick() {
		return (isXLeftStick() && getDirection(xLeftStick) == XboxOneConstants.UP_VALUE);
	}

	public boolean isRightRightStick() {
		return (isXRightStick() && getDirection(xRightStick) == XboxOneConstants.UP_VALUE);
	}

	public boolean isUpLeftStick() {
		return (isYLeftStick() && getDirection(yLeftStick) == XboxOneConstants.UP_VALUE);
	}

	public boolean isUpRightStick() {
		return (isYRightStick() && getDirection(yRightStick) == XboxOneConstants.UP_VALUE);
	}

	private boolean isXLeftStick() {
		return (xLeftStick >= XboxOneConstants.SENSITIVE || xLeftStick <= -XboxOneConstants.SENSITIVE);
	}

	private boolean isXRightStick() {
		return (xRightStick >= XboxOneConstants.SENSITIVE || xRightStick <= -XboxOneConstants.SENSITIVE);
	}

	private boolean isYLeftStick() {
		return (yLeftStick >= XboxOneConstants.SENSITIVE || yLeftStick <= -XboxOneConstants.SENSITIVE);
	}

	private boolean isYRightStick() {
		return (yRightStick >= XboxOneConstants.SENSITIVE || yRightStick <= -XboxOneConstants.SENSITIVE);
	}

	public void readCurrentState(PadState controllerState) {
		this.controllerState = controllerState; // to check isConnected
		setEventOnConnection();
		xRightStick = controllerState.rightStickX();
		yRightStick = controllerState.rightStickY();
		xLeftStick = controllerState.leftStickX();
		yLeftStick = controllerState.leftStickY();
		// branchement : ce qui est déjà enfoncé ne compte pas comme un appui
		var previous = hasBeenConnected || this.previous == null ? controllerState : this.previous;
		aButton = pressed(controllerState.a(), previous.a());
		bButton = pressed(controllerState.b(), previous.b());
		xButton = pressed(controllerState.x(), previous.x());
		yButton = pressed(controllerState.y(), previous.y());
		backButton = controllerState.back(); // tenu : l'aide reste affichée tant qu'on appuie
		backPressed = pressed(controllerState.back(), previous.back()); // en jeu : « quitter ? »
		dpadDown = pressed(controllerState.dpadDown(), previous.dpadDown());
		dpadUp = pressed(controllerState.dpadUp(), previous.dpadUp());
		dpadLeft = pressed(controllerState.dpadLeft(), previous.dpadLeft());
		dpadRight = pressed(controllerState.dpadRight(), previous.dpadRight());
		start = pressed(controllerState.start(), previous.start());
		rightStick = pressed(controllerState.rightStickClick(), previous.rightStickClick());
		leftStick = pressed(controllerState.leftStickClick(), previous.leftStickClick());
		this.previous = controllerState;
	}

	/**
	 * Un appui = enfoncé maintenant, pas au battement précédent (front montant).
	 * D28 (étape 20.1) : un bouton tenu d'un écran à l'autre ne redéclenche plus
	 * l'action de l'écran suivant.
	 */
	private static boolean pressed(boolean now, boolean before) {
		return now && !before;
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

}
