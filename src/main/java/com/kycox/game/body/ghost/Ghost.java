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

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kycox.game.action.ghost.GhostActions;
import com.kycox.game.body.UserBody;
import com.kycox.game.body.ladybug.Ladybug;
import com.kycox.game.constant.Constants;
import com.kycox.game.constant.ghost.GhostBehavious;
import com.kycox.game.constant.ghost.GhostStatus;
import com.kycox.game.constant.ghost.image.GhostsBodyImages;
import com.kycox.game.constant.ladybug.LadybugStatus;
import com.kycox.game.level.ScreenData;
import com.kycox.game.maths.GhostSensitiveBehavious;
import com.kycox.game.tools.Utils;
import com.kycox.game.tools.dijkstra.Dijkstra;

import lombok.Getter;
import lombok.Setter;

public abstract class Ghost extends UserBody {
	private static final Log logger = LogFactory.getLog(Ghost.class);
	@Getter
	@Setter
	private GhostBehavious behavious;
	@Getter
	@Setter
	private GhostsBodyImages color;
	@Getter
	private GhostActions ghostActions;
	@Setter
	@Getter
	private GhostSensitiveBehavious sensitiveBehavious;
	@Getter
	@Setter
	private GhostStatus status = GhostStatus.NORMAL;

	// FIXME : c'est une fonction un peu alambiquée en fait; un refacto me semble
	// nécessaire
	private void defaultMoving(ScreenData screenData) {
		List<Point> lstDirections = new ArrayList<>();
		var posPoint = getPosition();
		if (isPerfectOnABlock()) {
			var currentScreenBlock = screenData.getDataBlock(Utils.convertPointToBlockUnit(posPoint));
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
		posPoint.translate(getDirection().x * getSpeed(), getDirection().y * getSpeed());
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

	private boolean isScaredOrFlashed() {
		return getStatus() == GhostStatus.SCARED || getStatus() == GhostStatus.FLASH;
	}

	private boolean isTooNearOfLadybug(Ladybug ladybug) {
		return getPosition().distance(ladybug.getPosition()) < (Constants.BLOCK_SIZE / 2);
	}

	private void moveByBehaviour(Point ladybugPosBlock, Point ladybugDirection, ScreenData screenData) {
		switch (behavious) {
			case SMART -> smartMoving(ladybugPosBlock, ladybugDirection, screenData);
			case AGGRESSIVE -> moveTo(ladybugPosBlock, screenData);
			default -> defaultMoving(screenData);
		}
	}

	public void moveComputedGhost(Ladybug ladybug, ScreenData screenData) {
		var ladybugPosBlock = Utils.convertPointToBlockUnit(ladybug.getPosition());
		// Déplacement en fonction du status du fantôme
		switch (getStatus()) {
			case DYING -> moveToRegeneratePoint(screenData);
			case FLASH, SCARED -> scaredOrFlashedMoving(ladybugPosBlock, screenData);
			case NORMAL -> normalMoving(ladybug, screenData);
			case REGENERATED -> {
				// Do nothing
			}
			default -> logger.error("Le statut " + getStatus() + " n'est pas reconnu, le fantôme est immobile !!");

		}
	}

	public void moveGhostUser(Ladybug ladybug, ScreenData screenData, Point ghostRequest) {
		setUserRequest(ghostRequest);
		if (isPerfectOnABlock() && getStatus() == GhostStatus.NORMAL) {
			move(screenData.getDataBlock(Utils.convertPointToBlockUnit(getPosition())));
			getPosition().translate(getDirection().x * getSpeed(), getDirection().y * getSpeed());
		} else {
			moveComputedGhost(ladybug, screenData);
		}
	}

	private void moveScared(Point ladybugPosBlock, ScreenData screenData) {
		var ptCurrentScreenGhost = getPosition();
		var canScaredMove = false;
		var scaredDirection = Constants.POINT_ZERO;
		if (isPerfectOnABlock()) {
			var ptCurrentBlockGhost = Utils.convertPointToBlockUnit(ptCurrentScreenGhost);
			var currentBlockGhost = screenData.getDataBlock(ptCurrentBlockGhost);
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
			ptCurrentScreenGhost.translate(getDirection().x * getSpeed(), getDirection().y * getSpeed());
		} else {
			defaultMoving(screenData);
		}
	}

	private void moveTo(Point ladybugPosBlock, ScreenData screenData) {
		if (isPerfectOnABlock()) {
			var ptCurrentBlockGhost = Utils.convertPointToBlockUnit(getPosition());
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

	private void moveToRegeneratePoint(ScreenData screenData) {
		// Le fantôme est arrivé au limite du block
		if (isPerfectOnABlock()) {
			// calcul du chemin le plus court :
			var shorterWay = Dijkstra.getShorterWay(Utils.convertPointToBlockUnit(getPosition()),
			        Utils.convertPointToBlockUnit(screenData.getRevivorGhostPos()), screenData);
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

	private void normalMoving(Ladybug ladybug, ScreenData screenData) {
		var ladybugPosBlock = Utils.convertPointToBlockUnit(ladybug.getPosition());
		var ladybugDirection = ladybug.getDirection();
		if (sensitiveBehavious.isActive() && ladybug.getStatus() != LadybugStatus.DEAD) {
			moveByBehaviour(ladybugPosBlock, ladybugDirection, screenData);
		} else {
			defaultMoving(screenData);
		}
	}

	private void scaredOrFlashedMoving(Point ladybugPosBlock, ScreenData screenData) {
		if (Utils.convertPointToGraphicUnit(ladybugPosBlock).distance(getPosition()) < 5 * Constants.BLOCK_SIZE) {
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
			var currentScreenBlock = screenData.getDataBlock(Utils.convertPointToBlockUnit(getPosition()));
			ghostActions.setToBeTeleported(currentScreenBlock.isTeleportation());
		}
	}

	public abstract void setInitSpeed(int numLevel);

	public void setNumLevel(int numLevel) {
		// initialise le comportement du fantôme en fonction du niveau
		getSensitiveBehavious().setNumLevel(numLevel);
	}

	public void setSettingAfterBeEaten(int numLevel) {
		setPosition(Utils.convertPointToGraphicUnit(Utils.convertPointToBlockUnit(getPosition())));
		// � d�placer dans
		setStatus(GhostStatus.DYING);
		setSpeedIndex(getSpeedFunction().getRealIndexSpeedPlus(numLevel));
	}

	public abstract void setSpeed(int numLevel, int perCent);

	private void smartMoving(Point ladybugPosBlock, Point ladybugDirection, ScreenData screenData) {
		// Point ladybugPosBlock = Utils.convertPointToBlockUnit(ladybug.getPosition());
		var ladybugScreenBlock = screenData.getDataBlock(ladybugPosBlock);
		// Point ladybugDirection = ladybug.getDirection();
		if (ladybugDirection.equals(Constants.POINT_UP) && !ladybugScreenBlock.isBorderUp()) {
			ladybugPosBlock.y--;
		} else if (ladybugDirection.equals(Constants.POINT_DOWN) && !ladybugScreenBlock.isBorderDown()) {
			ladybugPosBlock.y++;
		} else if (ladybugDirection.equals(Constants.POINT_RIGHT) && !ladybugScreenBlock.isBorderRight()) {
			ladybugPosBlock.x++;
		} else if (ladybugDirection.equals(Constants.POINT_LEFT) && !ladybugScreenBlock.isBorderLeft()) {
			ladybugPosBlock.x--;
		}
		moveTo(ladybugPosBlock, screenData);
	}
}