package com.kycox.game.contract;

import java.awt.Point;

public interface IGameModelForController extends IGameCommon {
	public void forceStopGame();

	public void gameInPause();

	public IGameStatusForController getCurrentGameStatus();

	public ILadybugForController getLadybug();

	public void setGhostRequest(Point point);

	public void startGame();

	public void startStopSoundActive();

	public void setLadybugRequest(Point point);
}
