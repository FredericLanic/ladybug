package com.kycox.game.launcher;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Où chercher les fichiers de configuration externes (étapes 2.3 et 2.4).
 *
 * <p>Convention de Spring Boot (décision C29) : un fichier posé dans le dossier
 * courant, ou dans son sous-dossier {@code config/}, l'emporte sur celui du jar ;
 * {@code config/} passe avant le dossier courant. C'est le principe de la
 * <b>configuration externalisée</b> : chaque réglage peut être remplacé par un
 * réglage plus proche de l'utilisateur, sans recompiler.
 * Fiche : {@code doc/patterns/configuration-externalisee.md}.
 *
 * <pre>
 *   ligne de commande (--ladybug.config=…)      ← gagne toujours
 *   ./config/application-context.xml
 *   ./application-context.xml
 *   application-context.xml du jar             ← valeur par défaut
 * </pre>
 */
public final class ConfigLocations {
	/** Nom du fichier XML Spring qui câble les fantômes. */
	public static final String SPRING_XML = "application-context.xml";
	/** Propriété qui indique le XML à charger (lue par {@code @ImportResource}). */
	public static final String SPRING_XML_PROPERTY = "ladybug.config";
	/** XML du jar, utilisé quand aucun fichier externe n'existe. */
	public static final String DEFAULT_SPRING_XML = "classpath:" + SPRING_XML;
	/** Fichier des réglages d'apparence (chapeau, bandeau, peau de Ladybug). */
	public static final String GAME_PROPERTIES = "ladybug.properties";

	/**
	 * Fichier externe à utiliser, en cherchant d'abord dans {@code config/}, puis
	 * dans le dossier lui-même.
	 */
	public static Optional<Path> findExternal(Path workingDirectory, String fileName) {
		return List.of(workingDirectory.resolve("config").resolve(fileName), workingDirectory.resolve(fileName))
		        .stream()
		        .filter(Files::isRegularFile)
		        .findFirst()
		        .map(Path::toAbsolutePath);
	}

	/**
	 * Emplacement du XML Spring à charger, au format attendu par
	 * {@code @ImportResource} : {@code file:…} si un fichier externe existe, sinon
	 * celui du jar.
	 */
	public static String springXmlLocation(Path workingDirectory) {
		return findExternal(workingDirectory, SPRING_XML)
		        .map(path -> path.toUri().toString())
		        .orElse(DEFAULT_SPRING_XML);
	}

	/**
	 * XML réellement utilisé : celui de la ligne de commande
	 * ({@code --ladybug.config=…}) ou d'une propriété système
	 * ({@code -Dladybug.config=…}) s'il y en a un, sinon {@code defaultLocation}.
	 * Sert au message d'erreur de démarrage.
	 */
	public static String effectiveSpringXml(String[] args, String defaultLocation) {
		var prefix = "--" + SPRING_XML_PROPERTY + "=";
		String fromArgs = null;
		for (var arg : args) {
			if (arg.startsWith(prefix)) {
				fromArgs = arg.substring(prefix.length());
			}
		}
		if (fromArgs != null) {
			return fromArgs;
		}
		return System.getProperty(SPRING_XML_PROPERTY, defaultLocation);
	}

	private ConfigLocations() {
	}
}
