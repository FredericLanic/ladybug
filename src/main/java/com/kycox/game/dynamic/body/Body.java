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
package com.kycox.game.dynamic.body;

import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.engine.screendata.ScreenData;
import com.kycox.game.tools.maths.SpeedFunction;
import com.kycox.game.tools.Utils;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
public abstract class Body {
	@Setter
	private Point direction = GameMainConstants.POINT_ZERO;
	@Setter
	private int leftLifes = 0;
	@Setter
	private boolean newLife = false;
	@Setter
	private Point position = GameMainConstants.POINT_ZERO;
	@Setter
	private SpeedFunction speedFunction;
	@Setter
	private int speedIndex = 0;
	private int startSpeedIndex = 0;

	public Point getFrontPositionBlock(ScreenData screenData) {
		var positionBlock = (Point) getPositionBlock().clone();
		var screenBlock = screenData.getScreenBlock(positionBlock);
		if (direction.equals(GameMainConstants.POINT_UP) && !screenBlock.isBorderUp()) {
			positionBlock.y--;
		} else if (direction.equals(GameMainConstants.POINT_DOWN) && !screenBlock.isBorderDown()) {
			positionBlock.y++;
		} else if (direction.equals(GameMainConstants.POINT_RIGHT) && !screenBlock.isBorderRight()) {
			positionBlock.x++;
		} else if (direction.equals(GameMainConstants.POINT_LEFT) && !screenBlock.isBorderLeft()) {
			positionBlock.x--;
		}
		return positionBlock;
	}

	public Point getPositionBlock() {
		return Utils.convertGraphicPointToBlockPoint(getPosition());
	}

	public int getSpeed() {
		return GameMainConstants.VALID_SPEEDS
		        .get(speedIndex < GameMainConstants.VALID_SPEEDS.size() ? speedIndex : GameMainConstants.VALID_SPEEDS.size() - 1);
	}

	public void initSpeedIndex(int speedIndex) {
		this.speedIndex = speedIndex;
		this.startSpeedIndex = speedIndex;
	}

	protected abstract boolean isAllowedToDoActions();

	public boolean isPerfectOnABlock() {
		var pointPos = getPosition();
		return pointPos.x % GameMainConstants.BLOCK_SIZE == 0 && pointPos.y % GameMainConstants.BLOCK_SIZE == 0;
	}

	public void lostsALife() {
		this.leftLifes--;
	}

	public void manageNewLife() {
		if (isNewLife()) {
			setNewLife(false);
			this.leftLifes++;
		}
	}
}
