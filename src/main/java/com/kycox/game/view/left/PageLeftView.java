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
package com.kycox.game.view.left;

import com.kycox.game.body.ghost.Ghost;
import com.kycox.game.contract.GameModelForViews;
import com.kycox.game.contract.GroupGhostForGameView;
import com.kycox.game.contract.LadybugForGameView;
import com.kycox.game.contract.MainGraphicStructure;
import com.kycox.game.model.EventGameModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class PageLeftView extends JPanel implements ApplicationListener<EventGameModel>, MainGraphicStructure {
	private static final Log logger = LogFactory.getLog(PageLeftView.class);
	private transient GameModelForViews gameModelForViews;

	@Override
	public void paintComponent(Graphics g) {
		if (gameModelForViews != null) {
			setFocusable(false);
			setBackground(Color.BLACK);
			super.paintComponent(g);
			if (gameModelForViews.isDebugMode()) {
				draw(g);
			}
		}
	}

	private void draw(Graphics g) {
		var g2d = (Graphics2D) g;
		drawLadybugInfos(g2d, gameModelForViews.getLadybug());
		drawGhostsInfos(g2d, gameModelForViews.getGroupGhosts(), gameModelForViews.getLadybug());
	}

	private void drawGhostsInfos(Graphics2D g2d, GroupGhostForGameView groupGhost, LadybugForGameView ladybug) {
		AtomicInteger y = new AtomicInteger(250);

		groupGhost.getGhosts().stream().forEach(
			ghost -> {
				switch (ghost.getColor()) {
					case RED -> g2d.setColor(Color.RED);
					case PINK -> g2d.setColor(Color.PINK);
					case ORANGE -> g2d.setColor(Color.ORANGE);
					case CYAN -> g2d.setColor(Color.CYAN);
				}

				g2d.drawString(ghost.getName(), 10  , y.getAndAdd(20));
				g2d.setColor(Color.WHITE);
				g2d.drawString("position: " + ghost.getPosition() + "", 50  , y.getAndAdd(20));
				g2d.drawString("status: " + ghost.getStatus() + "", 50  , y.getAndAdd(20));

				if (ghost.getSpeedIndex() >= ladybug.getSpeedIndex()) {
					g2d.setColor(Color.RED);
				} else if (ghost.getSpeedIndex() == ladybug.getSpeedIndex() - 1) {
					g2d.setColor(Color.ORANGE);
				} else if (ghost.getSpeedIndex() == ladybug.getSpeedIndex() - 2) {
					g2d.setColor(Color.YELLOW);
				}
				g2d.drawString("speed index: " + ghost.getSpeedIndex() + "", 50  , y.getAndAdd(20));

				g2d.setColor(Color.WHITE);
				if (ghost.getMoveStyle() == Ghost.MoveStyle.MOVE_TO) {
					g2d.setColor(Color.RED);
				}
				g2d.drawString("move style: " + ghost.getMoveStyle() + "", 50  , y.getAndAdd(20));
			}
		);
	}

	private void drawLadybugInfos(Graphics2D g2d, LadybugForGameView ladybug) {
		int y = 10;

		g2d.setColor(Color.WHITE);
		g2d.drawString(ladybug.getName(), 10, y+=20);
		g2d.drawString("position: " + ladybug.getPosition() + "", 50, y+=20);
		g2d.drawString("view direction: " + ladybug.getViewDirection() + "", 50, y+=20);
		g2d.drawString("status: " + ladybug.getStatus() + "", 50, y+=20);
		g2d.drawString("speed index: " + ladybug.getSpeedIndex() + "",50, y+=20);
	}

	@Override
	public void setPreferredSize(Dimension preferredSize) {
		super.setPreferredSize(preferredSize);
	}

	@Override
	public void onApplicationEvent(EventGameModel event) {
		Object obj = event.getGameModel();
		if (obj instanceof GameModelForViews eventGameModelForViews) {
			gameModelForViews = eventGameModelForViews;
			repaint();
		}
	}
}