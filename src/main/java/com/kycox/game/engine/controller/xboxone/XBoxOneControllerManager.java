package com.kycox.game.engine.controller.xboxone;

import com.kycox.game.tools.os.WindowsHost;
import com.studiohartman.jamepad.ControllerManager;
import jakarta.annotation.PostConstruct;
import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

/**
 * Accès aux manettes Xbox One via la bibliothèque Jamepad (code natif SDL).
 * <p>
 * Si le code natif ne se charge pas (bibliothèque absente, processeur non
 * supporté…), la manette est désactivée et le jeu continue au clavier :
 * c'est une « dégradation gracieuse ».
 */
@Getter
@Component
@Conditional(WindowsHost.class)
public class XBoxOneControllerManager {

	private static final Logger logger = LoggerFactory.getLogger(XBoxOneControllerManager.class);

	private ControllerManager controllerManager;

	/** Vrai si Jamepad a pu être initialisé ; faux sinon (jeu au clavier seulement). */
	private boolean available;

	@PostConstruct
	public void initialize() {
		try {
			controllerManager = new ControllerManager();
			controllerManager.initSDLGamepad();
			available = true;
		} catch (LinkageError | RuntimeException e) {
			// LinkageError couvre UnsatisfiedLinkError (bibliothèque native introuvable)
			controllerManager = null;
			available = false;
			logger.warn("Manette Xbox désactivée : impossible de charger Jamepad ({}). Le jeu se joue au clavier.",
					e.toString());
		}
	}
}
