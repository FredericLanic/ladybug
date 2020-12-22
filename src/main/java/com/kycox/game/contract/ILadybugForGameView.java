package com.kycox.game.contract;

import java.awt.Point;

import com.kycox.game.constant.ladybug.LadybugStatus;

public interface ILadybugForGameView {
	public int getLeftLifes();

	public Point getPosition();

	public LadybugStatus getStatus();

	public Point getViewDirection();
}
