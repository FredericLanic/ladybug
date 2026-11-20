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
package com.kycox.game.engine.view.left;

import com.kycox.game.body.ghost.Ghost;
import com.kycox.game.constant.contract.MainGraphicStructure;
import com.kycox.game.engine.model.EventGameModel;
import com.kycox.game.snapshot.GameSnapshot;
import com.kycox.game.snapshot.GhostSnapshot;
import com.kycox.game.snapshot.LadybugSnapshot;
import org.springframework.context.ApplicationListener;
import com.kycox.game.launcher.GuiComponent;
import com.kycox.game.preferences.LocalPreferences;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

@GuiComponent
public class PageLeftView extends JPanel implements ApplicationListener<EventGameModel>, MainGraphicStructure {
	/** La photo du dernier battement (4.6). */
	private transient GameSnapshot snapshot;
	/** Réglages de ce poste : le mode debug (4.5). */
	private final transient LocalPreferences localPreferences;

	public PageLeftView(LocalPreferences localPreferences) {
		this.localPreferences = localPreferences;
	}

	@Override
	public void paintComponent(Graphics g) {
		// le fond noir est toujours peint, même avant le premier battement : sinon la
		// zone restait blanche tant que rien ne la redessinait (4.1)
		setFocusable(false);
		setBackground(Color.BLACK);
		super.paintComponent(g);
		if (snapshot != null && localPreferences.isDebugMode()) {
			draw(g);
		}
	}

	private void draw(Graphics g) {
		var g2d = (Graphics2D) g;
		drawLadybugInfos(g2d, snapshot.ladybug());
		drawGhostsInfos(g2d, snapshot.ghosts(), snapshot.ladybug());
	}

	private void drawGhostsInfos(Graphics2D g2d, java.util.List<GhostSnapshot> ghosts, LadybugSnapshot ladybug) {
		AtomicInteger y = new AtomicInteger(250);

		ghosts.forEach(
			ghost -> {
				switch (ghost.color()) {
					case RED -> g2d.setColor(Color.RED);
					case PINK -> g2d.setColor(Color.PINK);
					case ORANGE -> g2d.setColor(Color.ORANGE);
					case CYAN -> g2d.setColor(Color.CYAN);
				}

				g2d.drawString(ghost.name(), 10  , y.getAndAdd(20));
				g2d.setColor(Color.WHITE);
				g2d.drawString("position: " + ghost.position() + "", 50  , y.getAndAdd(20));
				g2d.drawString("status: " + ghost.status() + "", 50  , y.getAndAdd(20));

				if (ghost.speedIndex() >= ladybug.speedIndex()) {
					g2d.setColor(Color.RED);
				} else if (ghost.speedIndex() == ladybug.speedIndex() - 1) {
					g2d.setColor(Color.ORANGE);
				} else if (ghost.speedIndex() == ladybug.speedIndex() - 2) {
					g2d.setColor(Color.YELLOW);
				}
				g2d.drawString("speed index: " + ghost.speedIndex() + "", 50  , y.getAndAdd(20));

				g2d.setColor(Color.WHITE);
				if (ghost.moveStyle() == Ghost.MoveStyle.MOVE_TO) {
					g2d.setColor(Color.RED);
				}
				g2d.drawString("move style: " + ghost.moveStyle() + "", 50  , y.getAndAdd(20));
			}
		);
	}

	private void drawLadybugInfos(Graphics2D g2d, LadybugSnapshot ladybug) {
		int y = 10;

		g2d.setColor(Color.WHITE);
		g2d.drawString(ladybug.name(), 10, y+=20);
		g2d.drawString("position: " + ladybug.position() + "", 50, y+=20);
		g2d.drawString("view direction: " + ladybug.viewDirection() + "", 50, y+=20);
		g2d.drawString("status: " + ladybug.status() + "", 50, y+=20);
		g2d.drawString("speed index: " + ladybug.speedIndex() + "",50, y+=20);
	}

	@Override
	public void setPreferredSize(Dimension preferredSize) {
		super.setPreferredSize(preferredSize);
	}

	@Override
	public void onApplicationEvent(EventGameModel event) {
		snapshot = event.getSnapshot();
		repaint();
	}
}