package com.kycox.ladybug.contract;

import java.awt.Point;

import com.kycox.ladybug.body.ladybug.Ladybug;

public interface IGameModelForControleur extends IGameModelForExternalElements {
	public void forceStopGame();

	public void gameInPause();

	public Ladybug getLadybug();

	public void setGhostRequest(Point point);

	public void startGame();

	public void startStopSoundActive();
}
