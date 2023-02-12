package com.kycox.game.message;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class GameMessaging {
	private List<String> messages;

	public String get() {
		var value = "";
		if (!messages.isEmpty()) {
			value = messages.get(0);
			messages.remove(0);
		}
		return value;
	}

	@PostConstruct
	public void init() {
		messages = Collections.synchronizedList(new ArrayList<>());
	}

	public void put(String message) {
		messages.add(message);
	}
}