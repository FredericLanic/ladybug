package com.kycox.game.engine.view.map.rendering;

import com.kycox.game.constant.level.LevelRendering;
import com.kycox.game.constant.level.LevelRendering;
import com.kycox.game.engine.view.map.MazeOutline;
import com.kycox.game.snapshot.MazeSnapshot;
import com.kycox.game.launcher.GuiComponent;

import java.awt.*;
import java.util.EnumMap;

@GuiComponent
public class DisplayRendering {

	private final EnumMap<LevelRendering, Rendering> renderings = new EnumMap<>(LevelRendering.class);

	private final MazeOutline mazeOutline;

	public DisplayRendering(MazeOutline mazeOutline, LinesRendering linesRendering, TilesRendering492 tilesRendering492, TilesRendering584 tilesRendering584, TilesRenderingRedLines tilesRenderingRedLines) {
		this.mazeOutline = mazeOutline;
		renderings.put(LevelRendering.LINES_RENDERING, linesRendering);
		renderings.put(LevelRendering.TILES_RENDERING_492, tilesRendering492);
		renderings.put(LevelRendering.TILES_RENDERING_584, tilesRendering584);
		renderings.put(LevelRendering.TILES_RENDERING_REDLINES, tilesRenderingRedLines);
	}

	public void display(Graphics2D g2d, MazeSnapshot maze, LevelRendering levelRendering, int x, int y) {
		renderings.get(levelRendering).displayScreenBlockBorders(g2d, mazeOutline.of(maze), x, y);
	}

}
