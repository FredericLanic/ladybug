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
package com.kycox.game.sound;

import com.kycox.game.constant.Sounds;
import com.kycox.game.contract.NewSoundsForGameSounds;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NewSounds implements NewSoundsForGameSounds {
	@Getter
	private final List<Sounds> sounds = new ArrayList<>();

	public void addDyingGhost(boolean mustBeAdded) {
		addSounds(Sounds.GHOST_EATEN, mustBeAdded);
	}

	public void addGameBeginLevel(boolean mustBeAdded) {
		addSounds(Sounds.GAME_BEGIN_LEVEL, mustBeAdded);
	}

	public void addIntermission(boolean mustBeAdded) {
		addSounds(Sounds.LADYBUG_INTERMISSION, mustBeAdded);
	}

	public void addLadybugEatenAPoint(boolean mustBeAdded) {
		addSounds(Sounds.LADYBUG_CHOMP, mustBeAdded);
	}

	public void addLadybugEatGhost(boolean mustBeAdded) {
		addSounds(Sounds.LADYBUG_EAT_GHOST, mustBeAdded);
	}

	public void addLadybugIsDying(boolean mustBeAdded, boolean startFenerarium) {
		addSounds(Sounds.LADYBUG_IS_DYING, mustBeAdded && startFenerarium);
	}

	public void addLaybugEatenFruit(boolean mustBeAdded) {
		addSounds(Sounds.LADYBUG_EAT_FRUIT, mustBeAdded);
	}

	public void addNewLife(boolean mustBeAdded) {
		addSounds(Sounds.LADYBUG_EXTRA_PAC, mustBeAdded);
	}

	public void addRegeneratedGhost(boolean mustBeAdded) {
		addSounds(Sounds.GHOST_REGENERATE, mustBeAdded);
	}

	public void addScaredGhost(boolean mustBeAdded) {
		addSounds(Sounds.GHOST_SCARED, mustBeAdded);
	}

	public void addSirenSound(boolean mustBeAdded, int percent) {
		if (percent < 40) {
			addSounds(Sounds.GAME_SIREN_0, mustBeAdded);
		} else if (percent < 60) {
			addSounds(Sounds.GAME_SIREN_1, mustBeAdded);
		} else if (percent < 80) {
			addSounds(Sounds.GAME_SIREN_2, mustBeAdded);
		} else {
			addSounds(Sounds.GAME_SIREN_3, mustBeAdded);
		}
	}

	private void addSounds(Sounds soundsEnum, boolean mustBeAdded) {
		if (mustBeAdded) {
			sounds.add(soundsEnum);
		}
	}

	public void addTeleport(boolean mustBeAdded) {
		addSounds(Sounds.COMMON_TELEPORT, mustBeAdded);
	}

	@Override
	public boolean hasSound(Sounds soundsEnum) {
		return sounds.contains(soundsEnum);
	}

	public void initSounds() {
		sounds.clear();
	}
}
