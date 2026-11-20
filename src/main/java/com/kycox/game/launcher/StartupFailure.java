package com.kycox.game.launcher;

import java.awt.GraphicsEnvironment;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedExceptionUtils;

/**
 * Échec du démarrage (décision C27) : un XML externe invalide (faute de frappe,
 * fantôme mal câblé) ne doit pas être remplacé en silence par celui du jar.
 * On l'explique clairement, dans les logs et dans une boîte de dialogue, puis
 * le jeu s'arrête.
 */
public final class StartupFailure {
	private static final Logger logger = LoggerFactory.getLogger(StartupFailure.class);

	/** Explique l'échec et arrête le jeu (code de sortie 1). */
	public static void reportAndExit(Throwable failure, String springXml) {
		var cause = NestedExceptionUtils.getMostSpecificCause(failure);
		// ex. « java.lang.ClassNotFoundException: com.kycox.game.body.ghost.Clydee »
		var causeText = cause.toString();
		var message = "Ladybug n'a pas pu démarrer.\n\n"
		        + "Configuration Spring : " + springXml + "\n"
		        + "Cause : " + causeText;
		if (springXml.startsWith("file:")) {
			message += "\n\nCorrigez ce fichier, ou supprimez-le pour revenir à la configuration du jar.";
		}
		logger.error(message.replace("\n\n", " ").replace('\n', ' '));
		if (!GraphicsEnvironment.isHeadless()) {
			JOptionPane.showMessageDialog(null, message, "Ladybug", JOptionPane.ERROR_MESSAGE);
		}
		System.exit(1);
	}

	private StartupFailure() {
	}
}
