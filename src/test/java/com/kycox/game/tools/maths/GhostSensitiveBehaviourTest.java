package com.kycox.game.tools.maths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kycox.game.constant.game.GameMainConstants;

/**
 * Tests de {@link GhostSensitiveBehaviour} : probabilité qu'un fantôme suive son
 * comportement (SMART, AGGRESSIVE…) plutôt que d'errer.
 * <p>
 * Le tirage est aléatoire ({@code Utils.generateRandomInt}, statique) : on le
 * vérifie sur un grand nombre de tirages. Avec 20 000 tirages, l'écart toléré
 * (3 points) représente plus de dix écarts-types : le test ne peut pas échouer
 * par malchance.
 */
class GhostSensitiveBehaviourTest {
	private static final int DRAWS = 20_000;

	private static double activeRate(GhostSensitiveBehaviour behaviour) {
		return IntStream.range(0, DRAWS).filter(i -> behaviour.isActive()).count() / (double) DRAWS;
	}

	@Test
	@DisplayName("Sans niveau, le fantôme n'est jamais « sensible »")
	void neverActiveBeforeLevelIsSet() {
		assertThat(activeRate(new GhostSensitiveBehaviour())).isZero();
	}

	@Test
	@DisplayName("Niveau 1 : environ 19 % (tirage de 1 à 100 strictement sous 20)")
	void about19PercentAtFirstLevel() {
		var behaviour = new GhostSensitiveBehaviour();
		behaviour.setNumLevel(1);
		assertThat(activeRate(behaviour)).isCloseTo(0.19, within(0.03));
	}

	@Test
	@DisplayName("Dernier niveau : environ 89 % (tirage de 1 à 100 strictement sous 90)")
	void about89PercentAtLastLevel() {
		var behaviour = new GhostSensitiveBehaviour();
		behaviour.setNumLevel(GameMainConstants.NBR_LEVELS);
		assertThat(activeRate(behaviour)).isCloseTo(0.89, within(0.03));
	}
}
