package com.kycox.game.engine.controller.xboxone;

import com.studiohartman.jamepad.ControllerState;

/**
 * État d'une manette à un instant donné (étape 3.5.3d) : ce que Ladybug lit de
 * la manette, indépendamment de la bibliothèque Jamepad.
 *
 * <p>Jamepad 2.x ne permet plus de fabriquer un {@code ControllerState} (champs
 * {@code final}, constructeurs cachés). Ce petit record se crée librement : les
 * tests simulent une manette sans en brancher une.
 *
 * <p>Convention de Ladybug : sur les axes verticaux, <b>positif = vers le haut</b>.
 * Jamepad 2.x (comme SDL) donne l'inverse ; {@link #from(ControllerState)}
 * retourne donc les axes Y (constaté par Frédéric : haut et bas étaient inversés).
 */
public record PadState(boolean connected, float leftStickX, float leftStickY, float rightStickX,
        float rightStickY, boolean a, boolean b, boolean xJustPressed, boolean yJustPressed, boolean back,
        boolean dpadUpJustPressed, boolean dpadDownJustPressed, boolean dpadLeftJustPressed,
        boolean dpadRightJustPressed, boolean startJustPressed, boolean leftStickJustClicked,
        boolean rightStickJustClicked) {

	/** Manette absente. */
	public static PadState disconnected() {
		return new PadState(false, 0, 0, 0, 0, false, false, false, false, false, false, false, false, false, false,
		        false, false);
	}

	/** Recopie l'état lu par Jamepad, axes verticaux retournés (positif = haut). */
	public static PadState from(ControllerState state) {
		return new PadState(state.isConnected, state.leftStickX, -state.leftStickY, state.rightStickX,
		        -state.rightStickY, state.a, state.b, state.xJustPressed, state.yJustPressed, state.back,
		        state.dpadUpJustPressed, state.dpadDownJustPressed, state.dpadLeftJustPressed,
		        state.dpadRightJustPressed, state.startJustPressed, state.leftStickJustClicked,
		        state.rightStickJustClicked);
	}
}
