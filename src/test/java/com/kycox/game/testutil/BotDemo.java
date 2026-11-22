package com.kycox.game.testutil;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationListener;

import com.kycox.game.Application;
import com.kycox.game.constant.game.GameStatus;
import com.kycox.game.engine.model.EventGameModel;
import com.kycox.game.engine.model.GameModel;
import com.kycox.game.input.PlayerInputs;
import com.kycox.game.launcher.ConfigLocations;
import com.kycox.game.launcher.GuiComponent;

/**
 * Démonstration : le vrai jeu, avec sa fenêtre, où Ladybug est jouée par le
 * {@link AimingBot} (fuir, chasser, manger).
 *
 * <p>Rangé dans les tests, comme le bot : il n'entre jamais dans le jar livré, et
 * le jeu n'a pas de mode « bot » (cahier des charges des tests autonomes). Le
 * jeu démarre exactement comme {@link Application#main} ; la seule différence
 * est un écouteur branché sur chaque battement, qui écrit la décision du bot
 * dans la boîte aux lettres de Ladybug, comme le ferait le clavier.
 *
 * <p>Lancement, depuis la racine du projet :
 * <pre>
 * mvn -q test-compile exec:java -Dexec.mainClass=com.kycox.game.testutil.BotDemo -Dexec.classpathScope=test
 * </pre>
 * ou, dans IntelliJ, le triangle vert à côté de {@code main}.
 *
 * <p>La démo démarre toute seule : après {@value #PRESENTATION_BEATS} battements
 * (≈ 4 s) sur l'écran de présentation, elle fait comme les touches S puis N
 * (nouvelle partie au niveau 1), et recommence à chaque fin de partie. Le
 * clavier reste actif : appuyer sur 2 pendant la présentation pour jouer le
 * fantôme humain en ZQSD contre le bot ; Échap pour quitter.
 */
public final class BotDemo {
	private static final Logger logger = LoggerFactory.getLogger(BotDemo.class);
	/** Battements d'attente sur la présentation avant de lancer une partie (100 × 40 ms). */
	static final int PRESENTATION_BEATS = 100;

	public static void main(String[] args) {
		var springXml = ConfigLocations.springXmlLocation(Path.of(""));
		var context = new SpringApplicationBuilder(Application.class)
		        .headless(false)
		        .profiles(GuiComponent.PROFILE)
		        .properties(ConfigLocations.SPRING_XML_PROPERTY + "=" + springXml)
		        .run(args);

		var model = context.getBean(GameModel.class);
		var ladybug = context.getBean(PlayerInputs.class).ladybug();
		var bot = new AimingBot();
		var beatsOnPresentation = new AtomicInteger();
		var answered = new AtomicBoolean();
		ApplicationListener<EventGameModel> playsLadybug = event -> {
			var snapshot = event.getSnapshot();
			// comme S, puis N : nouvelle partie au niveau 1 (hors du battement en cours)
			if (snapshot.is(GameStatus.PROGRAM_PRESENTATION)) {
				if (beatsOnPresentation.incrementAndGet() == PRESENTATION_BEATS) {
					SwingUtilities.invokeLater(model::startGame);
				}
			} else {
				beatsOnPresentation.set(0);
			}
			// une seule fois par question, même si elle dure plusieurs battements
			if (snapshot.is(GameStatus.PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL)) {
				if (!answered.getAndSet(true)) {
					SwingUtilities.invokeLater(() -> model.initializeLevelNumAndStartGame(true));
				}
			} else {
				answered.set(false);
			}
			if (snapshot.is(GameStatus.IN_GAME)) {
				var direction = bot.decide(model);
				if (direction != null) {
					ladybug.request(direction);
				}
			}
		};
		context.addApplicationListener(playsLadybug);
		logger.info("Bot demo: Ladybug is driven by the AimingBot; a game starts by itself after the presentation");
	}

	private BotDemo() {
	}
}
