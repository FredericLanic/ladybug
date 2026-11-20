package com.kycox.game.constant.contract;

import java.awt.Point;

import com.kycox.game.constant.ghost.GhostStatus;
import com.kycox.game.body.ghost.GhostColor;

public interface GhostForView {

	GhostColor getColor();

	Point getDirection();

	GhostStatus getStatus();

	boolean isCamouflage();

}
