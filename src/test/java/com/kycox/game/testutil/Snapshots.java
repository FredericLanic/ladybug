package com.kycox.game.testutil;

import java.util.List;

import com.kycox.game.body.ghost.Ghost.MoveStyle;
import com.kycox.game.body.ghost.GhostColor;
import com.kycox.game.constant.game.GameStatus;
import com.kycox.game.constant.ghost.GhostStatus;
import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.constant.level.LevelRendering;
import com.kycox.game.snapshot.GameSnapshot;
import com.kycox.game.snapshot.GhostSnapshot;
import com.kycox.game.snapshot.LadybugSnapshot;
import com.kycox.game.snapshot.MazeSnapshot;
import com.kycox.game.tools.Position;

/**
 * Petites photos de partie fabriquées à la main, pour les tests qui n'ont pas
 * besoin de faire tourner le jeu (étape 4.6).
 */
public final class Snapshots {
	private static final Position ORIGIN = new Position(0, 0);

	private Snapshots() {
	}

	/** Une partie en cours, avec cette Ladybug et ces fantômes ; labyrinthe vide. */
	public static GameSnapshot inGame(LadybugSnapshot ladybug, List<GhostSnapshot> ghosts) {
		return new GameSnapshot(GameStatus.IN_GAME, 1, 0, ghosts.stream().anyMatch(g -> !g.computed()) ? 2 : 1, 3,
		        ladybug, ghosts, new MazeSnapshot(0, 0, 0, List.of()), false, LevelRendering.getDefaultLevelRendering(),
		        List.of(), List.of());
	}

	/** Une Ladybug ordinaire, sauf pour la méga-gomme et la téléportation. */
	public static LadybugSnapshot ladybug(boolean eatenAMegaPoint, boolean toBeTeleported) {
		return new LadybugSnapshot("Ladybug", ORIGIN, ORIGIN, ORIGIN, LadybugStatus.NORMAL, 3, false, 0,
		        eatenAMegaPoint, toBeTeleported, 0);
	}

	/** Une Ladybug ordinaire, au battement où elle gagne (ou non) une vie. */
	public static LadybugSnapshot ladybugWithNewLife(boolean newLife) {
		return new LadybugSnapshot("Ladybug", ORIGIN, ORIGIN, ORIGIN, LadybugStatus.NORMAL, 3, newLife, 0, false,
		        false, 0);
	}

	/** Un fantôme joué par un humain, sauf pour la téléportation et la mort. */
	public static GhostSnapshot humanGhost(boolean toBeTeleported, boolean dying) {
		return new GhostSnapshot("Blinky", GhostColor.RED, ORIGIN, ORIGIN, ORIGIN, GhostStatus.NORMAL, false, false,
		        dying, toBeTeleported, 0, MoveStyle.MOVE_DEFAULT, 0, null);
	}
}
