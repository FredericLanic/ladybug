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
package com.kycox.game.body.ghost;

import com.kycox.game.action.ghost.GhostActions;
import com.kycox.game.body.UserBody;
import com.kycox.game.body.ladybug.Ladybug;
import com.kycox.game.constant.Constants;
import com.kycox.game.constant.ghost.GhostBehaviour;
import com.kycox.game.constant.ghost.GhostStatus;
import com.kycox.game.constant.ghost.image.GhostsBodyImages;
import com.kycox.game.contract.GhostForController;
import com.kycox.game.contract.GhostForView;
import com.kycox.game.level.ScreenData;
import com.kycox.game.maths.GhostSensitiveBehaviour;
import com.kycox.game.tools.Utils;
import com.kycox.game.tools.dijkstra.Dijkstra;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Ghost extends UserBody implements GhostForController, GhostForView {
	private static final Log logger = LogFactory.getLog(Ghost.class);
	@Getter
	@Setter
	private GhostBehaviour behaviour;
	@Getter
	@Setter
	private GhostsBodyImages color;
	@Getter
	private GhostActions ghostActions;
	@Setter
	@Getter
	private GhostSensitiveBehaviour sensitiveBehaviour;
	@Getter
	@Setter
	private GhostStatus status = GhostStatus.NORMAL;

	@Getter
	@Setter
	private int maximumAttackDist;

	// FIXME : c'est une fonction un peu alambiquée en fait; un refacto me semble
	// nécessaire
	private void defaultMoving(ScreenData screenData) {
		List<Point> lstDirections = new ArrayList<>();
		if (isPerfectOnABlock()) {
			var currentScreenBlock = screenData.getScreenBlock(getPositionBlock());
			if (!currentScreenBlock.isBorderLeft() && getDirection().x != 1) {
				lstDirections.add(Constants.POINT_LEFT);
			}
			if (!currentScreenBlock.isBorderUp() && getDirection().y != 1) {
				lstDirections.add(Constants.POINT_UP);
			}
			if (!currentScreenBlock.isBorderRight() && getDirection().x != -1) {
				lstDirections.add(Constants.POINT_RIGHT);
			}
			if (!currentScreenBlock.isBorderDown() && getDirection().y != -1) {
				lstDirections.add(Constants.POINT_DOWN);
			}
			// mise à jour de la direction
			if (lstDirections.isEmpty() && currentScreenBlock.isBlocked()) {
				setDirection(Constants.POINT_ZERO);
			} else if (lstDirections.isEmpty() && currentScreenBlock.isDeadEnd()) {
				setDirection(currentScreenBlock.getDirectionInDeadEnd());
			} else if (lstDirections.isEmpty()) {
				// NOTE : je ne sais pas si on arrive un jour ici
				logger.info("On ne devrait pas passer par ici");
				setDirection(new Point(getDirection().x, -getDirection().y));
			} else {
				setDirection(lstDirections.get(Utils.generateRandomInt(lstDirections.size())));
			}
		}
		getPosition().translate(getDirection().x * getSpeed(), getDirection().y * getSpeed());
	}

	private boolean hasTouchedLadybug(Ladybug ladybug) {
		return isTooNearOfLadybug(ladybug) && isAllowedToDoActions() && ladybug.isAllowedToDoActions();
	}

	@Override
	protected boolean isAllowedToDoActions() {
		return getStatus() != GhostStatus.DYING && getStatus() != GhostStatus.REGENERATED;
	}

	public boolean isComputed() {
		return color.isComputed();
	}

	@Override
	public boolean isDying() {
		return getStatus() == GhostStatus.DYING;
	}

	public boolean isScaredOrFlashed() {
		return getStatus() == GhostStatus.SCARED || getStatus() == GhostStatus.FLASH;
	}

	public boolean isToBeTeleported() {
		return ghostActions.isToBeTeleported();
	}

	private boolean isTooNearOfLadybug(Ladybug ladybug) {
		return getPosition().distance(ladybug.getPosition()) < (Constants.BLOCK_SIZE / 2);
	}

	private void moveByBehaviour(Ladybug ladybug, ScreenData screenData) {
		switch (behaviour) {
			case SMART -> smartMoving(ladybug, screenData);
			case AGGRESSIVE -> moveTo(ladybug.getPositionBlock(), screenData);
			default -> defaultMoving(screenData);
		}
	}

	public void moveComputedGhost(Ladybug ladybug, ScreenData screenData) {
		// Déplacement en fonction du status du fantôme
		switch (getStatus()) {
			case DYING -> regeneratePointMoving(screenData);
			case FLASH, SCARED -> scaredOrFlashedMoving(ladybug.getPositionBlock(), screenData);
			case NORMAL -> normalMoving(ladybug, screenData);
			case REGENERATED -> { /* Do nothing */ }
			default -> logger.error("Le statut " + getStatus() + " n'est pas reconnu, le fantôme est immobile !!");
		}
	}

	public void moveGhostUser(Ladybug ladybug, ScreenData screenData, Point ghostRequest) {
		setUserRequest(ghostRequest);

		switch (getStatus()) {
			case DYING -> moveComputedGhost(ladybug, screenData);
			case REGENERATED -> {
				// Do nothing
			}
			case FLASH, SCARED, NORMAL -> {
				move(screenData.getScreenBlock(getPositionBlock()));
				getPosition().translate(getDirection().x * getSpeed(), getDirection().y * getSpeed());
			}
			default -> logger.error("Le statut " + getStatus() + " n'est pas reconnu, le fantôme est immobile !!");
		}
	}

	private void moveScared(Point ladybugPosBlock, ScreenData screenData) {
		var canScaredMove = false;
		var scaredDirection = Constants.POINT_ZERO;
		if (isPerfectOnABlock()) {
			var ptCurrentBlockGhost = getPositionBlock();
			var currentBlockGhost = screenData.getScreenBlock(ptCurrentBlockGhost);
			var shorterWay = Dijkstra.getShorterWay(ptCurrentBlockGhost, ladybugPosBlock, screenData);
			if (shorterWay.size() != 1) {
				var point0 = shorterWay.get(0);
				var point1 = shorterWay.get(1);
				scaredDirection = new Point(point0.x - point1.x, point0.y - point1.y);
				canScaredMove = canMove(scaredDirection, currentBlockGhost);
			}
		}
		if (canScaredMove) {
			setDirection(scaredDirection);
			getPosition().translate(getDirection().x * getSpeed(), getDirection().y * getSpeed());
		} else {
			defaultMoving(screenData);
		}
	}

	private void moveTo(Point ladybugPosBlock, ScreenData screenData) {
		if (isPerfectOnABlock()) {
			var ptCurrentBlockGhost = getPositionBlock();
			var shorterWay = Dijkstra.getShorterWay(ptCurrentBlockGhost, ladybugPosBlock, screenData);
			var point0 = shorterWay.get(0);
			if (shorterWay.size() != 1) {
				var point1 = shorterWay.get(1);
				setDirection(new Point(point1.x - point0.x, point1.y - point0.y));
			} else {
				setDirection(new Point(ptCurrentBlockGhost.x - point0.x, ptCurrentBlockGhost.y - point0.y));
			}
		}
		getPosition().translate(getDirection().x * getSpeed(), getDirection().y * getSpeed());
	}

	private void normalMoving(Ladybug ladybug, ScreenData screenData) {
		if (getPosition().distance(ladybug.getPosition()) < getMaximumAttackDist() && ladybug.isAllowedToDoActions()) {
			moveTo(ladybug.getPositionBlock(), screenData);
		} else if (sensitiveBehaviour.isActive() && ladybug.isAllowedToDoActions()) {
			moveByBehaviour(ladybug, screenData);
		} else {
			defaultMoving(screenData);
		}
	}

	private void regeneratePointMoving(ScreenData screenData) {
		// Le fantôme est arrivé au limite du block
		if (isPerfectOnABlock()) {
			// calcul du chemin le plus court :
			var shorterWay = Dijkstra.getShorterWay(getPositionBlock(),
			        Utils.convertGraphicPointToBlockPoint(screenData.getRevivorGhostPos()), screenData);
			// S'il ne reste plus qu'un bloc, le fantôme est arrivé
			if (shorterWay.size() == 1) {
				// Le fantôme est arrivé au point de regénération, il redevient "normal" avec
				// une vitesse "normale" aussi
				setStatus(GhostStatus.TOBEREGERENATED);
			} else {
				// On prend le premier block cible
				var currentPoint = shorterWay.get(0);
				// calcul du prochain endroit à déplacer le fantôme mangé
				var nextPoint = shorterWay.get(1);
				var moveX = nextPoint.x - currentPoint.x;
				var moveY = nextPoint.y - currentPoint.y;
				if (moveX > 0) {
					setDirection(Constants.POINT_RIGHT);
				} else if (moveX < 0) {
					setDirection(Constants.POINT_LEFT);
				} else if (moveY > 0) {
					setDirection(Constants.POINT_DOWN);
				} else if (moveY < 0) {
					setDirection(Constants.POINT_UP);
				}
			}
		}
		getPosition().translate(getDirection().x * getSpeed(), getDirection().y * getSpeed());
	}

	private void scaredOrFlashedMoving(Point ladybugPosBlock, ScreenData screenData) {
		if (Utils.convertBlockPointToGraphicPoint(ladybugPosBlock).distance(getPosition()) < 5 * Constants.BLOCK_SIZE) {
			moveScared(ladybugPosBlock, screenData);
		} else {
			defaultMoving(screenData);
		}
	}

	/**
	 * Retourne les actions détectées issu du fantôme.
	 *
	 * @param ladybug
	 * @return
	 */
	public void setGhostActions(Ladybug ladybug, ScreenData screenData) {
		ghostActions = new GhostActions();
		ghostActions.setPosition((Point) getPosition().clone());
		// Détection de la collision avec un fantôme et ladybug
		if (hasTouchedLadybug(ladybug)) {
			if (isScaredOrFlashed()) {
				ghostActions.setEatenByLadybug(true);
			} else {
				ghostActions.setEatLadybug(true);
			}
		}
		// calcule uniquement lorsque ladybug rempli le block
		if (isPerfectOnABlock()) {
			var currentScreenBlock = screenData.getScreenBlock(getPositionBlock());
			ghostActions.setToBeTeleported(currentScreenBlock.isTeleportation());
		}
	}

	public abstract void setInitSpeed(int numLevel);

	public void setNumLevel(int numLevel) {
		// initialise le comportement du fantôme en fonction du niveau
		getSensitiveBehaviour().setNumLevel(numLevel);
	}

	public void setSettingAfterBeEaten(int numLevel) {
		setPosition(Utils.convertBlockPointToGraphicPoint(getPositionBlock()));
		setStatus(GhostStatus.DYING);
		setSpeedIndex(getSpeedFunction().getRealIndexSpeedPlus(numLevel));
	}

	public abstract void setSpeed(int numLevel, int perCent);

	private void smartMoving(Ladybug ladybug, ScreenData screenData) {
		moveTo(ladybug.getFrontPositionBlock(screenData), screenData);
	}
}