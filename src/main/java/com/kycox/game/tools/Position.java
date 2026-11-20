package com.kycox.game.tools;

/**
 * Une position (ou une direction) dans le jeu : deux entiers, et rien d'autre
 * (étape 4.3, pattern <b>Value Object</b>, fiche {@code doc/patterns/value-object.md}).
 *
 * <p>Elle remplace {@code java.awt.Point} dans le modèle. Deux différences
 * comptent :
 * <ul>
 * <li><b>immuable</b> : {@link #translate(int, int)} renvoie une nouvelle
 * position au lieu de modifier celle-ci. Avant, la constante partagée
 * {@code POINT_ZERO} pouvait être déplacée sur place et changer pour tout le jeu
 * (dette D21) ;</li>
 * <li><b>sans AWT</b> : le modèle n'importe plus rien de la bibliothèque
 * graphique, ce qui prépare un hôte réseau sans fenêtre (étape 9).</li>
 * </ul>
 *
 * <p>Comme tout {@code record}, deux positions de mêmes coordonnées sont égales
 * ({@code equals}), et l'affichage donne {@code Position[x=1, y=2]}.
 *
 * @param x abscisse (vers la droite)
 * @param y ordonnée (vers le bas, comme à l'écran)
 */
public record Position(int x, int y) {

	/** Nouvelle position, décalée de {@code dx} et {@code dy}. Celle-ci ne change pas. */
	public Position translate(int dx, int dy) {
		return new Position(x + dx, y + dy);
	}

	/**
	 * Distance à vol d'oiseau jusqu'à {@code other}. Même calcul que
	 * {@code java.awt.Point.distance} : les fantômes prennent exactement les mêmes
	 * décisions qu'avant l'étape 4.3.
	 */
	public double distance(Position other) {
		double dx = (double) other.x - x;
		double dy = (double) other.y - y;
		return Math.sqrt(dx * dx + dy * dy);
	}
}
