package com.kycox.game.contract;

public interface IGameStatusForGameView {
	public int getNumLevel();

	public boolean isInGame();

	public boolean isNoGame();

	public boolean isToConfiguration();

	// FIXME : hmmm ?
	public void setBeginingLevel();

	// FIXME : hmmm ?
	public void setNoGame();
}
