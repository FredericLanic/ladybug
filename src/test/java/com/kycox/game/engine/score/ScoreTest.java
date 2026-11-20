package com.kycox.game.engine.score;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kycox.game.body.GhostsGroup;
import com.kycox.game.body.ladybug.Ladybug;
import com.kycox.game.constant.fruits.Fruits;
import com.kycox.game.constant.game.GameMainConstants;

/**
 * Tests de {@link Score}.
 * <p>
 * Organisation de chaque test en trois temps (voir
 * {@code doc/patterns/test-unitaire.md}) :
 * <ol>
 * <li><b>Arrange</b> : on prépare la situation (ici avec des « mocks » Mockito
 * de {@link Ladybug} et de {@link GhostsGroup}) ;</li>
 * <li><b>Act</b> : on appelle la méthode testée ;</li>
 * <li><b>Assert</b> : on vérifie le résultat.</li>
 * </ol>
 */
class ScoreTest {
	private GhostsGroup ghostsGroup;
	private GroupMessages groupMessages;
	private Ladybug ladybug;
	private Score score;

	@BeforeEach
	void setUp() {
		// Fruits est une vraie instance : sa table de scores fait partie du test
		score = new Score(new Fruits());
		score.init();
		// Mocks : par défaut, toutes leurs méthodes « boolean » renvoient false
		// et getNbrEatenGhosts() renvoie 0
		ladybug = mock(Ladybug.class);
		ghostsGroup = mock(GhostsGroup.class);
		groupMessages = mock(GroupMessages.class);
	}

	@Test
	@DisplayName("Rien ne se passe : le score reste à zéro")
	void nothingHappens() {
		score.setScoreAndMessages(ghostsGroup, ladybug, groupMessages);

		assertThat(score.getScore()).isZero();
	}

	@Test
	@DisplayName("Une gomme rapporte 1 point")
	void simplePoint() {
		// Arrange
		when(ladybug.isEatenAPoint()).thenReturn(true);
		// Act
		score.setScoreAndMessages(ghostsGroup, ladybug, groupMessages);
		// Assert
		assertThat(score.getScore()).isEqualTo(GameMainConstants.SCORE_SIMPLE_POINT);
	}

	@Test
	@DisplayName("Une méga-gomme rapporte 10 points")
	void megaPoint() {
		when(ladybug.isEatenAMegaPoint()).thenReturn(true);

		score.setScoreAndMessages(ghostsGroup, ladybug, groupMessages);

		assertThat(score.getScore()).isEqualTo(GameMainConstants.SCORE_MEGA_POINT);
	}

	@Test
	@DisplayName("Une téléportation rapporte 15 points")
	void teleport() {
		when(ladybug.isToBeTeleported()).thenReturn(true);

		score.setScoreAndMessages(ghostsGroup, ladybug, groupMessages);

		assertThat(score.getScore()).isEqualTo(GameMainConstants.SCORE_TELEPORT_POINT);
	}

	@Test
	@DisplayName("Une cerise (fruit n° 1) rapporte 5 points")
	void fruit() {
		when(ladybug.hasEatenAFruit()).thenReturn(true);
		when(ladybug.getEatenAIdRefFruit()).thenReturn(1);

		score.setScoreAndMessages(ghostsGroup, ladybug, groupMessages);

		assertThat(score.getScore()).isEqualTo(5);
	}

	@Test
	@DisplayName("Un fantôme mangé rapporte 15 points, comme l'affiche le message")
	void eatenGhost() {
		when(ghostsGroup.getNbrEatenGhosts()).thenReturn(1);

		score.setScoreAndMessages(ghostsGroup, ladybug, groupMessages);

		assertThat(score.getScore()).isEqualTo(GameMainConstants.SCORE_EATEN_GHOST);
	}

	@Test
	@DisplayName("Le compteur de vie supplémentaire suit le score et repart à zéro sur demande")
	void incrementScoreForNewLife() {
		score.addScore(150);
		score.addScore(60);
		assertThat(score.getIncrementScore()).isEqualTo(210)
		        .isGreaterThanOrEqualTo(score.getNbrPointsForNewLife());

		score.initIncrementScore();

		assertThat(score.getIncrementScore()).isZero();
		assertThat(score.getScore()).isEqualTo(210);
	}
}
