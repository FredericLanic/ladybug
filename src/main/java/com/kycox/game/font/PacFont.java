package com.kycox.game.font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kycox.game.view.GameView;

import lombok.Getter;

public enum PacFont {
	PACFONT("fonts/font-pacfont/pac-font.ttf");

	@Getter
	private Font	  defaultFont = new Font("CrackMan", Font.BOLD, 14);
	private final Log logger	  = LogFactory.getLog(GameView.class);

	private PacFont(String filePath) {
		try {
			InputStream is = this.getClass().getResourceAsStream("/fonts/font-pacfont/pac-font.ttf");
			defaultFont	= Font.createFont(Font.TRUETYPE_FONT, is);
			defaultFont	= defaultFont.deriveFont((float) 38.0);
		} catch (FontFormatException | IOException e) {
			logger.error(e);
		}
	}
}