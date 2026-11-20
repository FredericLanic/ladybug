package com.kycox.game.body.ghost;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kycox.game.body.ladybug.Ladybug;
import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.constant.ghost.GhostStatus;
import com.kycox.game.constant.level.data.SecondGenerationLevel1;
import com.kycox.game.engine.maze.Maze;
import com.kycox.game.testutil.LevelFixtures;
import com.kycox.game.tools.Position;
import com.kycox.game.tools.Utils;

/**
 * Destination d'un fantôme ({@link Ghost#getTarget()}), affichée en mode debug :
 * la case visée quand il en vise une, rien quand il fuit ou est joué par un
 * humain. Elle ne change rien au déplacement (golden master identique).
 */
class GhostTargetTest {
	private SecondGenerationLevel1 level;
	private Maze maze;
	private Ladybug ladybug;
	private Position ladybugBlock;

	@BeforeEach
	void setUp() {
		level = new SecondGenerationLevel1();
		maze = LevelFixtures.mazeOf(level);
		ladybugBlock = level.getInitLadybugBlockPos();
		ladybug = mock(Ladybug.class);
		when(ladybug.getPositionBlock()).thenReturn(ladybugBlock);
		when(ladybug.getPosition()).thenReturn(Utils.convertBlockPointToGraphicPoint(ladybugBlock));
		when(ladybug.isAllowedToDoActions()).thenReturn(true);
	}

	private Ghost ghostNextToLadybug(GhostStatus status) {
		var path = LevelFixtures.openNeighbours(ladybugBlock, maze);
		var ghost = new Pinky();
		ghost.setPosition(Utils.convertBlockPointToGraphicPoint(path.get(0)));
		ghost.setStatus(status);
		ghost.setSpeedIndex(3);
		ghost.setMaximumAttackDist(Integer.MAX_VALUE);
		return ghost;
	}

	@Test
	@DisplayName("en attaque, le fantôme vise la case de Ladybug")
	void attackingGhostTargetsLadybug() {
		var ghost = ghostNextToLadybug(GhostStatus.NORMAL);
		ghost.moveComputedGhost(ladybug, maze);
		assertThat(ghost.getTarget()).isEqualTo(ladybugBlock);
	}

	@Test
	@DisplayName("mangé, le fantôme vise le point de résurrection")
	void eatenGhostTargetsTheRevivalPoint() {
		var ghost = ghostNextToLadybug(GhostStatus.DYING);
		ghost.moveComputedGhost(ladybug, maze);
		assertThat(ghost.getTarget()).isEqualTo(level.getGhostRegenerateBlockPoint());
	}

	@Test
	@DisplayName("effrayé, le fantôme fuit : pas de destination")
	void scaredGhostHasNoTarget() {
		var ghost = ghostNextToLadybug(GhostStatus.NORMAL);
		ghost.moveComputedGhost(ladybug, maze);
		ghost.setStatus(GhostStatus.SCARED);
		ghost.moveComputedGhost(ladybug, maze);
		assertThat(ghost.getTarget()).isNull();
	}

	@Test
	@DisplayName("joué par un humain, le fantôme n'a pas de destination")
	void humanGhostHasNoTarget() {
		var ghost = ghostNextToLadybug(GhostStatus.NORMAL);
		ghost.moveComputedGhost(ladybug, maze);
		ghost.moveGhostUser(ladybug, maze, GameMainConstants.POINT_UP);
		assertThat(ghost.getTarget()).isNull();
	}
}
