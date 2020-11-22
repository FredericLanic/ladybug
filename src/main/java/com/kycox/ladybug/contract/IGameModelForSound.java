package com.kycox.ladybug.contract;

import com.kycox.ladybug.model.GameStatus;

public interface IGameModelForSound {
	// FIXME : c'est trop spécifique !!
	public GameStatus getGameStatus();

	public int getSounds();

	public boolean isSoundActive();

	public void startGameTimer();

	public void stopGameTimer();
}
