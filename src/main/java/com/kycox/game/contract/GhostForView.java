package com.kycox.game.contract;

import java.awt.Point;

import com.kycox.game.constant.ghost.GhostStatus;
import com.kycox.game.constant.ghost.GhostsBodyImages;

public interface GhostForView {

	GhostsBodyImages getColor();

	Point getDirection();

	GhostStatus getStatus();

	boolean isCamouflage();

}
