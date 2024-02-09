package com.kycox.game.engine.message;

import lombok.Getter;

@Getter
public enum GameFunMessages {
	MSG1("msg.fun.1"), MSG2("msg.fun.2"),
	MSG3("msg.fun.3"), MSG4("msg.fun.4"),
	MSG5("msg.fun.5"), MSG6("msg.fun.6"),
	MSG7("msg.fun.7"), MSG8("msg.fun.8");

	private final String key;

	GameFunMessages(String key) {
		this.key = key;
	}
}
