package com.kycox.game.tools.maths;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests de {@link BlinkySpeedIndex} : bonus de vitesse de Blinky selon le
 * pourcentage de gommes mangées. La parabole passe par (0, 0), (80, 1) et
 * (100, 3).
 */
class BlinkySpeedIndexTest {
	private BlinkySpeedIndex blinkySpeedIndex;

	@BeforeEach
	void setUp() {
		blinkySpeedIndex = new BlinkySpeedIndex();
		blinkySpeedIndex.setNumLevel();
	}

	@ParameterizedTest(name = "{0} % mangé -> +{1}")
	@CsvSource({ "0, 0", "50, 0", "79, 0", "80, 1", "81, 1", "99, 2", "100, 3" })
	@DisplayName("Blinky accélère à la fin du labyrinthe : +1 à 80 %, +3 à 100 %")
	void bonusFollowsTheCurve(int perCent, int expectedBonus) {
		assertThat(blinkySpeedIndex.getIncrementSpeedIndex(perCent)).isEqualTo(expectedBonus);
	}
}
