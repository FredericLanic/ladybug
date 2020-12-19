package com.kycox.game.contract;

import java.awt.Point;

import com.kycox.game.body.ladybug.Ladybug;

public interface IGameModelForControleur extends IGameModelForExternalElements {
	public void forceStopGame();

	public void gameInPause();

	public Ladybug getLadybug();

	public void setGhostRequest(Point point);

	public void startGame();

	public void startStopSoundActive();
}
