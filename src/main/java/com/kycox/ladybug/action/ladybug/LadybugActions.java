/**
  Copyright (C) 2019  Frédéric Lanic frederic.lanic@outlook.fr

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
package com.kycox.ladybug.action.ladybug;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.level.ScreenBlock;
import com.kycox.ladybug.score.GroupIncrementScores;
import com.kycox.ladybug.tools.Utils;

import lombok.Data;

@Data
public class LadybugActions {
	private ScreenBlock	currentScreenBlock;
	private boolean		eatenAMegaPoint;
	private boolean		eatenAPoint;

	public void addIncrementScores(GroupIncrementScores groupIncrementScores) {
		if (isEatenAMegaPoint()) {
			groupIncrementScores.add(Utils.convertPointToGraphicUnit(currentScreenBlock.getCoordinate()),
			        Integer.toString(Constants.SCORE_MEGA_POINT));
		}
	}

	public void init() {
		currentScreenBlock = null;
		eatenAMegaPoint	   = false;
		eatenAPoint		   = false;
	}
}
