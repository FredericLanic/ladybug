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
package com.kycox.game.constant.ghost;

import java.awt.Image;
import java.util.Arrays;
import java.util.List;

import com.kycox.game.constant.pictures.PicturesEnum;

import lombok.Getter;

/**
 * Setting spécifique des fantômes : Blinky, Clyde, Inky, Pinky
 *
 * @author kycox
 *
 */
public enum GhostsColorEnum {
	BLUE(PicturesEnum.GHOST_COLOR_BLUE, true), ORANGE(PicturesEnum.GHOST_COLOR_ORANGE, true),
	PINK(PicturesEnum.GHOST_COLOR_PINK, true), RED(PicturesEnum.GHOST_COLOR_RED, true);

	/**
	 * Initialise la configuration isComputed pour tous les settings des fantômes
	 *
	 * @param isComputed
	 */
	public static void initIsComputed(boolean isComputed) {
		List<GhostsColorEnum> lstEnum = Arrays.asList(GhostsColorEnum.class.getEnumConstants());
		lstEnum.stream().forEach(e -> e.isComputed = isComputed);
	}

	@Getter
	private PicturesEnum colorImage;
	private boolean		 isComputed;

	private GhostsColorEnum(PicturesEnum colorImage, boolean isComputed) {
		this.colorImage	= colorImage;
		this.isComputed	= isComputed;
	}

	public Image getImage() {
		return colorImage.getImage();
	}

	public boolean isComputed() {
		return isComputed;
	}

	public void setComputed(boolean isComputed) {
		this.isComputed = isComputed;
	}
}
