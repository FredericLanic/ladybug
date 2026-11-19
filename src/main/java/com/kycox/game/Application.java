package com.kycox.game;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ImportResource;

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
 * <p>La fenêtre est ouverte une fois le contexte prêt, par {@link MainLadybug}.
 */
@SpringBootApplication
@ImportResource("classpath:application-context.xml")
public class Application {
	public static void main(String[] args) {
		new SpringApplicationBuilder(Application.class)
		        .headless(false)
		        .run(args);
	}
}
