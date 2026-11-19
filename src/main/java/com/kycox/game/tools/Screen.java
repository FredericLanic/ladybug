package com.kycox.game.tools;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.launcher.GuiComponent;

/**
 * Dimensions de l'écran où s'affiche le jeu (étape 2.8, dette D18).
 *
 * <p>Avant : {@code Toolkit.getScreenSize()} donnait la taille de l'écran
 * <b>principal</b>, barre de menus et Dock compris. Sur un Mac, il restait donc
 * 687 px pour un labyrinthe de 720 : le bas était coupé. Et avec deux écrans,
 * le jeu s'ouvrait toujours sur l'écran principal.
 *
 * <p>Après : on prend l'écran où se trouve la souris au lancement, et seulement
 * la place réellement disponible (bornes de l'écran moins barre de menus et
 * Dock, {@code Toolkit.getScreenInsets}).
 *
 * <p>Répartition verticale (demandes de Frédéric) : la zone du bas (score et
 * animation de Ladybug) reste collée sous le labyrinthe ; elle prend la moitié de
 * la place libre, entre deux et trois cases de haut. Le reste est partagé à
 * égalité au-dessus et en dessous, pour que le bloc soit centré.
 *
 * <pre>
 *   bande du haut (vide)     getTopHeight()
 *   labyrinthe               getEdgeGameSide()      720 px
 *   zone du bas              getBottomZoneHeight()   96 à 144 px
 *   bande du bas (vide)      getBottomGapHeight()
 * </pre>
 */
@GuiComponent
public class Screen {
	private static final Logger logger = LoggerFactory.getLogger(Screen.class);
	/** Côté du labyrinthe : 15 cases. */
	private static final double EDGE_GAME_SIDE = 15.0 * GameMainConstants.BLOCK_SIZE;
	/** Zone du bas : au moins deux cases de haut (une pour le score, une pour l'animation)… */
	private static final double MIN_BOTTOM_ZONE_HEIGHT = 2.0 * GameMainConstants.BLOCK_SIZE;
	/** … et au plus trois, pour ne pas manger l'écran. */
	private static final double MAX_BOTTOM_ZONE_HEIGHT = 3.0 * GameMainConstants.BLOCK_SIZE;

	private final Rectangle usableBounds;

	/** Écran de la souris au lancement (utilisé par Spring). */
	public Screen() {
		this(usableBoundsOfMouseScreen());
		logger.info("Game screen: usable area {}x{} at ({},{})", usableBounds.width, usableBounds.height,
		        usableBounds.x, usableBounds.y);
	}

	/** Zone disponible donnée (utilisé par les tests). */
	Screen(Rectangle usableBounds) {
		this.usableBounds = new Rectangle(usableBounds);
	}

	/**
	 * Place réellement disponible sur l'écran où se trouve la souris : bornes de
	 * l'écran moins barre de menus, Dock ou barre des tâches.
	 */
	static Rectangle usableBoundsOfMouseScreen() {
		var pointer = MouseInfo.getPointerInfo();
		GraphicsConfiguration configuration = pointer != null
		        ? pointer.getDevice().getDefaultConfiguration()
		        : GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		var bounds = configuration.getBounds();
		var insets = Toolkit.getDefaultToolkit().getScreenInsets(configuration);
		return new Rectangle(bounds.x + insets.left, bounds.y + insets.top,
		        bounds.width - insets.left - insets.right, bounds.height - insets.top - insets.bottom);
	}

	/** Zone de l'écran où placer la fenêtre. */
	public Rectangle getUsableBounds() {
		return new Rectangle(usableBounds);
	}

	/**
	 * Hauteur de la zone du bas (score, vies, animation de Ladybug) : la moitié de
	 * la place libre, entre deux et trois cases.
	 */
	public double getBottomZoneHeight() {
		var halfFreeSpace = Math.floor((getScreenHeight() - EDGE_GAME_SIDE) / 2);
		return Math.clamp(halfFreeSpace, MIN_BOTTOM_ZONE_HEIGHT, MAX_BOTTOM_ZONE_HEIGHT);
	}

	/**
	 * Bande vide au-dessus du labyrinthe : la moitié de la place restante. Nulle
	 * sur un écran trop petit pour tout contenir.
	 */
	public double getTopHeight() {
		return Math.max(0, Math.floor((getScreenHeight() - EDGE_GAME_SIDE - getBottomZoneHeight()) / 2));
	}

	/** Bande vide sous la zone du bas : le reste de la place. */
	public double getBottomGapHeight() {
		return Math.max(0, getScreenHeight() - EDGE_GAME_SIDE - getBottomZoneHeight() - getTopHeight());
	}

	public double getBorderWidth() {
		return (getScreenWidth() - EDGE_GAME_SIDE) / 2;
	}

	public double getEdgeGameSide() {
		return EDGE_GAME_SIDE;
	}

	public double getScreenHeight() {
		return usableBounds.getHeight();
	}

	public double getScreenWidth() {
		return usableBounds.getWidth();
	}
}
