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
package com.kycox.game.tools;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public final class ImageUtils {
	public static Image appendImages(Image image1, Image image2) {
		var bufferedImage = new BufferedImage(image1.getWidth(null), image1.getHeight(null),
		        BufferedImage.TYPE_INT_ARGB);
		var graphics2D = bufferedImage.createGraphics();
		graphics2D.drawImage(image1, 0, 0, null);
		graphics2D.drawImage(image2, 0, 0, null);
		return bufferedImage;
	}

	// https://www.baeldung.com/java-resize-image
	public static Image resizeImage(Image originalImage, int targetWidth, int targetHeight) {
		var resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
		var graphics2D = resizedImage.createGraphics();
		graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
		graphics2D.dispose();
		return resizedImage;
	}

	// Code récupéré à
	// https://www.developpez.net/forums/d1194588/general-developpement/algorithme-mathematiques/traitement-d-images/pivoter-image-d-angle-java/
	// FIXME : nécessite un refacto
	public static Image rotateImage(Image imageIn, int angle) {
		var bufferedImageIn = new BufferedImage(imageIn.getWidth(null), imageIn.getHeight(null),
		        BufferedImage.TYPE_INT_ARGB);
		var graphics2D = bufferedImageIn.createGraphics();
		graphics2D.drawImage(imageIn, 0, 0, null);
		// cosinus et sinus de l'angle de rotation
		var cos = Math.cos(Math.PI * angle / 180.);
		var sin = Math.sin(Math.PI * angle / 180.);
		var widthIn = bufferedImageIn.getWidth();
		var heightIn = bufferedImageIn.getHeight();
		// Centre de l'image de départ
		var centerXIn = widthIn / 2;
		var centerYIn = heightIn / 2;
		// dimension de la nouvelle image
		var widthOut = (int) (widthIn * Math.abs(cos) + heightIn * Math.abs(sin));
		var heightOut = (int) (widthIn * Math.abs(sin) + heightIn * Math.abs(cos));
		// Centre de la nouvelle image
		var centerXOut = widthOut / 2;
		var centerYOut = heightOut / 2;
		// Création de la nouvelle image
		var newImage = new BufferedImage(widthOut, heightOut, BufferedImage.TYPE_INT_ARGB);
		// pour chaque pixel de la nouvelle image
		for (var yOut = 0; yOut < heightOut; yOut++) {
			for (var xOut = 0; xOut < widthOut; xOut++) {
				// pixel correspondant dans l'image de départ
				var xIn = (int) Math.round(centerXIn + (xOut - centerXOut) * cos - (yOut - centerYOut) * sin);
				var yIn = (int) Math.round(centerYIn + (xOut - centerXOut) * sin + (yOut - centerYOut) * cos);
				// copie de la valeur
				if (xIn >= 0 && xIn < widthIn && yIn >= 0 && yIn < heightIn) {
					newImage.setRGB(xOut, yOut, bufferedImageIn.getRGB(xIn, yIn));
				}
			}
		}
		return newImage;
	}

	private ImageUtils() {
	}
}