package com.kycox.game.engine.model;

import com.kycox.game.input.InputSource;

/**
 * Une modification du salon (étape 20.3, pattern Command).
 *
 * <p>Le clavier et les manettes produisent ces commandes ; demain, les postes du
 * réseau enverront les mêmes, et l'hôte restera le seul à les appliquer
 * ({@link Lobby#apply(LobbyCommand)}) : c'est l'hôte qui fait autorité (étape 9).
 */
public sealed interface LobbyCommand {

	/** Ligne précédente (−1) ou suivante (+1). */
	record MoveRow(int step) implements LobbyCommand {
	}

	/** Passer de la colonne « personnage » à la colonne « commandes », et inversement. */
	record SwitchColumn() implements LobbyCommand {
	}

	/** Valeur précédente (−1) ou suivante (+1) dans la colonne active de la ligne active. */
	record ChangeValue(int step) implements LobbyCommand {
	}

	/** Personnage précédent (−1) ou suivant (+1) de la ligne active, quelle que soit la colonne (stick gauche / droite). */
	record ChangeCharacter(int step) implements LobbyCommand {
	}

	/** Commandes précédentes (−1) ou suivantes (+1) de la ligne active, quelle que soit la colonne (stick haut / bas). */
	record ChangeControls(int step) implements LobbyCommand {
	}

	/** Annule le dernier choix de la ligne active. */
	record Undo() implements LobbyCommand {
	}

	/**
	 * Valide l'étape en cours de la ligne active (A à la manette, Espace au
	 * clavier) : le personnage, puis les commandes, puis ligne suivante.
	 */
	record Confirm() implements LobbyCommand {
	}

	/**
	 * Revient en arrière (B à la manette, Retour arrière au clavier) : des
	 * commandes au personnage ; sur le personnage, annule le dernier choix.
	 */
	record Back() implements LobbyCommand {
	}

	/**
	 * Un appareil s'annonce (une manette sur laquelle on appuie sur A ; demain, un
	 * poste du réseau) : il prend la ligne qui attend un appareil.
	 */
	record Claim(InputSource device) implements LobbyCommand {
	}
}
