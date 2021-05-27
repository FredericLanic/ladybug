package com.kycox.game.font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import lombok.Getter;

public enum GameFont {
	PACFONT("/fonts/font-pacfont/pac-font.ttf");

	@Getter
	private Font defaultFont = new Font("CrackMan", Font.BOLD, 14);
	private final Log logger = LogFactory.getLog(GameFont.class);

	private GameFont(String filePath) {
		try {
			InputStream is = this.getClass().getResourceAsStream(filePath);
			defaultFont = Font.createFont(Font.TRUETYPE_FONT, is);
			defaultFont = defaultFont.deriveFont((float) 38.0);
		} catch (FontFormatException | IOException e) {
			logger.error(e);
		}
	}
}