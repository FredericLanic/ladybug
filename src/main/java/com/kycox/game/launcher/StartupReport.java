package com.kycox.game.launcher;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * Écrit dans les logs, au démarrage, la configuration réellement utilisée
 * (étapes 2.3 à 2.5) : on sait ainsi tout de suite si un fichier externe a été
 * pris en compte.
 */
@Component
public class StartupReport {
	private static final Logger logger = LoggerFactory.getLogger(StartupReport.class);

	private final String springXml;
	private final LaunchMode launchMode;

	public StartupReport(@Value("${" + ConfigLocations.SPRING_XML_PROPERTY + ":"
	        + ConfigLocations.DEFAULT_SPRING_XML + "}") String springXml,
	        @Value("${ladybug.mode:LOCAL}") LaunchMode launchMode) {
		this.springXml = springXml;
		this.launchMode = launchMode;
	}

	@PostConstruct
	void report() {
		var workingDirectory = Path.of("").toAbsolutePath();
		logger.info("Launch mode: {}", launchMode);
		logger.info("Spring XML configuration: {}", springXml);
		logger.info("Game properties: {}", ConfigLocations.findExternal(workingDirectory, ConfigLocations.GAME_PROPERTIES)
		        .map(path -> path + " (external, overrides the jar)")
		        .orElse("classpath:" + ConfigLocations.GAME_PROPERTIES + " (jar)"));
	}
}
