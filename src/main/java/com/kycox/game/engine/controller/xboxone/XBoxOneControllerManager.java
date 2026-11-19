package com.kycox.game.engine.controller.xboxone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kycox.game.launcher.GuiComponent;
import com.studiohartman.jamepad.Configuration;
import com.studiohartman.jamepad.ControllerManager;

import jakarta.annotation.PostConstruct;

/**
 * Accès aux manettes par Jamepad (fork libGDX 2.x, étape 3.5.3d) : Windows,
 * Linux et macOS, y compris Apple Silicon.
 *
 * <p>Un seul objet, partagé : depuis 3.5.3d, {@code XboxOneController} l'utilise
 * au lieu d'en hériter, et Jamepad n'est plus initialisé deux fois (dette D19).
 * Si la bibliothèque native ne se charge pas, la manette est désactivée et le
 * jeu se joue au clavier (étape 1.3).
 */
@GuiComponent
public class XBoxOneControllerManager {
	private static final Logger logger = LoggerFactory.getLogger(XBoxOneControllerManager.class);
	/**
	 * Correspondances supplémentaires de manettes : fichier vide fourni par
	 * Ladybug (SDL connaît déjà les manettes Xbox). Sans lui, Jamepad écrit un
	 * avertissement à chaque lancement.
	 */
	private static final String MAPPINGS = "/jamepad/gamecontrollerdb.txt";

	private ControllerManager controllerManager;
	/** Vrai si Jamepad a pu être initialisé ; faux sinon (jeu au clavier seulement). */
	private boolean available;

	@PostConstruct
	public void initialize() {
		try {
			controllerManager = new ControllerManager(new Configuration(), MAPPINGS);
			controllerManager.initSDLGamepad();
			available = true;
			logger.info("Gamepad support enabled ({} controller(s) connected)", controllerManager.getNumControllers());
		} catch (LinkageError | RuntimeException e) {
			// LinkageError couvre UnsatisfiedLinkError (bibliothèque native introuvable)
			controllerManager = null;
			available = false;
			logger.warn("Manette Xbox désactivée : impossible de charger Jamepad ({}). Le jeu se joue au clavier.",
			        e.toString());
		}
	}

	public boolean isAvailable() {
		return available;
	}

	/** Relit les manettes (Jamepad 2.x : à appeler à chaque battement, avant {@link #state(int)}). */
	public void update() {
		if (available) {
			controllerManager.update();
		}
	}

	/** État de la manette numéro {@code index}. */
	public PadState state(int index) {
		return available ? PadState.from(controllerManager.getState(index)) : PadState.disconnected();
	}

	/** Fait vibrer la manette {@code index}. */
	public void vibrate(int index, float leftMagnitude, float rightMagnitude, int durationMillis) {
		if (available) {
			controllerManager.doVibration(index, leftMagnitude, rightMagnitude, durationMillis);
		}
	}
}
