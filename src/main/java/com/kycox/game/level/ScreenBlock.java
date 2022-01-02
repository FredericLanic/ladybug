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
package com.kycox.game.level;

import java.awt.Point;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kycox.game.constant.Constants;

import lombok.Getter;
import lombok.Setter;

/**
 * Class ScreenBlock correspond à une case dans le ScreenData
 *
 */
public class ScreenBlock implements Cloneable {
	private static final int DOWN			= 8;
	private static final int EATEN_POINT	= 512;
	private static final int GHOST_REVIVER	= 64;
	private static final int LEFT			= 1;
	private static final Log logger			= LogFactory.getLog(ScreenBlock.class);
	private static final int MEGA_POINT		= 32;
	private static final int NOT_ACCESSIBLE	= 128;
	private static final int POINT			= 16;
	private static final int RIGHT			= 4;
	private static final int TELEPORTATION	= 256;
	private static final int UP				= 2;
	@Getter
	@Setter
	private int				 content;
	@Getter
	@Setter
	private Point			 coordinate		= Constants.POINT_ZERO;

	public ScreenBlock(int content) {
		this.content = content;
	}

	public void addBorderDown() {
		content |= DOWN;
	}

	public void addBorderLeft() {
		content |= LEFT;
	}

	public void addBorderRight() {
		content |= RIGHT;
	}

	public void addBorderUp() {
		content |= UP;
	}

	public void addEatenPoint() {
		content |= EATEN_POINT;
	}

	public void addGhostReviver() {
		content |= GHOST_REVIVER;
	}

	public void addMegaPoint() {
		content |= MEGA_POINT;
	}

	public void addTeleportation() {
		content |= TELEPORTATION;
	}

	@Override
	public ScreenBlock clone() {
		ScreenBlock o = null;
		try {
			o = (ScreenBlock) super.clone();
		} catch (CloneNotSupportedException cnse) {
			logger.error(cnse.getMessage());
		}
		return o;
	}

	public Point getDirectionInDeadEnd() {
		if (!isBorderLeft())
			return Constants.POINT_LEFT;
		if (!isBorderRight())
			return Constants.POINT_RIGHT;
		if (!isBorderUp())
			return Constants.POINT_UP;
		if (!isBorderDown())
			return Constants.POINT_DOWN;
		return Constants.POINT_ZERO;
	}

	public boolean isBlocked() {
		return getDirectionInDeadEnd().equals(Constants.POINT_ZERO);
	}

	public boolean isBorderDown() {
		return (content & DOWN) != 0;
	}

	public boolean isBorderLeft() {
		return (content & LEFT) != 0;
	}

	public boolean isBorderRight() {
		return (content & RIGHT) != 0;
	}

	/**
	 * Bordure en haut
	 *
	 * @return
	 */
	public boolean isBorderUp() {
		return (content & UP) != 0;
	}

	public boolean isDeadEnd() {
		int nbrBorders = 0;
		if (isBorderLeft())
			nbrBorders++;
		if (isBorderRight())
			nbrBorders++;
		if (isBorderUp())
			nbrBorders++;
		if (isBorderDown())
			nbrBorders++;
		return nbrBorders == 3;
	}

	public boolean isEatenPoint() {
		return (content & EATEN_POINT) != 0;
	}

	public boolean isGhostReviver() {
		return (content & GHOST_REVIVER) != 0;
	}

	public boolean isMegaPoint() {
		return (content & MEGA_POINT) != 0;
	}

	public boolean isNotAccessible() {
		return (content & NOT_ACCESSIBLE) != 0;
	}

	public boolean isPoint() {
		return (content & POINT) != 0;
	}

	public boolean isTeleportation() {
		return (content & TELEPORTATION) != 0;
	}

	public void removeBorderDown() {
		content &= ~(DOWN);
	}

	public void removeBorderLeft() {
		content &= ~(LEFT);
	}

	public void removeBorderRight() {
		content &= ~(RIGHT);
	}

	public void removeBorderUp() {
		content &= ~(UP);
	}

	public void removePoint() {
		content &= ~(POINT | MEGA_POINT);
	}
}
