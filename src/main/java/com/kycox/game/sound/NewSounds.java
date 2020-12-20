package com.kycox.game.sound;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import com.kycox.game.constant.Sounds;
import com.kycox.game.contract.INewSoundsForGameSounds;

import lombok.Getter;

@Named("NewSounds")
public class NewSounds implements INewSoundsForGameSounds {
	@Getter
	private List<Sounds> sounds = new ArrayList<>();

	public void addDyingGhost(boolean mustBeAdded) {
		addSounds(Sounds.GHOST_EATEN, mustBeAdded);
	}

	public void addGameBeginLevel(boolean mustBeAdded) {
		addSounds(Sounds.GAME_BEGIN_LEVEL, mustBeAdded);
	}

	public void addLadybugEatenAPoint(boolean mustBeAdded) {
		addSounds(Sounds.LADYBUG_CHOMP, mustBeAdded);
	}

	public void addLadybugEatGhost(boolean mustBeAdded) {
		addSounds(Sounds.LADYBUG_EAT_GHOST, mustBeAdded);
	}

	public void addLadybugIsDying(boolean mustBeAdded, boolean startFenerarium) {
//		if (mustBeAdded && startFenerarium) {
//			sounds.clear();
//			System.out.println("Start sound ladybug is dying");
//		}
		addSounds(Sounds.LADYBUG_IS_DYING, mustBeAdded && startFenerarium);
	}

	public void addNewLife(boolean mustBeAdded) {
		addSounds(Sounds.LADYBUG_EXTRA_PAC, mustBeAdded);
	}

	public void addRegeneratedGhost(boolean mustBeAdded) {
		addSounds(Sounds.GHOST_REGENERATE, mustBeAdded);
	}

	public void addScaredGhost(boolean mustBeAdded) {
		addSounds(Sounds.LADYBUG_INTERMISSION, mustBeAdded);
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

	private void addSounds(Sounds soundsEnum, boolean mustBeAdded) {
		if (mustBeAdded) {
			sounds.add(soundsEnum);
		}
	}
}
