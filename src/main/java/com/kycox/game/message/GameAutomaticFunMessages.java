package com.kycox.game.message;

import lombok.Getter;

@Getter
public enum GameAutomaticFunMessages {
	MSG1("hey buddy, have a good game"), MSG2("listen kid, do your home work first"),
	MSG3("hey, open your eyes, wake up !!"), MSG4("listen to your mummy, brush your teeth"),
	MSG5("stop playing, go to school now"), MSG6("don't worry, you will lose"),
	MSG7("try tomorrow, you will do better"), MSG8("do or no do, there is no try");

	private final String message;

	GameAutomaticFunMessages(String message) {
		this.message = message;
	}
}
