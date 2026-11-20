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


import com.kycox.game.tools.Position;
import com.kycox.game.engine.bodyaction.ladybug.LadybugActions;
import com.kycox.game.body.UserBody;
import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.engine.maze.Maze;

import lombok.Getter;
import lombok.Setter;

@Getter
public final class Ladybug extends UserBody {
	/**
	 * Actions du dernier battement. Jamais nulles depuis l'étape 4.2 (D23) : avant,
	 * elles n'existaient qu'après le premier battement de l'écran de présentation.
	 */
	private LadybugActions ladybugActions = new LadybugActions();
	@Setter
	private LadybugStatus status = LadybugStatus.NORMAL;
	private Position viewDirection = GameMainConstants.POINT_UP;

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

	public void move(Maze maze) {
		if (ladybugActions.isToBeTeleported()) {
			teleport(maze);
			return;
		}
		var currentScreenBlock = maze.getScreenBlock(getPositionBlock());
		if (isPerfectOnABlock()) {
			if (canMove(userRequest(), currentScreenBlock)) {
				viewDirection = userRequest();
			}
			move(currentScreenBlock);
		}
		setPosition(getPosition().translate(getDirection().x() * getSpeed(), getDirection().y() * getSpeed()));
	}

	public LadybugActions setActions(Maze maze) {
		ladybugActions = new LadybugActions();
		if (getStatus() == LadybugStatus.DEAD) {
			return ladybugActions;
		}
		if (userRequest().equals(new Position(getDirection().x(), -getDirection().y()))) {
			setDirection(userRequest());
			viewDirection = getDirection();
		}
		// calcule uniquement lorsque ladybug rempli le block
		if (isPerfectOnABlock()) {
			var currentScreenBlock = maze.getScreenBlock(getPositionBlock());
			ladybugActions.setCurrentScreenBlock(currentScreenBlock);
			ladybugActions.setEatenAPoint(currentScreenBlock.isPoint());
			ladybugActions.setEatenAMegaPoint(currentScreenBlock.isMegaPoint());
			ladybugActions.setToBeTeleported(currentScreenBlock.isTeleportation());
			ladybugActions.setEatenAIdRefFruit(currentScreenBlock.getIdRefFruit());
		}
		return ladybugActions;
	}

	public void settingsForNewLevel(int numLevel, Position startPosition) {
		setPosition(startPosition);
		setDirection(GameMainConstants.POINT_ZERO);
		viewDirection = GameMainConstants.POINT_ZERO;
		playerInput.reset();
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
