package com.kycox.game.view.map.rendering;

import com.kycox.game.constant.level.LevelRendering;
import com.kycox.game.level.ScreenData;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.EnumMap;

@Component
public class DisplayRendering {

	private final EnumMap<LevelRendering, Rendering> renderings = new EnumMap<>(LevelRendering.class);

	public DisplayRendering(LinesRendering linesRendering, TilesRendering492 tilesRendering492, TilesRendering584 tilesRendering584) {
		renderings.put(LevelRendering.LINES_RENDERING, linesRendering);
		renderings.put(LevelRendering.TILES_RENDERING_492, tilesRendering492);
		renderings.put(LevelRendering.TILES_RENDERING_584, tilesRendering584);
	}

	public void display(Graphics2D g2d, ScreenData screenData, int x, int y) {
		renderings.get(screenData.getLevelRendering()).displayScreenBlockBorders(g2d, screenData, x, y);
	}

}
