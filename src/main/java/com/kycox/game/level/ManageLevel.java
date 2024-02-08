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
package com.kycox.game.level;

import com.kycox.game.constant.contract.LevelStructure;
import com.kycox.game.tools.Utils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class ManageLevel {
	private final List<LevelStructure> levels;

	public ManageLevel(List<LevelStructure> levels) {
		this.levels = levels;
	}

	public LevelStructure getLevel(int i) {
		var num = i;
		if (num > levels.size()) {
			num = 1 + Utils.generateRandomInt(levels.size());
		}
		return levels.get(num - 1);
	}
}
