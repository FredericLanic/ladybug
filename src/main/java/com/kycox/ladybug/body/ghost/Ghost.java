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
package com.kycox.ladybug.body.ghost;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kycox.ladybug.action.ghost.GhostActions;
import com.kycox.ladybug.body.Body;
import com.kycox.ladybug.body.UserBody;
import com.kycox.ladybug.body.ladybug.Ladybug;
import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.constant.ghost.GhostBehaviousEnum;
import com.kycox.ladybug.constant.ghost.GhostStatusEnum;
import com.kycox.ladybug.constant.ghost.GhostsImagesEnum;
import com.kycox.ladybug.constant.ladybug.LadybugStatusEnum;
import com.kycox.ladybug.level.ScreenBlock;
import com.kycox.ladybug.level.ScreenData;
import com.kycox.ladybug.maths.GhostSensitiveBehavious;
import com.kycox.ladybug.tools.Utils;
import com.kycox.ladybug.tools.dijkstra.Dijkstra;

import lombok.Getter;
import lombok.Setter;

/**
 * Spécificité d'un fantôme
 *
 */
public abstract class Ghost extends UserBody {
	private static final Log		logger = LogFactory.getLog(Ghost.class);
	@Getter
	@Setter
	private GhostBehaviousEnum		behavious;
	@Getter
	@Setter
	private GhostsImagesEnum		images;
	@Setter
	@Getter
	private GhostSensitiveBehavious	sensitiveBehavious;
	@Getter
	@Setter
	private GhostStatusEnum			status = GhostStatusEnum.NORMAL;

	/**
	 * Retourne true si le fantôme est géré par l'ordinateur
	 *
	 * @return
	 */
	public boolean isComputed() {
		return images.isComputed();
	}

	/**
	 * Déplacement du fantôme (géré par l'ordinateur) en fonction de l'emplacement
	 * de Ladybug
	 *
	 * @param ladybugPosBlock
	 */
	public void moveGhostByComputer(Body ladybug, ScreenData screenData) {
		Point ladybugPosBlock = Utils.convertPointToBlockUnit(ladybug.getPosition());
		// Déplacement en fonction du status du fantôme
		switch (getStatus()) {
			case DYING -> moveToRegenerate(screenData);
			case FLASH, SCARED -> flashOrScaredMoving(ladybugPosBlock, screenData);
			case NORMAL -> normalMoving(ladybug, screenData);
			default -> logger.error("Le statut " + getStatus() + " n'est pas reconnu, le fantôme est immobile !!");
		}
	}

	/**
	 * Déplacement du fantôme géré par l'utilisateur
	 *
	 * @param ladybugPosBlock
	 * @param screenData
	 * @param ghostRequest
	 */
	public void moveGhostByUser(Body ladybug, ScreenData screenData, Point ghostRequest) {
		setUserRequest(ghostRequest);
		if (hasChangeBlock() && getStatus().equals(GhostStatusEnum.NORMAL)) {
			move(screenData.getDataBlock(Utils.convertPointToBlockUnit(getPosition())));
			getPosition().translate(getDirection().x * getSpeed(), getDirection().y * getSpeed());
		} else {
			moveGhostByComputer(ladybug, screenData);
		}
	}

	/**
	 * Retourne les actions détectées issu du fantôme.
	 *
	 * @param ladybug
	 * @return
	 */
	public GhostActions setGhostActions(Ladybug ladybug) {
		GhostActions ghostActions = new GhostActions();
		ghostActions.setGhost(this);
		// Détection de la collision avec un fantôme et ladybug
		if (getPosition().distance(ladybug.getPosition()) < (Constants.BLOCK_SIZE / 2)
		        && !getStatus().equals(GhostStatusEnum.DYING) && !getStatus().equals(GhostStatusEnum.REGENERATING)
		        && !ladybug.getStatus().equals(LadybugStatusEnum.DYING)
		        && !ladybug.getStatus().equals(LadybugStatusEnum.DEAD)) {
			if (GhostStatusEnum.isScared().test(this) || GhostStatusEnum.isFlashing().test(this)) {
				ghostActions.setEaten(true);
			} else {
				// Mise à mort de Ladybug !!!
				ghostActions.setEatLadybug(true);
			}
		}
		return ghostActions;
	}

	/**
	 * Cette fonction est a définir pour chaque fantôme nommé (Blinky, Inky, Clyde &
	 * Pinky)
	 *
	 * @param numLevel
	 */
	public abstract void setInitSpeed(int numLevel);

	public void setNumLevel(int numLevel) {
		// initialise le comportement du fantôme en fonction du niveau
		getSensitiveBehavious().setNumLevel(numLevel);
	}

	/**
	 * Affecte des paramètres au fantômes qui vient d'être mangé
	 */
	public void setSettingAfterBeEaten(int numLevel) {
		setPosition(Utils.convertPointToGraphicUnit(Utils.convertPointToBlockUnit(getPosition())));
		// � d�placer dans
		setStatus(GhostStatusEnum.DYING);
		setSpeedIndex(getSpeedFunction().getRealIndexSpeedPlus(numLevel));
	}

	/**
	 * Cette fonction est a définir pour chaque fantôme nommé (Blinky, Inky, Clyde &
	 * Pinky)
	 *
	 * @param numLevel, int perCent
	 */
	public abstract void setSpeed(int numLevel, int perCent);

	private void flashOrScaredMoving(Point ladybugPosBlock, ScreenData screenData) {
		if (Utils.convertPointToGraphicUnit(ladybugPosBlock).distance(getPosition()) < 5 * Constants.BLOCK_SIZE)
			moveScared(ladybugPosBlock, screenData);
		else
			moveByDefault(screenData);
	}

	/**
	 * Déplacement agressif du fantôme
	 *
	 * @param screenData
	 * @param ladybugPosBlock
	 */
	private void moveAgressive(Body ladybug, ScreenData screenData) {
		Point ladybugPosBlock = Utils.convertPointToBlockUnit(ladybug.getPosition());
		moveTo(ladybugPosBlock, screenData);
	}

	/**
	 * Déplacement d'un fantôme en fonction de son comportement
	 *
	 * (son état est NORMAL)
	 *
	 * @param screenData
	 * @param ladybugPosBlock
	 */
	private void moveByBehaviour(Body ladybug, ScreenData screenData) {
		switch (behavious) {
			case SMART -> moveSmart(ladybug, screenData);
			case AGGRESSIVE -> moveAgressive(ladybug, screenData);
			default -> moveByDefault(screenData);
		}
	}

	/**
	 * Déplacement du fantôme Renvoi un objet StateMoving A ce jour, cela ne sert à
	 * rien, mais je préfère le laisser pour les prochaines évolutions.
	 */
	// FIXME : c'est une fonction un peu alambiquée en fait; un refacto me semble
	// nécessaire
	private void moveByDefault(ScreenData screenData) {
		List<Point>	lstDirections = new ArrayList<>();
		Point		posPoint	  = getPosition();
		if (hasChangeBlock()) {
			ScreenBlock currentScreenBlock = screenData.getDataBlock(Utils.convertPointToBlockUnit(posPoint));
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
			if (lstDirections.size() == 0 && currentScreenBlock.isBlocked()) {
				setDirection(Constants.POINT_ZERO);
			} else if (lstDirections.size() == 0 && currentScreenBlock.isDeadEnd()) {
				setDirection(currentScreenBlock.getDirectionInDeadEnd());
			} else if (lstDirections.size() == 0) {
				// NOTE : je ne sais pas si on arrive un jour ici
				logger.info("On ne devrait pas passer par ici");
				setDirection(new Point(getDirection().x, -getDirection().y));
			} else {
				setDirection(lstDirections.get(new Random().nextInt(lstDirections.size())));
			}
		}
		posPoint.translate(getDirection().x * getSpeed(), getDirection().y * getSpeed());
	}

	/**
	 * Déplacement quand le fantôme a peur
	 *
	 * @param data
	 * @param ladybugPosBlock
	 */
	private void moveScared(Point ladybugPosBlock, ScreenData screenData) {
		Point	ptCurrentScreenGhost = getPosition();
		boolean	canScaredMove		 = false;
		Point	scaredDirection		 = Constants.POINT_ZERO;
		if (hasChangeBlock()) {
			Point		ptCurrentBlockGhost	= Utils.convertPointToBlockUnit(ptCurrentScreenGhost);
			ScreenBlock	currentBlockGhost	= screenData.getDataBlock(ptCurrentBlockGhost);
			List<Point>	shorterWay			= Dijkstra.getShorterWay(ptCurrentBlockGhost, ladybugPosBlock, screenData);
			if (shorterWay.size() != 1) {
				Point point0 = shorterWay.get(0);
				Point point1 = shorterWay.get(1);
				scaredDirection	= new Point(point0.x - point1.x, point0.y - point1.y);
				canScaredMove	= canMove(scaredDirection, currentBlockGhost);
			}
		}
		if (canScaredMove) {
			setDirection(scaredDirection);
			ptCurrentScreenGhost.translate(getDirection().x * getSpeed(), getDirection().y * getSpeed());
		} else {
			moveByDefault(screenData);
		}
	}

	private void moveSmart(Body ladybug, ScreenData screenData) {
		Point		ladybugPosBlock	   = Utils.convertPointToBlockUnit(ladybug.getPosition());
		ScreenBlock	ladybugScreenBlock = screenData.getDataBlock(ladybugPosBlock);
		Point		ladybugDirection   = ladybug.getDirection();
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

	private void moveTo(Point ladybugPosBlock, ScreenData screenData) {
		/**
		 * Note : le fantôme peut changer de direction uniquement lorsqu'il rempli le
		 * block
		 */
		if (hasChangeBlock()) {
			Point		ptCurrentBlockGhost	= Utils.convertPointToBlockUnit(getPosition());
			List<Point>	shorterWay			= Dijkstra.getShorterWay(ptCurrentBlockGhost, ladybugPosBlock, screenData);
			Point		point0				= shorterWay.get(0);
			if (shorterWay.size() != 1) {
				Point point1 = shorterWay.get(1);
				setDirection(new Point(point1.x - point0.x, point1.y - point0.y));
			} else {
				setDirection(new Point(ptCurrentBlockGhost.x - point0.x, ptCurrentBlockGhost.y - point0.y));
			}
		}
		getPosition().translate(getDirection().x * getSpeed(), getDirection().y * getSpeed());
	}

	/**
	 * Déplacement du fantôme mangé
	 *
	 * @param data
	 * @param coordinateRevivorGhost
	 */
	private void moveToRegenerate(ScreenData screenData) {
		// Le fantôme est arrivé au limite du block
		if (hasChangeBlock()) {
			// calcul du chemin le plus court :
			List<Point> shorterWay = Dijkstra.getShorterWay(Utils.convertPointToBlockUnit(getPosition()),
			        Utils.convertPointToBlockUnit(screenData.getRevivorGhostPos()), screenData);
			// S'il ne reste plus qu'un bloc, le fantôme est arrivé
			if (shorterWay.size() == 1) {
				// Le fantôme est arrivé au point de regénération, il redevient "normal" avec
				// une vitesse "normale" aussi
				setStatus(GhostStatusEnum.REGENERATING);
			} else {
				// On prend le premier block cible
				Point currentPoint = shorterWay.get(0);
				// calcul du prochain endroit à déplacer le fantôme mangé
				Point nextPoint	= shorterWay.get(1);
				int	  moveX		= nextPoint.x - currentPoint.x;
				int	  moveY		= nextPoint.y - currentPoint.y;
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

	private void normalMoving(Body ladybug, ScreenData screenData) {
		if (sensitiveBehavious.isActive())
			moveByBehaviour(ladybug, screenData);
		else
			moveByDefault(screenData);
	}
}