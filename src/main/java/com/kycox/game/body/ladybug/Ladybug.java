/**
  Copyright (C) 2021  Frédéric Lanic frederic.lanic@outlook.fr

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.kycox.game.body.ladybug;

import java.awt.Point;

import com.kycox.game.bodyaction.ladybug.LadybugActions;
import com.kycox.game.body.UserBody;
import com.kycox.game.constant.GameMainConstants;
import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.contract.LadybugForController;
import com.kycox.game.contract.LadybugForGameView;
import com.kycox.game.level.ScreenData;

import lombok.Getter;
import lombok.Setter;

public class Ladybug extends UserBody implements LadybugForController, LadybugForGameView {
	@Getter
	private LadybugActions ladybugActions;
	@Setter
	@Getter
	private LadybugStatus status = LadybugStatus.NORMAL;
	@Getter
	private Point viewDirection = GameMainConstants.POINT_UP;

	public int getEatenAIdRefFruit() {
		return ladybugActions.getEatenAIdRefFruit();
	}

	public boolean hasEatenAFruit() {
		return getEatenAIdRefFruit() != GameMainConstants.NOFRUITID;
	}

	@Override
	public boolean isAllowedToDoActions() {
		return getStatus() != LadybugStatus.DYING && getStatus() != LadybugStatus.DEAD;
	}

	public boolean isEatenAMegaPoint() {
		return ladybugActions.isEatenAMegaPoint();
	}

	public boolean isEatenAPoint() {
		return ladybugActions.isEatenAPoint();
	}

	public boolean isToBeTeleported() {
		return ladybugActions.isToBeTeleported();
	}

	public void move(ScreenData screenData) {
		if (ladybugActions.isToBeTeleported()) {
			teleport(screenData);
			return;
		}
		var currentScreenBlock = screenData.getScreenBlock(getPositionBlock());
		if (isPerfectOnABlock()) {
			if (canMove(userRequest, currentScreenBlock)) {
				viewDirection = userRequest;
			}
			move(currentScreenBlock);
		}
		getPosition().translate(getDirection().x * getSpeed(), getDirection().y * getSpeed());
	}

	public LadybugActions setActions(ScreenData screenData) {
		ladybugActions = new LadybugActions();
		if (getStatus() == LadybugStatus.DEAD) {
			return ladybugActions;
		}
		if (userRequest.equals(new Point(getDirection().x, -getDirection().y))) {
			setDirection(userRequest);
			viewDirection = getDirection();
		}
		// calcule uniquement lorsque ladybug rempli le block
		if (isPerfectOnABlock()) {
			var currentScreenBlock = screenData.getScreenBlock(getPositionBlock());
			ladybugActions.setCurrentScreenBlock(currentScreenBlock);
			ladybugActions.setEatenAPoint(currentScreenBlock.isPoint());
			ladybugActions.setEatenAMegaPoint(currentScreenBlock.isMegaPoint());
			ladybugActions.setToBeTeleported(currentScreenBlock.isTeleportation());
			ladybugActions.setEatenAIdRefFruit(currentScreenBlock.getIdRefFruit());
		}
		return ladybugActions;
	}

	public void settingsForNewLevel(int numLevel, Point startPosition) {
		setPosition(startPosition);
		setDirection(GameMainConstants.POINT_ZERO);
		viewDirection = GameMainConstants.POINT_ZERO;
		setUserRequest(GameMainConstants.POINT_ZERO);
		setStatus(LadybugStatus.NORMAL);
		initSpeedIndex(getSpeedFunction().getRealIndexSpeed(numLevel));
	}

	@Override
	public String toString() {
		var sb = new StringBuilder();
		sb.append("Ladybug ");
		sb.append("position: " + getPosition());
		return sb.toString();
	}
}
