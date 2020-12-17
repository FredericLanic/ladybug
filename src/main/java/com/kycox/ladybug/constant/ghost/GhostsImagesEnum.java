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
package com.kycox.ladybug.constant.ghost;

import java.awt.Image;
import java.util.Arrays;
import java.util.List;

import com.kycox.ladybug.constant.pictures.PicturesEnum;

/**
 * Setting spécifique des fantômes : Blinky, Clyde, Inky, Pinky
 *
 * @author kycox
 *
 */
public enum GhostsImagesEnum {
    // cf : https://fr.wikipedia.org/wiki/Pac-Man
    // Blinky (AGGRESIVE - rouge) attaque directement Pac Man. Il suit Pac-Man comme
    // son ombre.
    // Pinky (SMART - rose) a tendance à se mettre en embuscade. Elle vise l'endroit
    // où va se trouver Pac-Man.
    // Inky (CAPRICIOUS - bleu) est capricieux. De temps en temps, il part dans la
    // direction opposée de Pac-Man.
    // Clyde (INDIFFERENT - orange) feint l'indifférence. De temps en temps, il
    // choisit une direction au hasard (qui peut être celle de Pac-Man).
	BLUE(PicturesEnum.GHOST_BLUE_RIGHT_EYES, PicturesEnum.GHOST_BLUE_LEFT_EYES, PicturesEnum.GHOST_BLUE_UP_EYES,
	        PicturesEnum.GHOST_BLUE_DOWN_EYES, true),
	ORANGE(PicturesEnum.GHOST_ORANGE_RIGHT_EYES, PicturesEnum.GHOST_ORANGE_LEFT_EYES, PicturesEnum.GHOST_ORANGE_UP_EYES,
	        PicturesEnum.GHOST_ORANGE_DOWN_EYES, true),
	PINK(PicturesEnum.GHOST_PINK_RIGHT_EYES, PicturesEnum.GHOST_PINK_LEFT_EYES, PicturesEnum.GHOST_PINK_UP_EYES,
	        PicturesEnum.GHOST_PINK_DOWN_EYES, true),
	RED(PicturesEnum.GHOST_RED_RIGHT_EYES, PicturesEnum.GHOST_RED_LEFT_EYES, PicturesEnum.GHOST_RED_UP_EYES,
	        PicturesEnum.GHOST_RED_DOWN_EYES, true);

	/**
	 * Initialise la configuration isComputed pour tous les settings des fantômes
	 *
	 * @param isComputed
	 */
	public static void initIsComputed(boolean isComputed) {
		List<GhostsImagesEnum> lstEnum = Arrays.asList(GhostsImagesEnum.class.getEnumConstants());
		lstEnum.stream().forEach(e -> e.isComputed = isComputed);
	}

	private PicturesEnum downEyesImg;
	private boolean		 isComputed;
	private PicturesEnum leftEyesImg;
	private PicturesEnum rightEyesImg;
	private PicturesEnum upEyesImg;

	/**
	 * Constructeur
	 *
	 * @param ghostBehavior
	 * @param leftEyesImg
	 * @param rightEyesImg
	 */
	private GhostsImagesEnum(PicturesEnum leftEyesImg, PicturesEnum rightEyesImg, PicturesEnum upEyesImg,
	        PicturesEnum downEyesImg, boolean isComputed) {
		this.leftEyesImg  = leftEyesImg;
		this.rightEyesImg = rightEyesImg;
		this.upEyesImg	  = upEyesImg;
		this.downEyesImg  = downEyesImg;
		this.isComputed	  = isComputed;
	}

	/**
	 * Retourne l'image du fantômes les yeux en bas
	 *
	 * @return
	 */
	public Image getGhostDownEyesImg() {
		return downEyesImg.getImg();
	}

	/**
	 * Retourne l'image du fantômes les yeux à gauche
	 *
	 * @return
	 */
	public Image getGhostLeftEyesImg() {
		return leftEyesImg.getImg();
	}

	/**
	 * Retourne l'image du fantômes les yeux à droite
	 *
	 * @return
	 */
	public Image getGhostRightEyesImg() {
		return rightEyesImg.getImg();
	}

	/**
	 * Retourne l'image du fantômes les yeux en haut
	 *
	 * @return
	 */
	public Image getGhostUpEyesImg() {
		return upEyesImg.getImg();
	}

	public boolean isComputed() {
		return isComputed;
	}

	public void setComputed(boolean isComputed) {
		this.isComputed = isComputed;
	}
}
