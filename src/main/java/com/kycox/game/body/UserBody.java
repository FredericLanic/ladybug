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
package com.kycox.game.body;

import com.kycox.game.constant.Constants;
import com.kycox.game.level.ScreenBlock;
import com.kycox.game.level.ScreenData;
import com.kycox.game.tools.Utils;
import lombok.Setter;

import java.awt.*;

public abstract class UserBody extends Body {
	@Setter
	protected Point userRequest = Constants.POINT_ZERO;

	protected boolean canMove(Point direction, ScreenBlock screenBlock) {
		return ((!direction.equals(Constants.POINT_LEFT) || !screenBlock.isBorderLeft())
		        && (!direction.equals(Constants.POINT_RIGHT) || !screenBlock.isBorderRight())
		        && (!direction.equals(Constants.POINT_UP) || !screenBlock.isBorderUp())
		        && (!direction.equals(Constants.POINT_DOWN) || !screenBlock.isBorderDown()));
	}

	protected void move(ScreenBlock screenBlock) {
		if (isPerfectOnABlock()) {
			if (userRequestHasChanged() && canMove(userRequest, screenBlock)) {
				setDirection(userRequest);
			}
			// Quand le body est coincé : au début ou quand il butte sur un mur.
			if (!canMove(getDirection(), screenBlock)) {
				setDirection(Constants.POINT_ZERO);
			}
		}
	}

	public void teleport(ScreenData screenData) {
		if (isPerfectOnABlock()) {
			var currentScreenBlock = screenData.getScreenBlock(getPositionBlock());
			var destinationPoint = Utils.convertBlockPointToGraphicPoint(currentScreenBlock.getDestinationPoint());
			setPosition(destinationPoint);
			getPosition().translate(getDirection().x * getSpeed(), getDirection().y * getSpeed());
		}
	}

	private boolean userRequestHasChanged() {
		return userRequest.x != 0 || userRequest.y != 0;
	}
}
