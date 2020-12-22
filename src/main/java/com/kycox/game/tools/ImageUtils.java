package com.kycox.game.tools;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public final class ImageUtils {
	public static Image appendImages(Image image1, Image image2) {
		BufferedImage bufferedImage	= new BufferedImage(image1.getWidth(null), image1.getHeight(null),
		        BufferedImage.TYPE_INT_ARGB);
		Graphics2D	  graphics2D	= bufferedImage.createGraphics();
		graphics2D.drawImage(image1, 0, 0, null);
		graphics2D.drawImage(image2, 0, 0, null);
		return bufferedImage;
	}

	// https://www.baeldung.com/java-resize-image
	public static Image resizeImage(Image originalImage, int targetWidth, int targetHeight) {
		BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D	  graphics2D   = resizedImage.createGraphics();
		graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
		graphics2D.dispose();
		return resizedImage;
	}

	// Code récupéré à
	// https://www.developpez.net/forums/d1194588/general-developpement/algorithme-mathematiques/traitement-d-images/pivoter-image-d-angle-java/
	// FIXME : nécessite un refacto
	public static Image rotateImage(Image imageIn, int angle) {
		BufferedImage bufferedImageIn = new BufferedImage(imageIn.getWidth(null), imageIn.getHeight(null),
		        BufferedImage.TYPE_INT_ARGB);
		Graphics2D	  graphics2D	  = bufferedImageIn.createGraphics();
		graphics2D.drawImage(imageIn, 0, 0, null);
		// cosinus et sinus de l'angle de rotation
		double cos		= Math.cos(Math.PI * angle / 180.);
		double sin		= Math.sin(Math.PI * angle / 180.);
		int	   widthIn	= bufferedImageIn.getWidth();
		int	   heightIn	= bufferedImageIn.getHeight();
		// Centre de l'image de départ
		int	centerXIn = widthIn / 2;
		int	centerYIn = heightIn / 2;
		// dimension de la nouvelle image
		int	widthOut  = (int) (widthIn * Math.abs(cos) + heightIn * Math.abs(sin));
		int	heightOut = (int) (widthIn * Math.abs(sin) + heightIn * Math.abs(cos));
		// Centre de la nouvelle image
		int	centerXOut = widthOut / 2;
		int	centerYOut = heightOut / 2;
		// Création de la nouvelle image
		BufferedImage newImage = new BufferedImage(widthOut, heightOut, BufferedImage.TYPE_INT_ARGB);
		// pour chaque pixel de la nouvelle image
		for (int yOut = 0; yOut < heightOut; yOut++) {
			for (int xOut = 0; xOut < widthOut; xOut++) {
				// pixel correspondant dans l'image de départ
				int	xIn	= (int) Math.round(centerXIn + (xOut - centerXOut) * cos - (yOut - centerYOut) * sin);
				int	yIn	= (int) Math.round(centerYIn + (xOut - centerXOut) * sin + (yOut - centerYOut) * cos);
				// copie de la valeur
				if (xIn >= 0 && xIn < widthIn && yIn >= 0 && yIn < heightIn)
					newImage.setRGB(xOut, yOut, bufferedImageIn.getRGB(xIn, yIn));
			}
		}
		try {
			ImageIO.write(newImage, "png", new File("rotation.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newImage;
	}

	private ImageUtils() {
	}
}