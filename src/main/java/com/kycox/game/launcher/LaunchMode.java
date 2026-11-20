package com.kycox.game.launcher;

/**
 * Mode de lancement du jeu (étape 2.5, décision C28).
 *
 * <p>Aujourd'hui, un seul mode : {@link #LOCAL}, tout se joue sur ce poste. Le
 * choix entre 1 et 2 joueurs reste dans le jeu, sur l'écran de présentation.
 * Les modes réseau {@code HOST} (héberger une partie) et {@code JOIN} (la
 * rejoindre) viendront à l'étape 9. Propriété : {@code --ladybug.mode=LOCAL}.
 */
public enum LaunchMode {
	/** Partie sur ce poste (1 ou 2 joueurs, choisis dans le jeu). */
	LOCAL
}
