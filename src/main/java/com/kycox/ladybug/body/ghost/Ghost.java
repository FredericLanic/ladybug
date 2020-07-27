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
import java.util.List;
import java.util.Random;

import com.kycox.ladybug.action.ghost.GhostActions;
import com.kycox.ladybug.body.BodyMovedByUser;
import com.kycox.ladybug.body.ladybug.Ladybug;
import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.constant.ghost.GhostStatusEnum;
import com.kycox.ladybug.constant.ghost.GhostsSettingsEnum;
import com.kycox.ladybug.constant.ladybug.LadybugStatusEnum;
import com.kycox.ladybug.level.ScreenBlock;
import com.kycox.ladybug.level.ScreenData;
import com.kycox.ladybug.maths.GhostBehavious;
import com.kycox.ladybug.maths.SpeedFunction;
import com.kycox.ladybug.tools.Utils;
import com.kycox.ladybug.tools.dijkstra.Dijkstra;

/**
 * Spécificité d'un fantôme
 *
 */
public abstract class Ghost extends BodyMovedByUser {
	private GhostBehavious	   behaviousGhost = null;
	private GhostsSettingsEnum ghostSettings  = null;
	private GhostStatusEnum	   status		  = null;

	/**
	 * Constructor
	 */
	public Ghost(int numLevel) {
		super();
		// initialise le comportement du fantôme en fonction du niveau
		behaviousGhost = new GhostBehavious(numLevel);
		// statut de départ NORMAL
		setStatus(GhostStatusEnum.NORMAL);
		// immobile pour commencer
		setDirection(Constants.POINT_ZERO);
	}

	/**
	 * Retourne le setting du fantôme.
	 *
	 * @return
	 */
	public GhostsSettingsEnum getGhostSettings() {
		return ghostSettings;
	}

	/**
	 * Cette fonction est a définir pour chaque fantôme nommé (Blinky, Inky, Clyde &
	 * Pinky)
	 *
	 * @param numLevel
	 */
	public abstract void getInitSpeed(int numLevel);

	/**
	 * Getters / Setters pour le statut du fantôme
	 *
	 * @return
	 */
	public GhostStatusEnum getStatus() {
		return status;
	}

	/**
	 * Retourne true si le fantôme est géré par l'ordinateur
	 *
	 * @return
	 */
	public boolean isComputed() {
		return ghostSettings.isComputed();
	}

	/**
	 * Déplacement du fantôme (géré par l'ordinateur) en fonction de l'emplacement
	 * de Ladybug
	 *
	 * @param ladybugPosBlock
	 */
	public void moveGhostByComputer(Point ladybugPosBlock, ScreenData screenData) {
		// Déplacement en fonction du status du fantôme
		switch (getStatus()) {
		case DYING -> moveToRegenerate(screenData);
		case FLASH, SCARED -> flashOrScaredMoving(ladybugPosBlock, screenData);
		case NORMAL -> normalMoving(ladybugPosBlock, screenData);
		default -> System.out.println("Le statut " + getStatus() + " n'est pas reconnu, le fantôme est immobile !!");
		}
	}

	private void flashOrScaredMoving(Point ladybugPosBlock, ScreenData screenData) {
		if (Utils.convertPointToGraphicUnit(ladybugPosBlock).distance(getPosition()) < 5 * Constants.BLOCK_SIZE)
			moveScared(ladybugPosBlock, screenData);
		else
			moveByDefault(screenData);
	}

	private void normalMoving(Point ladybugPosBlock, ScreenData screenData) {
		if (behaviousGhost.isActive())
			moveByBehaviour(ladybugPosBlock, screenData);
		else
			moveByDefault(screenData);
	}

	/**
	 * Déplacement du fantôme géré par l'utilisateur
	 *
	 * @param ladybugPosBlock
	 * @param screenData
	 * @param ghostRequest
	 */
	public void moveGhostByUser(Point ladybugPosBlock, ScreenData screenData, Point ghostRequest) {
		setMovingRequete(ghostRequest);
		if (changeBlock() && getStatus().equals(GhostStatusEnum.NORMAL)) {
			move(screenData.getDataBlock(Utils.convertPointToBlockUnit(getPosition())));
			getPosition().translate(getDirection().x * getSpeed(), getDirection().y * getSpeed());
		} else {
			moveGhostByComputer(ladybugPosBlock, screenData);
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
				// FANTOME
				ghostActions.setEaten(true);
			} else {
				// Mise à mort de Ladybug !!!
				ghostActions.setHasEatenLadybug(true);
			}
		}
		return ghostActions;
	}

	/**
	 * Affecte la configuration du fantôme
	 */
	public void setGhostSettings(GhostsSettingsEnum ghostSettings) {
		this.ghostSettings = ghostSettings;
	}

	/**
	 * Affecte des paramètres au fantômes qui vient d'être mangé
	 */
	public void setSettingAfterBeEaten(int numLevel) {
		setPosition(Utils.convertPointToGraphicUnit(Utils.convertPointToBlockUnit(getPosition())));
		// � d�placer dans
		setStatus(GhostStatusEnum.DYING);
		setSpeedIndex(SpeedFunction.getInstance().getRealIndexSpeedPlus(numLevel));
	}

	public abstract void setSpeed(int numLevel, int perCent);

	/**
	 * Affecte l'état du statut du fantôme
	 *
	 * @param status
	 */
	public void setStatus(GhostStatusEnum status) {
		this.status = status;
	}

	/**
	 * Déplacement agressif du fantôme
	 *
	 * @param screenData
	 * @param ladybugPosBlock
	 */
	private void moveAgressive(Point ladybugPosBlock, ScreenData screenData) {
		/**
		 * Note : le fantôme peut changer de direction uniquement lorsqu'il rempli le
		 * block
		 */
		if (changeBlock()) {
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
	 * Déplacement d'un fantôme en fonction de son comportement
	 *
	 * (son état est NORMAL)
	 *
	 * @param screenData
	 * @param ladybugPosBlock
	 */
	private void moveByBehaviour(Point ladybugPosBlock, ScreenData screenData) {
		switch (ghostSettings.getBehavious()) {
		case SMART, AGGRESSIVE -> moveAgressive(ladybugPosBlock, screenData);
		default -> moveByDefault(screenData);
		}
	}

	/**
	 * Déplacement du fantôme Renvoi un objet StateMoving A ce jour, cela ne sert à
	 * rien, mais je préfère le laisser pour les prochaines évolutions.
	 */
	// FIXME : c'est une fonction un peu alambiquée en fait; un refacto me semble
	// n�cessaire
	private void moveByDefault(ScreenData screenData) {
		int	  count	   = 0;
		int[] dx	   = new int[4];
		int[] dy	   = new int[4];
		Point posPoint = getPosition();
		if (changeBlock()) {
			ScreenBlock currentScreenBlock = screenData.getDataBlock(Utils.convertPointToBlockUnit(posPoint));
			count = 0;
			if (!currentScreenBlock.isLeft() && getDirection().x != 1) {
				dx[count] = -1;
				dy[count] = 0;
				count++;
			}
			if (!currentScreenBlock.isUp() && getDirection().y != 1) {
				dx[count] = 0;
				dy[count] = -1;
				count++;
			}
			if (!currentScreenBlock.isRight() && getDirection().x != -1) {
				dx[count] = 1;
				dy[count] = 0;
				count++;
			}
			if (!currentScreenBlock.isDown() && getDirection().y != -1) {
				dx[count] = 0;
				dy[count] = 1;
				count++;
			}
			if (count == 0) {
				if (currentScreenBlock.isLeft() && currentScreenBlock.isRight() && currentScreenBlock.isUp()
				        && currentScreenBlock.isDown()) {
					setDirection(Constants.POINT_ZERO);
				} else {
					setDirection(new Point(getDirection().x, -getDirection().y));
				}
			} else {
				count = new Random().nextInt(count);
				if (count > 3) {
					count = 3;
				}
				setDirection(new Point(dx[count], dy[count]));
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
		if (changeBlock()) {
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

	/**
	 * Déplacement du fantôme mangé
	 *
	 * @param data
	 * @param coordinateRevivorGhost
	 */
	private void moveToRegenerate(ScreenData screenData) {
		// Le fant�me est arriv� au limite du block
		if (changeBlock()) {
			// calcul du chemin le plus court :
			List<Point> shorterWay = Dijkstra.getShorterWay(Utils.convertPointToBlockUnit(getPosition()),
			        Utils.convertPointToBlockUnit(screenData.getRevivorGhostPos()), screenData);
			// S'il ne reste plus qu'un bloc � parcourir, le fantôme est arrivé
			if (shorterWay.size() == 1) {
				// Le fantôme est arrivé au point de regénération, il redevient "normal" avec
				// une vitesse "normale" aussi
				// setSpeedIndex(getStartIndexSpeed());
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
}