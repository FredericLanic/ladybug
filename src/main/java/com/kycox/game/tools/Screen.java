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
package com.kycox.game.tools;

import com.kycox.game.constant.Constants;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class Screen {
	final Toolkit defaultToolKit = Toolkit.getDefaultToolkit();

	public double getBorderHeight() {
		return (getScreenHeight() - 15 * Constants.BLOCK_SIZE) / 2;
	}

	public double getBorderWidth() {
		return (getScreenWidth() - 15 * Constants.BLOCK_SIZE) / 2;
	}

	public double getEdgeGameSide() {
		return 15.0 * Constants.BLOCK_SIZE;
	}

	public double getScreenHeight() {
		return defaultToolKit.getScreenSize().getHeight();
	}

	public double getScreenWidth() {
		return defaultToolKit.getScreenSize().getWidth();
	}
}
