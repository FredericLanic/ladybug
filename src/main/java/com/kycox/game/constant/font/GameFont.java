package com.kycox.game.constant.font;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;

public enum GameFont {
	PACFONT("/fonts/font-pacfont/pac-font.ttf");

	@Getter
	private Font defaultFont = new Font("CrackMan", Font.BOLD, 14);
	private final Logger logger = LoggerFactory.getLogger(GameFont.class);

	GameFont(String filePath) {
		try(var is = this.getClass().getResourceAsStream(filePath)) {
			defaultFont = Font.createFont(Font.TRUETYPE_FONT, is);
			defaultFont = defaultFont.deriveFont((float) 38.0);
		} catch (FontFormatException | IOException e) {
			logger.error(String.valueOf(e));
		}
	}
}