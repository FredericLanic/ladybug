package com.kycox.game.font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import lombok.Getter;

public enum PacFont {
	PACFONT("fonts/font-pacfont/pac-font.ttf");
	
	@Getter
	private Font defaultFont;
	
	private PacFont(String filePath) {

		     File fis;
			try {
				fis = Paths.get(PacFont.class.getClassLoader().getResource(filePath).toURI()).toFile();
				defaultFont = Font.createFont(Font.TRUETYPE_FONT, fis);
				defaultFont = defaultFont.deriveFont((float)38.0);
			} catch (URISyntaxException | FontFormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 		     

	}
}
