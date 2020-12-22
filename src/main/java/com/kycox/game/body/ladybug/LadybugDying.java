package com.kycox.game.body.ladybug;

import javax.inject.Named;

import com.kycox.game.constant.Constants;

import lombok.Getter;
import lombok.Setter;

@Named("LadybugDeath")
public class LadybugDying {
	private int	 bip;
	@Setter
	@Getter
	private long millisecondLenght = 0;

	public void initBip() {
		bip = 0;
	}

	public void inProgress() {
		bip++;
	}

	public boolean isEnd() {
		return bip * Constants.PACE >= millisecondLenght;
	}

	public boolean isInPogress() {
		return bip > 0;
	}
}
