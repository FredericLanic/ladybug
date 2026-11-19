package com.kycox.game.launcher;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Composant de l'interface : fenêtre, vues, clavier, manette, son (étape 2.7).
 *
 * <p>Il n'est créé que dans le profil Spring {@value #PROFILE}, activé par le
 * lanceur ({@code Application}). Sans ce profil, le contexte ne contient que le
 * modèle du jeu : un test peut le charger sans écran ni carte son (test 3.3),
 * et le futur mode réseau pourra faire tourner un hôte sans fenêtre.
 *
 * <p>C'est une <b>méta-annotation</b> : elle regroupe {@code @Component} et
 * {@code @Profile("gui")} sous un nom qui dit à quoi sert la classe.
 * Fiche : {@code doc/patterns/configuration-externalisee.md}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Profile(GuiComponent.PROFILE)
public @interface GuiComponent {
	/** Nom du profil Spring de l'interface. */
	String PROFILE = "gui";
}
