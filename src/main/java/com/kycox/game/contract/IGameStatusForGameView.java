package com.kycox.game.contract;

public interface IGameStatusForGameView extends IGameCommon {
	public int getNumLevel();

	public boolean isNoGame();

	public boolean isToConfiguration();

	// FIXME : hmmm ?
	public void setBeginingLevel();

	// FIXME : hmmm ?
	public void setNoGame();
}
