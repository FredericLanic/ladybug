package com.kycox.game.testutil;

import java.nio.file.Path;

import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.kycox.game.Application;
import com.kycox.game.constant.contract.GameModelForController;
import com.kycox.game.constant.game.GameStatus;
import com.kycox.game.constant.ghost.GhostsBodyImages;
import com.kycox.game.engine.model.GameModel;
import com.kycox.game.tools.GameRandom;

/**
 * Banc d'essai du jeu entier (étape 3.5.2a) : le vrai jeu (Spring Boot + XML),
 * sans écran ni son, piloté battement par battement.
 *
 * <ul>
 * <li>la graine du hasard est fixée ({@link GameRandom#reseed(long)}) ;</li>
 * <li>pas de boucle Swing (elle n'existe qu'avec le profil « gui », étape
 * 4.1) : c'est le test qui appelle {@link GameModel#beat()} ;</li>
 * <li>les « touches » passent par {@link GameModelForController}, exactement
 * comme le clavier ;</li>
 * <li>la sauvegarde du niveau va dans un dossier du test, jamais dans celle du
 * joueur.</li>
 * </ul>
 *
 * <p>Chaque {@code GameDriver} crée un contexte neuf : deux parties ne partagent
 * aucun état. À fermer après usage ({@code try-with-resources}).
 */
public final class GameDriver implements AutoCloseable {
	private final ConfigurableApplicationContext context;
	private final GameModel gameModel;
	private final GameJournal journal;
	private int beatCount;

	private GameDriver(ConfigurableApplicationContext context, long seed) {
		this.context = context;
		this.journal = new GameJournal(seed);
		this.gameModel = context.getBean(GameModel.class);
	}

	/** Démarre une partie avec cette graine ; la sauvegarde va dans {@code saveDirectory}. */
	public static GameDriver start(long seed, Path saveDirectory) {
		// « fantôme joué par un humain » : état partagé dans une énumération (CTEC1),
		// qui survivrait d'une partie à l'autre : on repart de « tous joués par l'ordinateur »
		for (var ghostImage : GhostsBodyImages.values()) {
			ghostImage.setComputed(true);
		}
		GameRandom.reseed(seed);
		var context = new SpringApplicationBuilder(Application.class)
		        .headless(true)
		        .bannerMode(Banner.Mode.OFF)
		        .logStartupInfo(false)
		        .properties("ladybug.save.dir=" + saveDirectory.toAbsolutePath())
		        .run();
		return new GameDriver(context, seed);
	}

	/** Les « touches » du jeu, comme les utilise le clavier. */
	public GameModelForController keys() {
		return gameModel;
	}

	/** Un composant du jeu (par exemple {@code GameProperties}). */
	public <T> T bean(Class<T> type) {
		return context.getBean(type);
	}

	public GameModel model() {
		return gameModel;
	}

	public GameStatus status() {
		return gameModel.getCurrentProgramStatus().getGameStatus();
	}

	/** Nombre de battements joués depuis le début. */
	public int beatCount() {
		return beatCount;
	}

	/** Journal de la partie (3.5.2b), mis à jour après chaque battement. */
	public GameJournal journal() {
		return journal;
	}

	/** Joue un battement, puis le note dans le journal. */
	public GameDriver beat() {
		gameModel.beat();
		beatCount++;
		journal.record(beatCount, gameModel);
		return this;
	}

	/** Joue {@code n} battements. */
	public GameDriver beats(int n) {
		for (var i = 0; i < n; i++) {
			beat();
		}
		return this;
	}

	/**
	 * Joue jusqu'à atteindre ce statut, au plus {@code maxBeats} battements.
	 *
	 * @return le numéro du battement où le statut est atteint
	 */
	public int beatsUntil(GameStatus expected, int maxBeats) {
		for (var i = 0; i < maxBeats && status() != expected; i++) {
			beat();
		}
		if (status() != expected) {
			throw new AssertionError("Status " + expected + " not reached after " + maxBeats + " beats (still "
			        + status() + ")");
		}
		return beatCount;
	}

	@Override
	public void close() {
		context.close();
	}
}
