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

import com.kycox.game.tools.Position;
import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.engine.maze.ScreenBlock;
import com.kycox.game.engine.maze.Maze;
import com.kycox.game.tools.Utils;
import com.kycox.game.input.PlayerInput;
import lombok.Getter;


public abstract class UserBody extends Body {
	/**
	 * Les ordres du joueur qui pilote ce personnage (étape 5.1) : la dernière
	 * direction demandée au clavier, à la manette ou, plus tard, par le réseau.
	 */
	@Getter
	protected final PlayerInput playerInput = new PlayerInput(this::getName);

	/** La dernière direction demandée par le joueur. */
	protected Position userRequest() {
		return playerInput.requested();
	}

	protected boolean canMove(Position direction, ScreenBlock screenBlock) {
		return ((!direction.equals(GameMainConstants.POINT_LEFT) || !screenBlock.isBorderLeft())
		        && (!direction.equals(GameMainConstants.POINT_RIGHT) || !screenBlock.isBorderRight())
		        && (!direction.equals(GameMainConstants.POINT_UP) || !screenBlock.isBorderUp())
		        && (!direction.equals(GameMainConstants.POINT_DOWN) || !screenBlock.isBorderDown()));
	}

	protected void move(ScreenBlock screenBlock) {
		move(screenBlock, userRequest());
	}

	/**
	 * Avance d'une case en suivant cette direction demandée, si le mur le permet ;
	 * sinon, garde sa direction ou s'arrête contre le mur.
	 */
	protected void move(ScreenBlock screenBlock, Position request) {
		if (isPerfectOnABlock()) {
			var hasRequest = request.x() != 0 || request.y() != 0;
			if (hasRequest && canMove(request, screenBlock)) {
				setDirection(request);
			}
			// Quand le body est coincé : au début ou quand il butte sur un mur.
			if (!canMove(getDirection(), screenBlock)) {
				setDirection(GameMainConstants.POINT_ZERO);
			}
		}
	}

	public void teleport(Maze maze) {
		if (isPerfectOnABlock()) {
			var currentScreenBlock = maze.getScreenBlock(getPositionBlock());
			var destinationPoint = Utils.convertBlockPointToGraphicPoint(currentScreenBlock.getDestinationPoint());
			setPosition(destinationPoint);
			setPosition(getPosition().translate(getDirection().x() * getSpeed(), getDirection().y() * getSpeed()));
		}
	}

	public String getName() {
		var className = getClass().toString();
		return className.substring(className.lastIndexOf(".") + 1);
	}
}
