package com.kycox.game.engine.view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.function.Function;

import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.engine.view.map.MazeColors;
import com.kycox.game.snapshot.LobbySnapshot;

/**
 * Le dessin du salon (étape 20.3), à partir de sa seule photo
 * ({@link LobbySnapshot}) : une ligne par joueur (numéro, personnage dessiné,
 * commandes), la ligne active surlignée, la colonne active entre chevrons, puis
 * ce qui manque encore et les boutons « jouer » et « retour » (20.3 ter).
 *
 * <p>Séparé de {@code CentralView} pour être dessiné hors du jeu (tests, aperçus),
 * et demain par un poste du réseau.
 */
public final class LobbyDrawing {
	private static final Color HIGHLIGHT = MazeColors.BLUE_LADYBUG;
	private static final float HIGHLIGHT_OPACITY = 0.45f;
	/** Taille du texte des lignes et des notices : plus petite que le titre, pour tenir dans 15 cases. */
	private static final float TEXT_SIZE = 24f;
	/** Écart entre deux lignes de joueurs. */
	private static final int ROW_GAP = GameMainConstants.BLOCK_SIZE * 3 / 2;
	/** Un bouton qu'on ne peut pas encore choisir (« jouer » tant que le salon n'est pas prêt). */
	private static final Color DISABLED = Color.gray;

	private LobbyDrawing() {
	}

	/**
	 * @param messages  les textes du jeu, par clé ({@code lobby.title}…)
	 * @param images    le dessin d'un personnage par son nom (Ladybug ou un fantôme)
	 * @param width     largeur du labyrinthe en pixels
	 * @param height    hauteur du labyrinthe en pixels
	 */
	public static void draw(Graphics2D g2d, LobbySnapshot lobby, Function<String, String> messages,
	        Function<String, Image> images, int width, int height, FontMetrics metrics) {
		var size = GameMainConstants.BLOCK_SIZE;
		var top = height / 2 - 6 * size;
		g2d.setFont(metrics.getFont());
		g2d.setColor(Color.white);
		centered(g2d, messages.apply("lobby.title"), top, width, metrics);

		// le reste en plus petit
		var small = metrics.getFont().deriveFont(TEXT_SIZE);
		g2d.setFont(small);
		metrics = g2d.getFontMetrics(small);
		var characterX = size;
		var controlsX = 8 * size;
		var headerY = top + 2 * size;
		g2d.drawString(messages.apply("lobby.header.character"), characterX + size, headerY);
		g2d.drawString(messages.apply("lobby.header.controls"), controlsX + size / 2, headerY);

		for (var i = 0; i < lobby.rows().size(); i++) {
			var row = lobby.rows().get(i);
			var baseline = headerY + (i + 1) * ROW_GAP;
			var active = i == lobby.activeRow();
			if (active) {
				var previous = g2d.getComposite();
				g2d.setComposite(AlphaComposite.SrcOver.derive(HIGHLIGHT_OPACITY));
				g2d.setColor(HIGHLIGHT);
				g2d.fillRoundRect(size / 4, baseline - size - size / 4, width - size / 2, size + size / 2, size / 2,
				        size / 2);
				g2d.setComposite(previous);
				g2d.setColor(Color.white);
			}

			var characterText = row.character() == null ? messages.apply("lobby.none") : label(row.character(), messages);
			var characterLeft = characterX + size + size / 4; // aligné, avec ou sans dessin
			if (row.character() != null) {
				var image = images.apply(row.character());
				if (image != null) {
					g2d.drawImage(image, characterX, baseline - size + 4, size - 4, size - 4, null);
				}
			}
			cell(g2d, characterText, characterLeft, baseline, active && "CHARACTER".equals(lobby.activeColumn()),
			        metrics);

			var controlsText = row.character() == null ? ""
			        : row.waitingForPad() ? messages.apply("lobby.waitPad")
			        : messages.apply("lobby.device." + row.device());
			cell(g2d, controlsText, controlsX, baseline, active && "CONTROLS".equals(lobby.activeColumn()), metrics);
		}

		// ce qui manque encore, au-dessus des boutons (rien quand tout est prêt)
		var statusY = headerY + (lobby.rows().size() + 1) * ROW_GAP + size / 2;
		if (!lobby.ready()) {
			var status = lobby.rows().stream().noneMatch(r -> "Ladybug".equals(r.character()) && r.device() != null)
			        ? "lobby.needLadybug" : "lobby.needControls";
			centered(g2d, messages.apply(status), statusY, width, metrics);
		}

		// la ligne des boutons, juste après les joueurs
		var onButtons = lobby.activeRow() == lobby.rows().size();
		var play = messages.apply("lobby.button.play");
		var back = messages.apply("lobby.button.back");
		var buttonWidth = Math.max(PadIcons.width(play, metrics), PadIcons.width(back, metrics))
		        + capHeight(metrics).height(); // une demi-majuscule de chaque côté
		var gap = size;
		var left = (width - 2 * buttonWidth - gap) / 2;
		var buttonsY = statusY + 2 * size;
		button(g2d, play, left, buttonsY, buttonWidth, onButtons && "PLAY".equals(lobby.activeColumn()),
		        lobby.ready(), metrics);
		button(g2d, back, left + buttonWidth + gap, buttonsY, buttonWidth,
		        onButtons && "BACK".equals(lobby.activeColumn()), true, metrics);
	}

	/**
	 * Un bouton : un cadre arrondi, ajusté à la hauteur réelle des lettres, et son
	 * texte centré ; surligné s'il est choisi, gris s'il ne peut pas encore servir.
	 */
	private static void button(Graphics2D g2d, String text, int x, int baseline, int buttonWidth, boolean focused,
	        boolean enabled, FontMetrics metrics) {
		var letters = capHeight(metrics);
		var margin = Math.max(3, letters.height() / 3);
		var top = baseline + letters.top() - margin;
		var height = letters.height() + 2 * margin;
		var arc = margin;
		if (focused) {
			var previous = g2d.getComposite();
			g2d.setComposite(AlphaComposite.SrcOver.derive(HIGHLIGHT_OPACITY));
			g2d.setColor(HIGHLIGHT);
			g2d.fillRoundRect(x, top, buttonWidth, height, arc, arc);
			g2d.setComposite(previous);
		}
		g2d.setColor(enabled ? Color.white : DISABLED);
		g2d.drawRoundRect(x, top, buttonWidth, height, arc, arc);
		PadIcons.draw(g2d, text, x + (buttonWidth - PadIcons.width(text, metrics)) / 2, baseline, metrics);
		g2d.setColor(Color.white);
	}

	/**
	 * Hauteur réelle d'une majuscule (la police du jeu ne correspond pas à ce
	 * qu'annonce {@code getAscent}) : les boutons s'ajustent dessus.
	 */
	private static Letters capHeight(FontMetrics metrics) {
		var bounds = metrics.getFont().createGlyphVector(metrics.getFontRenderContext(), "H").getVisualBounds();
		return new Letters((int) Math.floor(bounds.getY()), Math.max(6, (int) Math.round(bounds.getHeight())));
	}

	/** Où commencent les lettres par rapport à la ligne de base (négatif : au-dessus), et leur hauteur. */
	private record Letters(int top, int height) {
	}

	/**
	 * Une cellule ; si elle est la colonne active de la ligne active, entre deux
	 * petits triangles ◀ ▶ (la police du jeu n'a pas de chevrons) : on peut en
	 * changer la valeur avec gauche / droite.
	 */
	private static void cell(Graphics2D g2d, String text, int x, int baseline, boolean focused, FontMetrics metrics) {
		if (text.isEmpty()) {
			return;
		}
		var arrow = Math.max(6, metrics.getAscent() / 2);
		var textX = x + arrow + arrow / 2;
		PadIcons.draw(g2d, text, textX, baseline, metrics);
		if (focused) {
			var middle = baseline - metrics.getAscent() / 3;
			var end = textX + PadIcons.width(text, metrics) + arrow / 2;
			g2d.fillPolygon(new int[] { x + arrow, x + arrow, x }, new int[] { middle - arrow, middle + arrow, middle }, 3);
			g2d.fillPolygon(new int[] { end, end, end + arrow }, new int[] { middle - arrow, middle + arrow, middle }, 3);
		}
	}

	private static void centered(Graphics2D g2d, String line, int baseline, int width, FontMetrics metrics) {
		PadIcons.draw(g2d, line, (width - PadIcons.width(line, metrics)) / 2, baseline, metrics);
	}

	/** Le nom à la façon de la police du jeu : « Clyde » devient « cLYDE » ; Ladybug est Cox. */
	static String label(String character, Function<String, String> messages) {
		if ("Ladybug".equals(character)) {
			return messages.apply("lobby.ladybug");
		}
		return character.substring(0, 1).toLowerCase() + character.substring(1).toUpperCase();
	}
}
