package com.kycox.game.body.ghost;

/**
 * Couleur d'un fantôme, côté modèle (étape 4.2) : une simple valeur, sans image.
 *
 * <p>C'est la vue qui choisit l'image correspondante
 * ({@code GhostsBodyImages.of(color)}). Avant 4.2, le modèle manipulait
 * directement l'énumération des images, qui portait en plus le drapeau « fantôme
 * joué par un humain ».
 *
 * <p>L'ordre des valeurs compte : le fantôme du deuxième joueur est tiré au hasard
 * parmi elles ({@code GameRandom.randomEnum}) ; le changer changerait les parties
 * rejouées avec une même graine (golden master).
 */
public enum GhostColor {
	CYAN, ORANGE, PINK, RED
}
