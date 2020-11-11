/**
  Copyright (C) 2019  Frédéric Lanic frederic.lanic@outlook.fr

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
package com.kycox.ladybug.body.ladybug;

import java.awt.Point;

import com.kycox.ladybug.action.ladybug.LadybugActions;
import com.kycox.ladybug.body.UserBody;
import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.constant.ladybug.LadybugStatusEnum;
import com.kycox.ladybug.level.ScreenBlock;
import com.kycox.ladybug.level.ScreenData;
import com.kycox.ladybug.tools.Utils;

import lombok.Getter;
import lombok.Setter;

/**
 * Spécificités de Ladybug
 *
 */
public class Ladybug extends UserBody {
	// statut de ladybug
	@Setter
	@Getter
	private LadybugStatusEnum status = LadybugStatusEnum.NORMAL;
	// direction de ladybug
	@Getter
	private Point viewDirection = Constants.POINT_ZERO;

	/**
	 * Déplacement de ladybug et récupération du score obtenu Lancement du double
	 * timer dans le cas du super power
	 */
	public LadybugActions getActions(ScreenData screenData) {
		LadybugActions ladybugActions = new LadybugActions();
		if (getStatus().equals(LadybugStatusEnum.DEAD))
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

	public void move(ScreenData screenData, LadybugActions ladybugActions) {
		if (ladybugActions.isToBeTeleported()) {
			teleport(screenData);
		} else {
			move(screenData);
		}
	}

	/**
	 * Caractéristiques de ladybug en début de niveau
	 */
	public void setStartLevel(int numLevel, Point startPosition) {
		// attributs de ladybug
		setPosition(startPosition);
		setDirection(Constants.POINT_ZERO);
		viewDirection = Constants.POINT_LEFT;
		setUserRequest(Constants.POINT_ZERO);
		setStatus(LadybugStatusEnum.NORMAL);
		initSpeedIndex(getSpeedFunction().getRealIndexSpeed(numLevel));
	}

	private void move(ScreenData screenData) {
		ScreenBlock currentScreenBlock = screenData.getDataBlock(Utils.convertPointToBlockUnit(getPosition()));
		if (hasChangeBlock()) {
			if (canMove(userRequest, currentScreenBlock))
				viewDirection = userRequest;
			move(currentScreenBlock);
		}
		getPosition().translate(getDirection().x * getSpeed(), getDirection().y * getSpeed());
	}

	private void teleport(ScreenData screenData) {
		if (hasChangeBlock()) {
			Point newPoint = screenData.getRandomPosOnAPoint();
			setPosition(Utils.convertPointToGraphicUnit(newPoint));
			setDirection(Constants.POINT_ZERO);
		}
	}
}
