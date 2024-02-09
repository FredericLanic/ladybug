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
package com.kycox.game.engine;

import com.kycox.game.engine.model.GameModel;
import com.kycox.game.engine.sound.GameSounds;
import com.kycox.game.engine.view.CentralView;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class Engine {
	private final CentralView centralView;
	private final GameModel gameModel;
	private final GameSounds gameSounds;

	public Engine(GameModel gameModel, GameSounds gameSounds, CentralView centralView) {
		this.gameModel = gameModel;
		this.gameSounds = gameSounds;
		this.centralView = centralView;
	}

	@PostConstruct
	public void init() {
		// on récupère la longueur du son de la mort de ladybug et on l'affecte
		gameModel.getLadybugDying().setAgonyDuration(gameSounds.getLadybugAgonyDuration());
		gameModel.setBeginningDuration(gameSounds.getBeginningDuration());
		gameModel.setTimeEndingMilliseconds(1500);
		gameModel.setEndingLevelMilliseconds(gameSounds.getIntermissionDuration());
		centralView.setDurationLadybugNewLife(gameSounds.getNewLifeFunDuration());
	}
}
