package com.kycox.game.engine.loop;

import static com.kycox.game.constant.game.GameMainConstants.PACE;

import javax.swing.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kycox.game.engine.model.GameModel;
import com.kycox.game.launcher.GuiComponent;
import com.kycox.game.tools.timer.BeatPacer;

/**
 * Boucle du jeu avec l'interface Swing (étape 4.1, pattern <b>Game Loop</b>,
 * fiche {@code doc/patterns/game-loop.md}).
 *
 * <p>Elle fait battre le modèle 25 fois par seconde. Un timer Swing se réveille
 * deux fois par battement ; le {@link BeatPacer} décide combien de battements
 * jouer (0, 1, parfois plusieurs pour rattraper un retard). Les battements sont
 * joués sur le fil Swing, celui qui dessine : le modèle n'est jamais modifié
 * pendant un dessin.
 *
 * <p>Avant l'étape 4.1, ce timer vivait dans {@code GameModel}, qui dépendait
 * donc de Swing. Le modèle n'expose plus que {@link GameModel#beat()} ; qui le
 * fait battre est un choix extérieur : cette classe avec l'interface, un test
 * ({@code GameDriver}), plus tard un hôte réseau sans fenêtre.
 *
 * <p>Composant de l'interface ({@link GuiComponent}) : il n'existe pas dans les
 * tests, qui restent maîtres du temps.
 */
@GuiComponent
public class SwingGameLoop {
	/** Battements rattrapés au plus d'un coup après un retard (mise en veille…). */
	static final int MAX_CATCH_UP = 5;

	private static final Logger logger = LoggerFactory.getLogger(SwingGameLoop.class);

	private final GameModel gameModel;
	private final BeatPacer beatPacer = new BeatPacer(PACE, MAX_CATCH_UP);
	private final Timer timer = new Timer(PACE / 2, event -> wakeUp(System.nanoTime()));

	public SwingGameLoop(GameModel gameModel) {
		this.gameModel = gameModel;
	}

	/** Démarre la boucle (appelé une fois la fenêtre affichée). */
	public void start() {
		logger.info("Game loop started: one beat every {} ms", PACE);
		timer.start();
	}

	/** Un réveil du timer : joue les battements dus à l'heure {@code nowNanos}. */
	void wakeUp(long nowNanos) {
		var beats = beatPacer.beatsDue(nowNanos);
		for (var i = 0; i < beats; i++) {
			gameModel.beat();
		}
	}
}
