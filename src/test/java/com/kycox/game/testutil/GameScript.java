package com.kycox.game.testutil;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Consumer;

import com.kycox.game.constant.game.GameMainConstants;

/**
 * Scénario de partie : des « touches » à des battements précis (étape 3.5.2c).
 *
 * <pre>
 * var script = GameScript.soloLevelOne()      // S puis N, au bon moment
 *         .at(300, LEFT).at(420, UP).at(600, RIGHT);
 * script.play(game, 1500);
 * </pre>
 *
 * Rejoué avec la même graine, un scénario donne toujours la même partie.
 */
public final class GameScript {
	public static final Consumer<GameDriver> LEFT = game -> game.inputs().ladybug().request(GameMainConstants.POINT_LEFT);
	public static final Consumer<GameDriver> RIGHT = game -> game.inputs().ladybug().request(GameMainConstants.POINT_RIGHT);
	public static final Consumer<GameDriver> UP = game -> game.inputs().ladybug().request(GameMainConstants.POINT_UP);
	public static final Consumer<GameDriver> DOWN = game -> game.inputs().ladybug().request(GameMainConstants.POINT_DOWN);

	/** Battement où l'on appuie sur S (l'écran de présentation est apparu au 64e). */
	static final int START_BEAT = 70;

	private final TreeMap<Integer, List<Consumer<GameDriver>>> keysByBeat = new TreeMap<>();

	/** Partie solo au niveau 1 : S puis N sur l'écran de présentation. */
	public static GameScript soloLevelOne() {
		return new GameScript()
		        .at(START_BEAT, game -> game.keys().startGame())
		        .at(START_BEAT + 1, game -> game.keys().initializeLevelNumAndStartGame(true));
	}

	public static final Consumer<GameDriver> GHOST_LEFT = game -> game.inputs().humanGhost().orElseThrow().request(GameMainConstants.POINT_LEFT);
	public static final Consumer<GameDriver> GHOST_RIGHT = game -> game.inputs().humanGhost().orElseThrow().request(GameMainConstants.POINT_RIGHT);
	public static final Consumer<GameDriver> GHOST_UP = game -> game.inputs().humanGhost().orElseThrow().request(GameMainConstants.POINT_UP);
	public static final Consumer<GameDriver> GHOST_DOWN = game -> game.inputs().humanGhost().orElseThrow().request(GameMainConstants.POINT_DOWN);

	/** Partie à 2 joueurs au niveau 1 : touche 2, puis S et N. */
	public static GameScript twoPlayersLevelOne() {
		return new GameScript()
		        .at(START_BEAT - 2, game -> game.keys().setMultiPlayers(true))
		        .at(START_BEAT, game -> game.keys().startGame())
		        .at(START_BEAT + 1, game -> game.keys().initializeLevelNumAndStartGame(true));
	}

	/** Ajoute une « touche » au battement donné. */
	public GameScript at(int beat, Consumer<GameDriver> key) {
		keysByBeat.computeIfAbsent(beat, b -> new ArrayList<>()).add(key);
		return this;
	}

	/** Joue la partie jusqu'au battement {@code totalBeats}, touches comprises. */
	public void play(GameDriver game, int totalBeats) {
		while (game.beatCount() < totalBeats) {
			keysByBeat.getOrDefault(game.beatCount(), List.of()).forEach(key -> key.accept(game));
			game.beat();
		}
	}
}
