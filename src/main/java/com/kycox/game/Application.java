package com.kycox.game;

import java.nio.file.Path;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ImportResource;

import com.kycox.game.launcher.ConfigLocations;
import com.kycox.game.launcher.GuiComponent;
import com.kycox.game.launcher.StartupFailure;

/**
 * Point d'entrée de Ladybug : démarrage par Spring Boot (étape 2.1).
 *
 * <p>Le câblage des fantômes reste dans {@code application-context.xml}
 * (décision C01), chargé par {@link ImportResource}.
 *
 * <p>{@code headless(false)} est indispensable : par défaut, Spring Boot passe la
 * JVM en mode « sans écran » ({@code java.awt.headless=true}), prévu pour les
 * serveurs, et Swing refuse alors d'ouvrir une fenêtre. C'est sans doute ce qui
 * avait fait échouer le premier essai avec {@code SpringApplication.run}.
 *
 * <p>Configuration externalisée (étapes 2.3 et 2.4, fiche
 * {@code doc/patterns/configuration-externalisee.md}) : un
 * {@code application-context.xml} posé dans le dossier courant ou dans
 * {@code config/} remplace celui du jar ; {@code --ladybug.config=…} en impose
 * un autre. Si ce XML est invalide, le jeu le dit et s'arrête (C27).
 *
 * <p>Le lanceur active le profil {@code gui} ({@link GuiComponent}) : fenêtre,
 * vues, clavier, manette et son. Sans lui, le contexte ne contient que le
 * modèle du jeu (test 3.3).
 *
 * <p>La fenêtre est ouverte une fois le contexte prêt, par {@link MainLadybug}.
 */
@SpringBootApplication
@ImportResource("${" + ConfigLocations.SPRING_XML_PROPERTY + ":" + ConfigLocations.DEFAULT_SPRING_XML + "}")
public class Application {
	public static void main(String[] args) {
		// valeur par défaut : la ligne de commande (--ladybug.config=…) l'emporte
		var springXml = ConfigLocations.springXmlLocation(Path.of(""));
		try {
			new SpringApplicationBuilder(Application.class)
			        .headless(false)
			        .profiles(GuiComponent.PROFILE) // fenêtre, vues, clavier, son (2.7)
			        .properties(ConfigLocations.SPRING_XML_PROPERTY + "=" + springXml)
			        .run(args);
		} catch (RuntimeException failure) {
			StartupFailure.reportAndExit(failure, springXml);
		}
	}
}
