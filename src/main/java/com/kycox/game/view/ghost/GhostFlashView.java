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
package com.kycox.game.view.ghost;

import java.awt.Image;

import javax.swing.Timer;

import com.kycox.game.constant.contract.GhostForView;
import com.kycox.game.tools.timer.TimerView;

public class GhostFlashView implements TimerView {
	private static final GhostFlashView ghostFlashView = new GhostFlashView();

	public static GhostFlashView getInstance() {
		return ghostFlashView;
	}

	private boolean showScared;

    private GhostFlashView() {
        Timer timer = createTimer();
		timer.start();
	}

	@Override
	public void doAction() {
		showScared = !showScared;
	}

	public Image getImage(GhostForView ghostForView) {
		if (showScared) {
			return GhostScaredView.getInstance().getImage(ghostForView);
		}
		return GhostDefaultView.getInstance().getImage(ghostForView);
	}
}
