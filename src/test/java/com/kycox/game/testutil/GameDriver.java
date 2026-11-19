package com.kycox.game.testutil;

import java.nio.file.Path;

import javax.sound.sampled.AudioSystem;

import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.kycox.game.Application;
import com.kycox.game.constant.contract.GameModelForController;
import com.kycox.game.constant.game.GameStatus;
import com.kycox.game.engine.model.GameModel;
import com.kycox.game.tools.GameRandom;

/**
 * Banc d'essai du jeu entier (étape 3.5.2a) : le vrai jeu (Spring Boot + XML),
 * sans écran ni son, piloté battement par battement.
 *
 * <ul>
 * <li>la graine du hasard est fixée ({@link GameRandom#reseed(long)}) ;</li>
 * <li>la boucle Swing ne démarre pas : c'est le test qui appelle
 * {@link GameModel#beat()} ;</li>
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
	/** Durée de la fin de partie, comme dans {@code Engine}. */
	private static final long TIME_ENDING_MILLISECONDS = 1500;

	private final ConfigurableApplicationContext context;
	private final GameModel gameModel;
	private final GameJournal journal;
	private int beatCount;

	private GameDriver(ConfigurableApplicationContext context, long seed) {
		this.context = context;
		this.journal = new GameJournal(seed);
		this.gameModel = context.getBean(GameModel.class);
		applySoundDurations(gameModel);
	}

	/** Démarre une partie avec cette graine ; la sauvegarde va dans {@code saveDirectory}. */
	public static GameDriver start(long seed, Path saveDirectory) {
		System.setProperty(GameModel.LOOP_AUTOSTART_PROPERTY, "false");
		GameRandom.reseed(seed);
		var context = new SpringApplicationBuilder(Application.class)
		        .headless(true)
		        .bannerMode(Banner.Mode.OFF)
		        .logStartupInfo(false)
		        .properties("ladybug.save.dir=" + saveDirectory.toAbsolutePath())
		        .run();
		return new GameDriver(context, seed);
	}

	/**
	 * Mêmes durées qu'en jeu ({@code Engine.init}), lues dans les fichiers son
	 * eux-mêmes : pas besoin de carte son.
	 */
	private static void applySoundDurations(GameModel gameModel) {
		gameModel.getLadybugDying().setAgonyDuration(wavMilliseconds("ladybug_death.wav"));
		gameModel.setBeginningDuration(wavMilliseconds("game_begin_level.wav"));
		gameModel.setTimeEndingMilliseconds(TIME_ENDING_MILLISECONDS);
		gameModel.setEndingLevelMilliseconds(wavMilliseconds("ladybug_intermission.wav"));
	}

	/** Durée d'un fichier son, arrondie comme {@code Clip.getMicrosecondLength() / 1000}. */
	static long wavMilliseconds(String fileName) {
		var url = GameDriver.class.getClassLoader().getResource("sound/wav/" + fileName);
		try {
			var format = AudioSystem.getAudioFileFormat(url);
			var microseconds = (long) (format.getFrameLength() * 1_000_000L / format.getFormat().getFrameRate());
			return microseconds / 1000;
		} catch (Exception e) {
			throw new IllegalStateException("Cannot read sound " + fileName, e);
		}
	}

	/** Les « touches » du jeu, comme les utilise le clavier. */
	public GameModelForController keys() {
		return gameModel;
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
