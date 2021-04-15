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
package com.kycox.game.constant.pictures;

import java.awt.Image;
import java.awt.Point;

import com.kycox.game.constant.Constants;
import com.kycox.game.constant.ghost.image.GhostHeadbandImages;

public class PirateHeadband {
	public static Image getPirateHeadband(Point viewDirection) {
		if (Constants.POINT_LEFT.equals(viewDirection))
			return GhostHeadbandImages.HEADBAND_LEFT.getImage();
		if (Constants.POINT_RIGHT.equals(viewDirection))
			return GhostHeadbandImages.HEADBAND_RIGHT.getImage();
		if (Constants.POINT_DOWN.equals(viewDirection))
			return GhostHeadbandImages.HEADBAND_DOWN.getImage();
		return GhostHeadbandImages.HEADBAND_UP.getImage();
	}

	private PirateHeadband() {
	}
}
