package com.kycox.game.engine.score;

import lombok.Getter;

@Getter
public enum MessageType {
	POINT(" pt"), STRING("");

	private String endMessage;

	MessageType(String endMessage) {
		this.endMessage = endMessage;
	}
}
