package com.kycox.game.message;

import lombok.Getter;

public enum GameMessages {
	GAME_IN_PAUSE("game in pause"), HEY_WE_GO("here we go"), I_AM_GHOST("i am ghost"), I_AM_LADYBUG("i am ladybug"),
	ONE_PLAYER_MODE("single player mode"), SOUND_ACTIVE("sound on"), SOUND_OFF("sound off"),
	TWO_PLAYERS_MODE("two players mode"), XBOX_GHOST_CONNECTION("xboxone ghost connected"),
	XBOX_GHOST_DISCONNECTION("xboxone ghost disconnected"), XBOX_LADYBUG_CONNECTION("xboxone ladybug connected"),
	XBOX_LADYBUG_DISCONNECTION("xboxone ladybug disconnected"), INITIALIZE_LEVEL_NUMBER("initialize level number");

	@Getter
	private final String message;

	GameMessages(String message) {
		this.message = message;
	}
}
