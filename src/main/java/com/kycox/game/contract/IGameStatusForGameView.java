package com.kycox.game.contract;

public interface IGameStatusForGameView extends IGameCommon {
	public int getNumLevel();

	public boolean isNoGame();

	public boolean isToConfiguration();
	
	public boolean isLevelBegin();

	// FIXME : hmmm ?
	public void setNoGame();
}
