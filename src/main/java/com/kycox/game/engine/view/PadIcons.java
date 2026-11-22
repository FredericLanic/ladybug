package com.kycox.game.engine.view;

import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Les symboles d'une manette Xbox actuelle (les deux petits boutons du centre
 * et la croix), dessinés dans une ligne de texte (étape 20.1) : la police du
 * jeu ne les contient pas.
 * <ul>
 * <li>{@value #MENU} : le bouton Menu, trois traits (≡) ; l'ancien Start ;</li>
 * <li>{@value #VIEW} : le bouton Affichage, deux carrés superposés (⧉) ;
 * l'ancien Back ;</li>
 * <li>{@value #DPAD_UP}, {@value #DPAD_DOWN}, {@value #DPAD_LEFT},
 * {@value #DPAD_RIGHT} : la croix directionnelle, la branche concernée
 * remplie (demande de Frédéric : « un dessin simple »).</li>
 * </ul>
 * Dans les textes ({@code messages*.properties}), on écrit par exemple
 * {@code {MENU}: pAUSE} ; l'icône prend la place du mot, à la taille et dans la
 * couleur du texte.
 */
public final class PadIcons {
	public static final String MENU = "{MENU}";
	public static final String VIEW = "{VIEW}";
	public static final String DPAD_UP = "{DPAD_UP}";
	public static final String DPAD_DOWN = "{DPAD_DOWN}";
	public static final String DPAD_LEFT = "{DPAD_LEFT}";
	public static final String DPAD_RIGHT = "{DPAD_RIGHT}";
	private static final Pattern TOKEN = Pattern.compile("\\{(MENU|VIEW|DPAD_UP|DPAD_DOWN|DPAD_LEFT|DPAD_RIGHT)}");

	private PadIcons() {
	}

	/** Largeur de la ligne, icônes comprises. */
	public static int width(String line, FontMetrics metrics) {
		var width = 0;
		for (var part : parts(line)) {
			width += isIcon(part) ? iconWidth(metrics) : metrics.stringWidth(part);
		}
		return width;
	}

	/** Dessine la ligne à partir de {@code x}, sur la ligne de base {@code baseline}. */
	public static void draw(Graphics2D g2d, String line, int x, int baseline, FontMetrics metrics) {
		var cursor = x;
		for (var part : parts(line)) {
			if (isIcon(part)) {
				drawIcon(g2d, part, cursor, baseline, metrics);
				cursor += iconWidth(metrics);
			} else {
				g2d.drawString(part, cursor, baseline);
				cursor += metrics.stringWidth(part);
			}
		}
	}

	/** Le texte découpé en morceaux : du texte, ou un jeton d'icône. */
	static List<String> parts(String line) {
		var parts = new ArrayList<String>();
		var matcher = TOKEN.matcher(line);
		var start = 0;
		while (matcher.find()) {
			if (matcher.start() > start) {
				parts.add(line.substring(start, matcher.start()));
			}
			parts.add(matcher.group());
			start = matcher.end();
		}
		if (start < line.length()) {
			parts.add(line.substring(start));
		}
		return parts;
	}

	private static boolean isIcon(String part) {
		return TOKEN.matcher(part).matches();
	}

	/** Une icône occupe un carré de la hauteur des majuscules, plus une marge. */
	private static int iconWidth(FontMetrics metrics) {
		return iconSize(metrics) + iconSize(metrics) / 2;
	}

	/**
	 * Hauteur réelle d'une majuscule : la police du jeu dessine des lettres plus
	 * grandes que ce qu'annoncent ses mesures ({@code getAscent}).
	 */
	private static int iconSize(FontMetrics metrics) {
		var bounds = metrics.getFont().createGlyphVector(metrics.getFontRenderContext(), "H").getVisualBounds();
		return Math.max(6, (int) Math.round(bounds.getHeight()));
	}

	private static void drawIcon(Graphics2D g2d, String icon, int x, int baseline, FontMetrics metrics) {
		var size = iconSize(metrics);
		var left = x + size / 4;
		var top = baseline - size;
		var thickness = Math.max(2, size / 12); // trait fin, comme les lettres du jeu
		var previousStroke = g2d.getStroke();
		g2d.setStroke(new BasicStroke(thickness));
		if (MENU.equals(icon)) {
			// ≡ : trois traits horizontaux
			for (var i = 0; i < 3; i++) {
				var y = top + thickness + i * (size - 2 * thickness) / 2;
				g2d.drawLine(left, y, left + size, y);
			}
		} else if (VIEW.equals(icon)) {
			// ⧉ : deux carrés, le second décalé en bas à droite
			var square = size * 2 / 3;
			g2d.drawRect(left, top, square, square);
			g2d.drawRect(left + size - square, top + size - square, square, square);
		} else {
			drawDpad(g2d, icon, left, top, size);
		}
		g2d.setStroke(previousStroke);
	}

	/**
	 * La croix : un seul contour en forme de +, symétrique, et la branche concernée
	 * remplie à l'intérieur du contour (22/11/2026 : cases tracées une à une, les
	 * branches n'avaient pas toutes la même taille).
	 */
	private static void drawDpad(Graphics2D g2d, String icon, int left, int top, int size) {
		var arm = Math.max(2, size / 3);
		var a = 3 * arm; // côté de la croix, multiple de la largeur d'une branche
		var cross = new Path2D.Float();
		cross.moveTo(left + arm, top);
		cross.lineTo(left + 2 * arm, top);
		cross.lineTo(left + 2 * arm, top + arm);
		cross.lineTo(left + a, top + arm);
		cross.lineTo(left + a, top + 2 * arm);
		cross.lineTo(left + 2 * arm, top + 2 * arm);
		cross.lineTo(left + 2 * arm, top + a);
		cross.lineTo(left + arm, top + a);
		cross.lineTo(left + arm, top + 2 * arm);
		cross.lineTo(left, top + 2 * arm);
		cross.lineTo(left, top + arm);
		cross.lineTo(left + arm, top + arm);
		cross.closePath();
		// branche remplie : même géométrie que le contour, donc symétrique
		var column = switch (icon) {
			case DPAD_LEFT -> 0;
			case DPAD_RIGHT -> 2;
			default -> 1;
		};
		var row = switch (icon) {
			case DPAD_UP -> 0;
			case DPAD_DOWN -> 2;
			default -> 1;
		};
		g2d.fill(new Rectangle2D.Float(left + column * arm, top + row * arm, arm, arm));
		g2d.draw(cross);
	}
}
