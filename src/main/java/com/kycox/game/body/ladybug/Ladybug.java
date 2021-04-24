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

import com.kycox.game.action.ladybug.LadybugActions;
import com.kycox.game.body.UserBody;
import com.kycox.game.constant.Constants;
import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.contract.ILadybugForController;
import com.kycox.game.contract.ILadybugForGameView;
import com.kycox.game.level.ScreenBlock;
import com.kycox.game.level.ScreenData;
import com.kycox.game.tools.Utils;

import lombok.Getter;
import lombok.Setter;

/**
 * Spécificités de Ladybug
 *
 */
public class Ladybug extends UserBody implements ILadybugForController, ILadybugForGameView {
	@Getter
	private LadybugActions ladybugActions;
	// statut de ladybug
	@Setter
	@Getter
	private LadybugStatus status = LadybugStatus.NORMAL;
	// direction de ladybug
	@Getter
	private Point viewDirection = Constants.POINT_UP;

	public boolean isEatenAMegaPoint() {
		return ladybugActions.isEatenAMegaPoint();
	}

	public boolean isEatenAPoint() {
		return ladybugActions.isEatenAPoint();
	}

	public boolean isToBeTeleported() {
		return ladybugActions.isToBeTeleported();
	}

	/**
	 * Déplacement de Ladybug
	 *
	 * @param screenData
	 */
	public void move(ScreenData screenData) {
		if (ladybugActions.isToBeTeleported()) {
			teleport(screenData);
			return;
		}
		ScreenBlock currentScreenBlock = screenData.getDataBlock(Utils.convertPointToBlockUnit(getPosition()));
		if (hasChangeBlock()) {
			if (canMove(userRequest, currentScreenBlock))
				viewDirection = userRequest;
			move(currentScreenBlock);
		}
		getPosition().translate(getDirection().x * getSpeed(), getDirection().y * getSpeed());
	}

	/**
	 * Détermincaiton des actions de Ladybug
	 */
	public LadybugActions setActions(ScreenData screenData) {
		ladybugActions = new LadybugActions();
		if (getStatus().equals(LadybugStatus.DEAD))
			return ladybugActions;
		if (userRequest.equals(new Point(getDirection().x, -getDirection().y))) {
			setDirection(userRequest);
			this.viewDirection = getDirection();
		}
		// calcule uniquement lorsque ladybug rempli le block
		if (hasChangeBlock()) {
			ScreenBlock currentScreenBlock = screenData.getDataBlock(Utils.convertPointToBlockUnit(getPosition()));
			ladybugActions.setCurrentScreenBlock(currentScreenBlock);
			ladybugActions.setEatenAPoint(currentScreenBlock.isPoint());
			ladybugActions.setEatenAMegaPoint(currentScreenBlock.isMegaPoint());
			ladybugActions.setToBeTeleported(currentScreenBlock.isTeleportation());
		}
		return ladybugActions;
	}

	/**
	 * Caractéristiques de ladybug en début de niveau
	 */
	public void setStartLevel(int numLevel, Point startPosition) {
		// attributs de ladybug
		setPosition(startPosition);
		setDirection(Constants.POINT_ZERO);
		viewDirection = Constants.POINT_ZERO;
		setUserRequest(Constants.POINT_ZERO);
		setStatus(LadybugStatus.NORMAL);
		initSpeedIndex(getSpeedFunction().getRealIndexSpeed(numLevel));
	}

	private void teleport(ScreenData screenData) {
		if (hasChangeBlock() && screenData.getNbrBlocksWithPoint() > 0) {
			Point newPoint = screenData.getRandomPosOnAPoint();
			setPosition(Utils.convertPointToGraphicUnit(newPoint));
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Ladybug ");
		sb.append("position: " + getPosition());
		return sb.toString();
	}
	
}
