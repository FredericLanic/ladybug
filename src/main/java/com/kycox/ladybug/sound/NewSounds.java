package com.kycox.ladybug.sound;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import com.kycox.ladybug.constant.SoundsEnum;
import com.kycox.ladybug.contract.INewSoundsForGameSounds;

import lombok.Getter;

@Named("NewSounds")
public class NewSounds implements INewSoundsForGameSounds {
	@Getter
	private List<SoundsEnum> lstSoundsEnum = new ArrayList<>();

	public void addDyingGhost(boolean mustBeAdded) {
		addSounds(SoundsEnum.GHOST_EATEN, mustBeAdded);
	}

	public void addGameBeginLevel(boolean mustBeAdded) {
		addSounds(SoundsEnum.GAME_BEGIN_LEVEL, mustBeAdded);
	}

	public void addLadybugEatenAPoint(boolean mustBeAdded) {
		addSounds(SoundsEnum.LADYBUG_CHOMP, mustBeAdded);
	}

	public void addLadybugEatGhost(boolean mustBeAdded) {
		addSounds(SoundsEnum.LADYBUG_EAT_GHOST, mustBeAdded);
	}

	public void addLadybugIsDying(boolean mustBeAdded, boolean startFenerarium) {
		addSounds(SoundsEnum.LADYBUG_IS_DYING, mustBeAdded && startFenerarium);
	}

	public void addNewLife(boolean mustBeAdded) {
		addSounds(SoundsEnum.LADYBUG_EXTRA_PAC, mustBeAdded);
	}

	public void addRegeneratedGhost(boolean mustBeAdded) {
		addSounds(SoundsEnum.GHOST_REGENERATE, mustBeAdded);
	}

	public void addScaredGhost(boolean mustBeAdded) {
		addSounds(SoundsEnum.LADYBUG_INTERMISSION, mustBeAdded);
	}

	public void addSirenSound(boolean mustBeAdded, int percent) {
		if (percent < 40) {
			addSounds(SoundsEnum.GAME_SIREN_0, mustBeAdded);
		} else if (percent < 60) {
			addSounds(SoundsEnum.GAME_SIREN_1, mustBeAdded);
		} else if (percent < 80) {
			addSounds(SoundsEnum.GAME_SIREN_2, mustBeAdded);
		} else {
			addSounds(SoundsEnum.GAME_SIREN_3, mustBeAdded);
		}
	}

	public void addTeleport(boolean mustBeAdded) {
		addSounds(SoundsEnum.COMMON_TELEPORT, mustBeAdded);
	}

	@Override
	public boolean hasSound(SoundsEnum soundsEnum) {
		return lstSoundsEnum.contains(soundsEnum);
	}

	public void initSounds() {
		lstSoundsEnum.clear();
	}

	private void addSounds(SoundsEnum soundsEnum, boolean mustBeAdded) {
		if (mustBeAdded) {
			lstSoundsEnum.add(soundsEnum);
		}
	}
}
