package com.kycox.game.body;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.kycox.game.body.ladybug.Ladybug;
import com.kycox.game.constant.contract.LevelStructure;
import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.engine.maze.Maze;
import com.kycox.game.engine.maze.ManageLevel;
import com.kycox.game.testutil.LevelFixtures;
import com.kycox.game.tools.Position;
import com.kycox.game.tools.Utils;

/**
 * Dette D32 (22/11/2026), trouvée par la démo du bot laissée une nuit : un
 * demi-tour demandé pile au centre d'un téléporteur faisait repartir Ladybug du
 * téléporteur d'arrivée dans la nouvelle direction, parfois vers l'extérieur :
 * elle sortait du labyrinthe et n'y revenait plus.
 *
 * <p>Pour chaque téléporteur de chaque niveau et chacune des quatre directions,
 * après la téléportation, le personnage est soit pile sur le téléporteur
 * d'arrivée, soit déjà en route vers une case du labyrinthe.
 */
class TeleportTest {
	private static final List<Position> DIRECTIONS = List.of(GameMainConstants.POINT_UP,
	        GameMainConstants.POINT_DOWN, GameMainConstants.POINT_LEFT, GameMainConstants.POINT_RIGHT);

	private static Position opposite(Position direction) {
		return new Position(-direction.x(), -direction.y());
	}

	private static boolean enters(Maze maze, Position from, Position direction) {
		var block = maze.getScreenBlock(from.translate(direction.x(), direction.y()));
		return block != null && !block.isNotAccessible();
	}

	static List<LevelStructure> levels() {
		return LevelFixtures.activeLevels();
	}

	@ParameterizedTest(name = "{index}")
	@MethodSource("levels")
	void aTeleportedBodyNeverLeavesTheMaze(LevelStructure level) {
		// comme en partie : les téléporteurs ne sont posés qu'en jeu
		var maze = new Maze(new ManageLevel(List.of(level)));
		maze.setLevelMap(1, true);
		level.getTeleportPoints().forEach((from, to) -> {
			for (var direction : DIRECTIONS) {
				var ladybug = new Ladybug();
				ladybug.setPosition(Utils.convertBlockPointToGraphicPoint(from));
				ladybug.setDirection(direction);

				ladybug.teleport(maze);

				var arrival = Utils.convertBlockPointToGraphicPoint(to);
				if (ladybug.getPosition().equals(arrival)) {
					// immobile sur l'arrivée : seulement si aucun des deux côtés n'entre dans le
					// labyrinthe ; sinon, ping-pong sans fin entre deux téléporteurs (22/11/2026)
					assertThat(enters(maze, to, direction) || enters(maze, to, opposite(direction)))
					        .as("%s : de %s vers %s, direction %s, Ladybug reste sur l'arrivée alors qu'elle peut entrer",
					                LevelFixtures.nameOf(level), from, to, direction)
					        .isFalse();
				} else {
					// la direction réelle : Ladybug a pu faire demi-tour pour entrer
					var heading = ladybug.getDirection();
					var next = to.translate(heading.x(), heading.y());
					var nextBlock = maze.getScreenBlock(next);
					assertThat(nextBlock)
					        .as("%s : de %s vers %s, direction %s, Ladybug part vers %s, hors du labyrinthe",
					                LevelFixtures.nameOf(level), from, to, heading, next)
					        .isNotNull();
					assertThat(nextBlock.isNotAccessible()).as("case %s inaccessible", next).isFalse();
				}
			}
		});
	}
}
