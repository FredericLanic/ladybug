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
package com.kycox.game.body.ladybug;

import com.kycox.game.constant.game.GameMainConstants;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class LadybugDying {
	private int bip;
	@Setter
	@Getter
	private long agonyDuration = 0;

	public void initBip() {
		bip = 0;
	}

	public void inProgress() {
		bip++;
	}

	public boolean isEnd() {
		return (long) bip * GameMainConstants.PACE >= agonyDuration;
	}

	public boolean isInProgress() {
		return bip > 0;
	}
}
