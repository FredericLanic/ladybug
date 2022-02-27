package com.kycox.game.message;

import lombok.Getter;

public enum GameMessages {
	GAME_IN_PAUSE("game in pause"), HEY_WE_GO("here we go"), ONE_PLAYER_MODE("single player mode"),
	SOUND_ACTIVE("sound activated"), SOUND_OFF("sound off"), TWO_PLAYERS_MODE("two player mode"),
	XBOX_GHOST_CONNECTION("xboxone ghost connected"), XBOX_GHOST_DISCONNECTION("xboxone ghost disconnected"),
	XBOX_LADYBUG_CONNECTION("xboxone ladybug connected"), XBOX_LADYBUG_DISCONNECTION("xboxone ladybug disconnected");

	@Getter
	private String message;

	GameMessages(String message) {
		this.message = message;
	}
}
