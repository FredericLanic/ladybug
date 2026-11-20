package com.kycox.game.constant.contract;


import com.kycox.game.tools.Position;
import com.kycox.game.constant.ghost.GhostStatus;
import com.kycox.game.body.ghost.GhostColor;

public interface GhostForView {

	GhostColor getColor();

	Position getDirection();

	GhostStatus getStatus();

	boolean isCamouflage();

}
