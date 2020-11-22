package com.kycox.ladybug.sound;

import java.util.ArrayList;
import java.util.List;

import com.kycox.ladybug.constant.SoundsEnum;
import com.kycox.ladybug.contract.INewSoundsForGameSounds;

import lombok.Getter;

public class NewSounds implements INewSoundsForGameSounds {
	@Getter
	private List<SoundsEnum> lstSoundsEnum = new ArrayList<>();

	public void addDyingGhost(boolean mustBeAdded) {
		if (mustBeAdded) {
			lstSoundsEnum.add(SoundsEnum.GHOST_EATEN);
		}
	}

	public void addGameBeginLevel(boolean mustBeAdded) {
		if (mustBeAdded) {
			lstSoundsEnum.add(SoundsEnum.GAME_BEGIN_LEVEL);
		}
	}

	public void addLadybugEatenAPoint(boolean mustBeAdded) {
		if (mustBeAdded) {
			lstSoundsEnum.add(SoundsEnum.LADYBUG_CHOMP);
		}
	}

	public void addLadybugEatGhost(boolean mustBeAdded) {
		if (mustBeAdded) {
			lstSoundsEnum.add(SoundsEnum.LADYBUG_EAT_GHOST);
		}
	}

	public void addLadybugIsDying(boolean mustBeAdded, boolean startFenerarium) {
		if (mustBeAdded & startFenerarium) {
			lstSoundsEnum.add(SoundsEnum.LADYBUG_IS_DYING);
		}
	}

	public void addNewLife(boolean mustBeAdded) {
		if (mustBeAdded) {
			lstSoundsEnum.add(SoundsEnum.LADYBUG_EXTRA_PAC);
		}
	}

	public void addRegeneratedGhost(boolean mustBeAdded) {
		if (mustBeAdded) {
			lstSoundsEnum.add(SoundsEnum.GHOST_REGENERATE);
		}
	}

	public void addScaredGhost(boolean mustBeAdded) {
		if (mustBeAdded) {
			lstSoundsEnum.add(SoundsEnum.LADYBUG_INTERMISSION);
		}
	}

	public void addSirenSound(boolean mustBeAdded, int percent) {
		if (!mustBeAdded) {
			return;
		}
		if (percent < 40) {
			lstSoundsEnum.add(SoundsEnum.GAME_SIREN_0);
		} else if (percent < 60) {
			lstSoundsEnum.add(SoundsEnum.GAME_SIREN_1);
		} else if (percent < 80) {
			lstSoundsEnum.add(SoundsEnum.GAME_SIREN_2);
		} else {
			lstSoundsEnum.add(SoundsEnum.GAME_SIREN_3);
		}
	}

	public void addTeleport(boolean mustBeAdded) {
		if (mustBeAdded) {
			lstSoundsEnum.add(SoundsEnum.COMMON_TELEPORT);
		}
	}

	public boolean hasSound(SoundsEnum soundsEnum) {
		return lstSoundsEnum.contains(soundsEnum);
	}

	public void initSounds() {
		lstSoundsEnum.removeAll(lstSoundsEnum);
	}
}
