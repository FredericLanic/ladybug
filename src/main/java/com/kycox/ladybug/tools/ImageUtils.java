package com.kycox.ladybug.tools;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public final class ImageUtils {
	
	private ImageUtils() {
		
	}
	
	public static Image appendImages(Image image1, Image image2) {
		BufferedImage bufferedImage = new BufferedImage(image1.getWidth(null), image1.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bufferedImage.createGraphics();
		g2.drawImage(image1, 0, 0, null);
		g2.drawImage(image2, 0, 0, null);
		return bufferedImage;
	}
}
 