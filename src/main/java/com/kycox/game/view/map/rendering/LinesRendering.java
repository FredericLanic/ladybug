package com.kycox.game.view.map.rendering;

import com.kycox.game.constant.GameMainConstants;
import com.kycox.game.level.ScreenData;
import com.kycox.game.tools.Utils;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.scanner.Constant;

import java.awt.*;

@Component
public class LinesRendering implements Rendering {

	@Override
	public void displayScreenBlockBorders(Graphics2D g2d, ScreenData screenData, int x, int y) {
		var screenBlock = screenData.getViewBlock(Utils.convertGraphicPointToBlockPoint(new Point(x, y)));
		g2d.setStroke(new BasicStroke(2));
		var currentCoord = screenBlock.getCoordinate();
		// affichage de la barre à gauche
		if (screenBlock.isBorderLeft() && !screenBlock.isBorderUp() && !screenBlock.isBorderDown()
				|| currentCoord.x == 0 && currentCoord.y != 0 && currentCoord.y != screenData.getNbrLines() - 1) {
			g2d.drawLine(x, y, x, y + GameMainConstants.BLOCK_SIZE);
		}
		// affichage de la barre en haut
		if (screenBlock.isBorderUp() && !screenBlock.isBorderLeft() && !screenBlock.isBorderRight()
				|| currentCoord.y == 0 && currentCoord.x != 0
				&& currentCoord.x != screenData.getCurrentLevel().getNbrBlocksByLine() - 1) {
			g2d.drawLine(x, y, x + GameMainConstants.BLOCK_SIZE, y);
		}
		// affichage de la barre à droite
		if (screenBlock.isBorderRight() && !screenBlock.isBorderUp() && !screenBlock.isBorderDown()
				|| currentCoord.x == screenData.getCurrentLevel().getNbrBlocksByLine() - 1 && currentCoord.y != 0
				&& currentCoord.y != screenData.getNbrLines() - 1) {
			g2d.drawLine(x + GameMainConstants.BLOCK_SIZE, y, x + GameMainConstants.BLOCK_SIZE, y + GameMainConstants.BLOCK_SIZE);
		}
		// affichage de la barre en bas
		if (screenBlock.isBorderDown() && !screenBlock.isBorderLeft() && !screenBlock.isBorderRight()
				|| currentCoord.y == screenData.getNbrLines() - 1 && currentCoord.x != 0
				&& currentCoord.x != screenData.getCurrentLevel().getNbrBlocksByLine() - 1) {
			g2d.drawLine(x, y + GameMainConstants.BLOCK_SIZE, x + GameMainConstants.BLOCK_SIZE, y + GameMainConstants.BLOCK_SIZE);
		}
		var rayon = GameMainConstants.BLOCK_SIZE;
		// affichage de la courbe gauche vers haut
		if (screenBlock.isBorderLeft() && screenBlock.isBorderUp() || (x==0 && y == 0)) {
			g2d.drawArc(x, y, rayon, rayon, 90, 90);
		}

		if ((screenBlock.isBorderLeft() && y == 0) || (x==0 && y == 14*GameMainConstants.BLOCK_SIZE)) {
			g2d.drawLine(x, y, x, y + GameMainConstants.BLOCK_SIZE / 2);
		}

		if ((screenBlock.isBorderRight() && y == 0)  || (x== 14*GameMainConstants.BLOCK_SIZE && y == 14*GameMainConstants.BLOCK_SIZE)) {
			g2d.drawLine(x + GameMainConstants.BLOCK_SIZE, y, x + GameMainConstants.BLOCK_SIZE, y + GameMainConstants.BLOCK_SIZE / 2);
		}

		if ((screenBlock.isBorderRight() && y == 14 * GameMainConstants.BLOCK_SIZE) || (x==14*GameMainConstants.BLOCK_SIZE && y == 0))  {
			g2d.drawLine(x + GameMainConstants.BLOCK_SIZE, y+ GameMainConstants.BLOCK_SIZE / 2, x + GameMainConstants.BLOCK_SIZE, y + GameMainConstants.BLOCK_SIZE);
		}

		if ((screenBlock.isBorderLeft() && y == 14 * GameMainConstants.BLOCK_SIZE) || (x==0 && y == 0)) {
			g2d.drawLine(x , y+ GameMainConstants.BLOCK_SIZE / 2, x , y + GameMainConstants.BLOCK_SIZE);
		}

		if ((screenBlock.isBorderUp() && x == 0) || (x==14*GameMainConstants.BLOCK_SIZE && y==0)) {
			g2d.drawLine(x, y, x+ GameMainConstants.BLOCK_SIZE / 2, y );
		}

		if ((screenBlock.isBorderDown() && x == 0) || (x==14*GameMainConstants.BLOCK_SIZE && y==14*GameMainConstants.BLOCK_SIZE)) {
			g2d.drawLine(x, y+GameMainConstants.BLOCK_SIZE, x+ GameMainConstants.BLOCK_SIZE / 2, y+GameMainConstants.BLOCK_SIZE);
		}

		if ((screenBlock.isBorderUp() && x == 14*GameMainConstants.BLOCK_SIZE) || (x==0 && y==0)) {
			g2d.drawLine(x+ GameMainConstants.BLOCK_SIZE / 2, y, x+ GameMainConstants.BLOCK_SIZE, y );
		}

		if ((screenBlock.isBorderDown() && x == 14*GameMainConstants.BLOCK_SIZE) || (x==0 && y==14*GameMainConstants.BLOCK_SIZE)) {
			g2d.drawLine(x+ GameMainConstants.BLOCK_SIZE / 2, y+GameMainConstants.BLOCK_SIZE, x+ GameMainConstants.BLOCK_SIZE, y+GameMainConstants.BLOCK_SIZE);
		}

		// affichage de la courbe gauche vers bas
		if (screenBlock.isBorderLeft() && screenBlock.isBorderDown() || (x==0 && y == 14*GameMainConstants.BLOCK_SIZE)) {
			g2d.drawArc(x, y, rayon, rayon, 270, -90);
		}
		// affichage de la courbe haut vers droite
		if (screenBlock.isBorderRight() && screenBlock.isBorderUp() ||(x==14*GameMainConstants.BLOCK_SIZE && y == 0)) {
			g2d.drawArc(x, y, rayon, rayon, 0, 90);
		}
		// affichage de la courbe droite vers bas
		if (screenBlock.isBorderRight() && screenBlock.isBorderDown() ||(x==14*GameMainConstants.BLOCK_SIZE && y == 14*GameMainConstants.BLOCK_SIZE)) {
			g2d.drawArc(x, y, rayon, rayon, 270, 90);
		}

		if (screenBlock.isBorderLeft() && screenBlock.isBorderUp()
				&& currentCoord.x < screenData.getCurrentLevel().getNbrBlocksByLine() - 1
				&& screenData.getViewBlock(new Point(currentCoord.x + 1, currentCoord.y)).isBorderUp()) {
			g2d.drawLine(x + GameMainConstants.BLOCK_SIZE / 2, y, x + GameMainConstants.BLOCK_SIZE, y);
		}
		if (screenBlock.isBorderUp() && currentCoord.x < screenData.getCurrentLevel().getNbrBlocksByLine() - 1
				&& currentCoord.y > 1
				&& screenData.getViewBlock(new Point(currentCoord.x + 1, currentCoord.y - 1)).isBorderLeft()) {
			g2d.drawLine(x + GameMainConstants.BLOCK_SIZE / 2, y, x + GameMainConstants.BLOCK_SIZE, y);
		}
		if (screenBlock.isBorderLeft() && screenBlock.isBorderDown()
				&& currentCoord.x < screenData.getCurrentLevel().getNbrBlocksByLine() - 1
				&& screenData.getViewBlock(new Point(currentCoord.x + 1, currentCoord.y)).isBorderDown()) {
			g2d.drawLine(x + GameMainConstants.BLOCK_SIZE / 2, y + GameMainConstants.BLOCK_SIZE, x + GameMainConstants.BLOCK_SIZE,
					y + GameMainConstants.BLOCK_SIZE);
		}
		if (screenBlock.isBorderDown() && currentCoord.x < screenData.getCurrentLevel().getNbrBlocksByLine() - 1
				&& currentCoord.y < screenData.getNbrLines() - 1
				&& screenData.getViewBlock(new Point(currentCoord.x + 1, currentCoord.y + 1)).isBorderLeft()) {
			g2d.drawLine(x + GameMainConstants.BLOCK_SIZE / 2, y + GameMainConstants.BLOCK_SIZE, x + GameMainConstants.BLOCK_SIZE,
					y + GameMainConstants.BLOCK_SIZE);
		}
		if (screenBlock.isBorderRight() && screenBlock.isBorderUp() && currentCoord.x > 0
				&& screenData.getViewBlock(new Point(currentCoord.x - 1, currentCoord.y)).isBorderUp()) {
			g2d.drawLine(x, y, x + GameMainConstants.BLOCK_SIZE / 2, y);
		}
		if (screenBlock.isBorderUp() && currentCoord.x > 0 && currentCoord.y > 1
				&& screenData.getViewBlock(new Point(currentCoord.x - 1, currentCoord.y - 1)).isBorderRight()) {
			g2d.drawLine(x, y, x + GameMainConstants.BLOCK_SIZE / 2, y);
		}
		var sbTmp = screenData.getViewBlock(new Point(currentCoord.x - 1, currentCoord.y));
		var sbTmp1 = screenData.getViewBlock(new Point(currentCoord.x - 1, currentCoord.y + 1));
		if (screenBlock.isBorderRight() && screenBlock.isBorderDown() && currentCoord.x > 0
				&& (sbTmp.isBorderDown() || sbTmp.isBorderRight())) {
			g2d.drawLine(x, y + GameMainConstants.BLOCK_SIZE, x + GameMainConstants.BLOCK_SIZE / 2, y + GameMainConstants.BLOCK_SIZE);
		}
		if (screenBlock.isBorderDown() && currentCoord.x > 0 && currentCoord.y < screenData.getNbrLines() - 1
				&& (sbTmp.isBorderDown() || sbTmp1.isBorderRight())) {
			g2d.drawLine(x, y + GameMainConstants.BLOCK_SIZE, x + GameMainConstants.BLOCK_SIZE / 2, y + GameMainConstants.BLOCK_SIZE);
		}
		// ********************//
		if (screenBlock.isBorderRight() && screenBlock.isBorderUp() && currentCoord.y < screenData.getNbrLines() - 1
				&& screenData.getViewBlock(new Point(currentCoord.x, currentCoord.y + 1)).isBorderRight()) {
			g2d.drawLine(x + GameMainConstants.BLOCK_SIZE, y + GameMainConstants.BLOCK_SIZE / 2, x + GameMainConstants.BLOCK_SIZE,
					y + GameMainConstants.BLOCK_SIZE);
		}
		if (screenBlock.isBorderRight() && currentCoord.y < screenData.getNbrLines() - 1
				&& currentCoord.x < screenData.getCurrentLevel().getNbrBlocksByLine() - 1
				&& screenData.getViewBlock(new Point(currentCoord.x + 1, currentCoord.y + 1)).isBorderUp()) {
			g2d.drawLine(x + GameMainConstants.BLOCK_SIZE, y + GameMainConstants.BLOCK_SIZE / 2, x + GameMainConstants.BLOCK_SIZE,
					y + GameMainConstants.BLOCK_SIZE);
		}
		if (screenBlock.isBorderLeft() && screenBlock.isBorderUp() && currentCoord.y < screenData.getNbrLines() - 1
				&& screenData.getViewBlock(new Point(currentCoord.x, currentCoord.y + 1)).isBorderLeft()) {
			g2d.drawLine(x, y + GameMainConstants.BLOCK_SIZE / 2, x, y + GameMainConstants.BLOCK_SIZE);
		}
		if (screenBlock.isBorderLeft() && currentCoord.y < screenData.getNbrLines() - 1 && currentCoord.x > 0
				&& screenData.getViewBlock(new Point(currentCoord.x - 1, currentCoord.y + 1)).isBorderUp()) {
			g2d.drawLine(x, y + GameMainConstants.BLOCK_SIZE / 2, x, y + GameMainConstants.BLOCK_SIZE);
		}
		if (screenBlock.isBorderRight() && screenBlock.isBorderDown() && currentCoord.y > 0
				&& screenData.getViewBlock(new Point(currentCoord.x, currentCoord.y - 1)).isBorderRight()) {
			g2d.drawLine(x + GameMainConstants.BLOCK_SIZE, y, x + GameMainConstants.BLOCK_SIZE, y + GameMainConstants.BLOCK_SIZE / 2);
		}
		if (screenBlock.isBorderRight() && currentCoord.y > 0
				&& currentCoord.x < screenData.getCurrentLevel().getNbrBlocksByLine() - 1
				&& screenData.getViewBlock(new Point(currentCoord.x + 1, currentCoord.y - 1)).isBorderDown()) {
			g2d.drawLine(x + GameMainConstants.BLOCK_SIZE, y, x + GameMainConstants.BLOCK_SIZE, y + GameMainConstants.BLOCK_SIZE / 2);
		}
		if (screenBlock.isBorderLeft() && screenBlock.isBorderDown() && currentCoord.y > 0
				&& screenData.getViewBlock(new Point(currentCoord.x, currentCoord.y - 1)).isBorderLeft()) {
			g2d.drawLine(x, y, x, y + GameMainConstants.BLOCK_SIZE / 2);
		}
		if (screenBlock.isBorderLeft() && currentCoord.y > 0 && currentCoord.x > 0
				&& screenData.getViewBlock(new Point(currentCoord.x - 1, currentCoord.y - 1)).isBorderDown()) {
			g2d.drawLine(x, y, x, y + GameMainConstants.BLOCK_SIZE / 2);
		}
	}
}
