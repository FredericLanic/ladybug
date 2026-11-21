package com.kycox.game.tools;

import java.security.SecureRandom;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Source unique de hasard du jeu (étape 3.5.1).
 *
 * <p>Tous les tirages du jeu passent par cette classe : direction des fantômes,
 * comportement sensible, fruits, mode lampe, rendu du niveau, messages et
 * musique. Le générateur part d'une <b>graine</b> : avec la même graine, les
 * tirages sortent dans le même ordre. C'est ce qui permettra de rejouer une
 * partie à l'identique dans les tests (golden master, étape 3.5.2).
 *
 * <p>La graine est écrite dans les logs au démarrage. Pour rejouer une partie,
 * on la redonne au lancement : {@code java -Dladybug.seed=123456 -jar ladybug.jar}.
 * Sans cette option, la graine est tirée au hasard, comme avant.
 */
public final class GameRandom {
	/** Propriété système qui impose la graine. */
	public static final String SEED_PROPERTY = "ladybug.seed";

	private static final Logger logger = LoggerFactory.getLogger(GameRandom.class);
	/**
	 * Source des graines tirées au hasard : une seule, créée une fois (règle Java
	 * S2119). Déclarée avant le bloc {@code static} qui s'en sert.
	 */
	private static final SecureRandom SEED_SOURCE = new SecureRandom();

	private static long seed;
	private static Random random;

	static {
		reseed(initialSeed());
	}

	/**
	 * Repart d'une graine donnée. Utilisé par les tests pour obtenir des tirages
	 * reproductibles.
	 */
	public static synchronized void reseed(long newSeed) {
		seed = newSeed;
		random = new Random(newSeed); // NOSONAR (S2245) : hasard du jeu, pas de la sécurité ; doit être rejouable à graine fixe
		logger.info("Random seed {} (to replay: -D{}={})", newSeed, SEED_PROPERTY, newSeed);
	}

	public static synchronized long getSeed() {
		return seed;
	}

	/** Entier tiré entre 0 (inclus) et {@code bound} (exclu). */
	public static synchronized int nextInt(int bound) {
		return random.nextInt(bound);
	}

	/** Réel tiré entre 0 (inclus) et 1 (exclu). */
	public static synchronized double nextDouble() {
		return random.nextDouble();
	}

	/** Valeur tirée au hasard parmi celles d'une énumération. */
	public static <T extends Enum<?>> T randomEnum(Class<T> enumClass) {
		var values = enumClass.getEnumConstants();
		return values[nextInt(values.length)];
	}

	/** Graine de la propriété {@value #SEED_PROPERTY}, sinon tirée au hasard. */
	private static long initialSeed() {
		var property = System.getProperty(SEED_PROPERTY);
		if (property != null && !property.isBlank()) {
			try {
				return Long.parseLong(property.trim());
			} catch (NumberFormatException e) {
				logger.warn("Invalid {} '{}', a random seed is used", SEED_PROPERTY, property);
			}
		}
		return SEED_SOURCE.nextLong();
	}

	private GameRandom() {
	}
}
