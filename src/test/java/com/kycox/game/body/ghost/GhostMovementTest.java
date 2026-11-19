package com.kycox.game.body.ghost;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Point;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kycox.game.body.ladybug.Ladybug;
import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.constant.ghost.GhostStatus;
import com.kycox.game.constant.level.data.SecondGenerationLevel1;
import com.kycox.game.engine.screendata.ScreenData;
import com.kycox.game.testutil.LevelFixtures;
import com.kycox.game.tools.Utils;
import com.kycox.game.tools.dijkstra.Dijkstra;

/**
 * Comportement commun des fantômes calculés par l'ordinateur
 * ({@link Ghost#moveComputedGhost}).
 * <p>
 * Blinky, Pinky, Inky et Clyde ne visent pas chacun une case différente : ils
 * poursuivent tous Ladybug elle-même et ne diffèrent que par leur vitesse. On
 * teste donc ce comportement commun avec un seul d'entre eux (Pinky).
 * <p>
 * Niveau utilisé : {@link SecondGenerationLevel1}. Ladybug est un mock immobile.
 */
class GhostMovementTest {
	private static final int BLOCK = GameMainConstants.BLOCK_SIZE;
	/** Index 3 de VALID_SPEEDS : 4 pixels par battement, donc 12 battements par case. */
	private static final int SPEED_INDEX = 3;

	private SecondGenerationLevel1 level;
	private ScreenData screenData;
	private Ladybug ladybug;
	private Point ladybugBlock;

	@BeforeEach
	void setUp() {
		level = new SecondGenerationLevel1();
		screenData = LevelFixtures.screenDataOf(level);
		ladybugBlock = level.getInitLadybugBlockPos();
		ladybug = ladybugAt(ladybugBlock);
	}

	private static Ladybug ladybugAt(Point block) {
		var mockLadybug = mock(Ladybug.class);
		when(mockLadybug.getPositionBlock()).thenReturn(block);
		when(mockLadybug.getPosition()).thenReturn(Utils.convertBlockPointToGraphicPoint(block));
		when(mockLadybug.isAllowedToDoActions()).thenReturn(true);
		return mockLadybug;
	}

	private static Ghost ghostAt(Point block, GhostStatus status) {
		var ghost = new Pinky();
		// toujours un nouveau Point : move() modifie la position sur place
		ghost.setPosition(Utils.convertBlockPointToGraphicPoint(block));
		ghost.setStatus(status);
		ghost.setSpeedIndex(SPEED_INDEX);
		// Ladybug toujours « à portée d'attaque » : pas de tirage aléatoire
		ghost.setMaximumAttackDist(Integer.MAX_VALUE);
		return ghost;
	}

	private static Point neighbourInDirection(Point block, Point direction) {
		return new Point(block.x + direction.x, block.y + direction.y);
	}

	@Test
	@DisplayName("En chasse, depuis n'importe quelle case, le premier pas rapproche le fantôme de Ladybug")
	void chasingStepGetsCloser() {
		var distancesToLadybug = LevelFixtures.distancesFrom(ladybugBlock, screenData);

		distancesToLadybug.forEach((start, distance) -> {
			if (distance == 0) {
				return;
			}
			var ghost = ghostAt(start, GhostStatus.NORMAL);

			ghost.moveComputedGhost(ladybug, screenData);

			var next = neighbourInDirection(start, ghost.getDirection());
			assertThat(distancesToLadybug.get(next)).as("depuis %s", start).isEqualTo(distance - 1);
			assertThat(ghost.getPosition()).as("déplacement depuis %s", start).isEqualTo(new Point(
			        start.x * BLOCK + ghost.getDirection().x * ghost.getSpeed(),
			        start.y * BLOCK + ghost.getDirection().y * ghost.getSpeed()));
		});
	}

	@Test
	@DisplayName("En chasse, le fantôme rejoint une Ladybug immobile par le plus court chemin")
	void chasingGhostCatchesStillLadybug() {
		var start = level.getGhostRegenerateBlockPoint();
		var distance = LevelFixtures.distancesFrom(ladybugBlock, screenData).get(start);
		var ghost = ghostAt(start, GhostStatus.NORMAL);
		var beatsPerBlock = BLOCK / ghost.getSpeed();

		for (var beat = 0; beat < distance * beatsPerBlock; beat++) {
			ghost.moveComputedGhost(ladybug, screenData);
		}

		assertThat(ghost.getPosition()).isEqualTo(Utils.convertBlockPointToGraphicPoint(ladybugBlock));
	}

	@Test
	@DisplayName("Effrayé et proche de Ladybug, le fantôme part à l'opposé du chemin qui mène à elle")
	void scaredGhostRunsAway() {
		var checkedCases = 0;
		for (var entry : LevelFixtures.distancesFrom(ladybugBlock, screenData).entrySet()) {
			var start = entry.getKey();
			var closeEnough = Utils.convertBlockPointToGraphicPoint(ladybugBlock)
			        .distance(Utils.convertBlockPointToGraphicPoint(start)) < 5.0 * BLOCK;
			if (entry.getValue() == 0 || !closeEnough) {
				continue;
			}
			var path = Dijkstra.getShorterWay(start, ladybugBlock, screenData);
			var awayDirection = new Point(path.get(0).x - path.get(1).x, path.get(0).y - path.get(1).y);
			var awayBlock = neighbourInDirection(start, awayDirection);
			if (!LevelFixtures.openNeighbours(start, screenData).contains(awayBlock)) {
				// mur derrière le fantôme : il erre au hasard, rien à vérifier ici
				continue;
			}
			var ghost = ghostAt(start, GhostStatus.SCARED);

			ghost.moveComputedGhost(ladybug, screenData);

			assertThat(ghost.getDirection()).as("fuite depuis %s", start).isEqualTo(awayDirection);
			checkedCases++;
		}
		assertThat(checkedCases).as("nombre de situations de fuite vérifiées").isPositive();
	}

	@Test
	@DisplayName("Mangé, le fantôme rejoint le point de régénération puis passe en AUTOGENERATED")
	void eatenGhostGoesBackToRegenerationPoint() {
		var regeneration = level.getGhostRegenerateBlockPoint();
		var distance = LevelFixtures.distancesFrom(regeneration, screenData).get(ladybugBlock);
		var ghost = ghostAt(ladybugBlock, GhostStatus.DYING);
		var beatsPerBlock = BLOCK / ghost.getSpeed();

		// un battement de plus : arrivé sur la case, il constate qu'il est arrivé
		for (var beat = 0; beat <= distance * beatsPerBlock && ghost.getStatus() == GhostStatus.DYING; beat++) {
			ghost.moveComputedGhost(ladybug, screenData);
		}

		assertThat(ghost.getStatus()).isEqualTo(GhostStatus.AUTOGENERATED);
		assertThat(ghost.getPositionBlock()).isEqualTo(regeneration);
	}
}
