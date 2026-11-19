package com.kycox.game.testutil;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Consumer;

import com.kycox.game.constant.contract.GameModelForController;
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
	public static final Consumer<GameModelForController> LEFT = keys -> keys.setLadybugRequest(GameMainConstants.POINT_LEFT);
	public static final Consumer<GameModelForController> RIGHT = keys -> keys.setLadybugRequest(GameMainConstants.POINT_RIGHT);
	public static final Consumer<GameModelForController> UP = keys -> keys.setLadybugRequest(GameMainConstants.POINT_UP);
	public static final Consumer<GameModelForController> DOWN = keys -> keys.setLadybugRequest(GameMainConstants.POINT_DOWN);

	/** Battement où l'on appuie sur S (l'écran de présentation est apparu au 64e). */
	static final int START_BEAT = 70;

	private final TreeMap<Integer, List<Consumer<GameModelForController>>> keysByBeat = new TreeMap<>();

	/** Partie solo au niveau 1 : S puis N sur l'écran de présentation. */
	public static GameScript soloLevelOne() {
		return new GameScript()
		        .at(START_BEAT, GameModelForController::startGame)
		        .at(START_BEAT + 1, keys -> keys.initializeLevelNumAndStartGame(true));
	}

	/** Ajoute une « touche » au battement donné. */
	public GameScript at(int beat, Consumer<GameModelForController> key) {
		keysByBeat.computeIfAbsent(beat, b -> new ArrayList<>()).add(key);
		return this;
	}

	/** Joue la partie jusqu'au battement {@code totalBeats}, touches comprises. */
	public void play(GameDriver game, int totalBeats) {
		while (game.beatCount() < totalBeats) {
			keysByBeat.getOrDefault(game.beatCount(), List.of()).forEach(key -> key.accept(game.keys()));
			game.beat();
		}
	}
}
