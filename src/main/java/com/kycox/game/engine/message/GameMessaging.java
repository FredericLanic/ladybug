package com.kycox.game.engine.message;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class GameMessaging {

	private List<String> messages;
	private final LadybugMessagesBundle ladybugMessagesBundle;

	public GameMessaging(LadybugMessagesBundle ladybugMessagesBundle) {
		this.ladybugMessagesBundle = ladybugMessagesBundle;
		messages = Collections.synchronizedList(new ArrayList<>());
	}

	public String get() {
        return messages.stream().findFirst().map(first -> {
			messages.remove(0);
			return first;
		}).orElse("");
	}

	public void addMessage(String key) {
		messages.add(ladybugMessagesBundle.getMessage(key));
	}

	public void addGameInPause(boolean gameInPause) {
		addMessage(gameInPause ? "msg.gameInPause" : "msg.heyWeGo");
	}

	public void addPlayerMode(boolean isMultiPlayer) {
		addMessage(isMultiPlayer ? "msg.twoPlayersMode" : "msg.onePlayerMode");
	}

	public void addSoundMode(boolean isActiveSound) {
		addMessage(isActiveSound ? "msg.soundOn" : "msg.soundOff");
	}

	public void addIamLadybug() {
		addMessage("msg.iAmLadybug");
	}

	public void addIamGhost() {
		addMessage("msg.iAmGhost");
	}

	public void addXboxLadybugConnected() {
		addMessage("msg.xboxLadybugConnection");
	}

	public void addXboxLadybugDisconnected() {
		addMessage("msg.xboxLadybugDisconnection");
	}

	public void addXboxGhostConnected() {
		addMessage("msg.xboxGhostConnection");
	}

	public void addXboxGhostDisconnected() {
		addMessage("msg.xboxGhostDisconnection");
	}
}