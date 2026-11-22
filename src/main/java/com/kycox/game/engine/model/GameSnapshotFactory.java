/**
  Copyright (C) 2021  Frédéric Lanic frederic.lanic@outlook.fr

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.kycox.game.engine.model;

import com.kycox.game.body.ghost.Ghost;
import com.kycox.game.body.ladybug.Ladybug;
import com.kycox.game.constant.sounds.Sounds;
import com.kycox.game.engine.maze.Maze;
import com.kycox.game.engine.maze.ScreenBlock;
import com.kycox.game.snapshot.CellSnapshot;
import com.kycox.game.snapshot.GameSnapshot;
import com.kycox.game.snapshot.GhostSnapshot;
import com.kycox.game.snapshot.LadybugSnapshot;
import com.kycox.game.snapshot.LobbySnapshot;
import com.kycox.game.snapshot.MazeSnapshot;
import com.kycox.game.snapshot.ScoreMessageSnapshot;

import java.util.Arrays;

/**
 * Prend la photo de la partie ({@link GameSnapshot}) à partir du modèle vivant.
 * C'est le seul endroit qui traduit le modèle en snapshot : quand le modèle
 * change, seule cette classe est à revoir (étape 4.6).
 */
public final class GameSnapshotFactory {

	private GameSnapshotFactory() {
	}

	public static GameSnapshot of(GameModel model) {
		var status = model.getCurrentProgramStatus();
		var levelState = model.getLevelState();
		return new GameSnapshot(status.getGameStatus(), status.getNumLevel(), model.getGameScore().getScore(),
		        model.getNbrPlayers(), model.getGhostLeftLives(),
		        ladybugOf(model.getLadybug(), model.getLadybugDying().getAgonyDuration()),
		        model.getGroupGhosts().getGhosts().stream().map(GameSnapshotFactory::ghostOf).toList(),
		        mazeOf(model.getMaze()), levelState.isLitLampMode(), levelState.getLevelRendering(),
		        model.getGroupMessages().getMessages().stream()
		                .map(m -> new ScoreMessageSnapshot(m.getPosition(), m.getValue(), m.getMessageType()))
		                .toList(),
		        Arrays.stream(Sounds.values()).filter(model.getNewSounds()::hasSound).toList(),
		        lobbyOf(model.getLobby()));
	}

	/** La photo du salon (20.2) : l'appareil par son nom, pour ne pas lier la photo aux entrées. */
	static LobbySnapshot lobbyOf(Lobby lobby) {
		return new LobbySnapshot(lobby.maxSeats(), lobby.seats().stream()
		        .map(seat -> new LobbySnapshot.SeatSnapshot(seat.device().name(), seat.ghost(), seat.confirmed()))
		        .toList());
	}

	static LadybugSnapshot ladybugOf(Ladybug ladybug, long agonyDuration) {
		return new LadybugSnapshot(ladybug.getName(), ladybug.getPosition(), ladybug.getPositionBlock(),
		        ladybug.getViewDirection(), ladybug.getStatus(), ladybug.getLeftLifes(), ladybug.isNewLife(),
		        ladybug.getSpeedIndex(), ladybug.isEatenAMegaPoint(), ladybug.isToBeTeleported(), agonyDuration);
	}

	static GhostSnapshot ghostOf(Ghost ghost) {
		return new GhostSnapshot(ghost.getName(), ghost.getColor(), ghost.getPosition(), ghost.getPositionBlock(),
		        ghost.getDirection(), ghost.getStatus(), ghost.isComputed(), ghost.isCamouflage(), ghost.isDying(),
		        ghost.isToBeTeleported(), ghost.getSpeedIndex(), ghost.getMoveStyle(), ghost.getMaximumAttackDist(),
		        ghost.getTarget());
	}

	/** La photo du labyrinthe seul (sert aussi aux tests du contour des murs). */
	public static MazeSnapshot mazeOf(Maze maze) {
		if (maze.getCurrentLevel() == null) {
			// avant le tout premier niveau préparé
			return new MazeSnapshot(maze.getLayoutVersion(), 0, 0, java.util.List.of());
		}
		return new MazeSnapshot(maze.getLayoutVersion(), maze.getCurrentLevel().getNbrBlocksByLine(),
		        maze.getNbrLines(), maze.getScreenBlocks().stream().map(GameSnapshotFactory::cellOf).toList());
	}

	static CellSnapshot cellOf(ScreenBlock block) {
		return new CellSnapshot(block.getCoordinate(), block.isBorderLeft(), block.isBorderUp(),
		        block.isBorderRight(), block.isBorderDown(), block.isNotAccessible(), block.isPoint(),
		        block.isMegaPoint(), block.isGhostReviver(), block.isTeleportation(), block.getIdRefFruit());
	}
}
