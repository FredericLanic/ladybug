package com.kycox.game.tools.maths;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.kycox.game.constant.game.GameMainConstants;

/**
 * Tests de {@link SpeedFunction} : l'index de vitesse croît linéairement avec le
 * niveau, de {@code lowLimit} au niveau 1 jusqu'à la taille de
 * {@link GameMainConstants#VALID_SPEEDS} au dernier niveau.
 * <p>
 * Réglage identique à celui des fantômes dans {@code application-context.xml}
 * ({@code lowLimit = 2}).
 */
class SpeedFunctionTest {
	private SpeedFunction speedFunction;

	@BeforeEach
	void setUp() {
		// Arrange commun : la fonction telle que Spring la construit
		speedFunction = new SpeedFunction();
		speedFunction.setLowLimit(2);
		speedFunction.init();
	}

	@ParameterizedTest(name = "niveau {0} -> index {1}")
	@CsvSource({ "1, 2", "6, 2", "7, 3", "13, 4", "19, 5", "25, 6", "29, 6" })
	@DisplayName("L'index de vitesse augmente d'un cran tous les six niveaux environ")
	void indexGrowsWithLevel(int numLevel, int expectedIndex) {
		assertThat(speedFunction.getRealIndexSpeed(numLevel)).isEqualTo(expectedIndex);
	}

	@Test
	@DisplayName("Au dernier niveau, l'index sort de la liste des vitesses (Body.getSpeed le ramène à la dernière)")
	void lastLevelIndexIsOutOfValidSpeeds() {
		// Comportement actuel documenté : 7 alors que VALID_SPEEDS va de 0 à 6.
		// Body.getSpeed() borne l'index, le jeu ne plante donc pas.
		assertThat(speedFunction.getRealIndexSpeed(GameMainConstants.NBR_LEVELS))
		        .isEqualTo(GameMainConstants.VALID_SPEEDS.size());
	}

	@Test
	@DisplayName("Minus retire un cran, sans descendre sous zéro")
	void minusRemovesOneStepButNotBelowZero() {
		assertThat(speedFunction.getRealIndexSpeedMinus(1)).isEqualTo(1);

		var slowFunction = new SpeedFunction();
		slowFunction.setLowLimit(0);
		slowFunction.init();
		assertThat(slowFunction.getRealIndexSpeedMinus(1)).isZero();
	}

	@Test
	@DisplayName("Plus ajoute un cran, sans dépasser le dernier index valide")
	void plusAddsOneStepButNotAboveLastValidIndex() {
		assertThat(speedFunction.getRealIndexSpeedPlus(1)).isEqualTo(3);
		var lastValidIndex = GameMainConstants.VALID_SPEEDS.size() - 1;
		assertThat(speedFunction.getRealIndexSpeedPlus(28)).isEqualTo(lastValidIndex);
	}
}
