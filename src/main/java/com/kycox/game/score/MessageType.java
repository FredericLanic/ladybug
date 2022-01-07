package com.kycox.game.score;

import lombok.Getter;

public enum MessageType {
	POINT(" pt"), STRING("");

	@Getter
	private String endMessage;

	MessageType(String endMessage) {
		this.endMessage = endMessage;
	}
}
