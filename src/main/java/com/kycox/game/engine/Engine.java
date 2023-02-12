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

import com.kycox.game.model.GameModel;
import com.kycox.game.sound.GameSounds;
import com.kycox.game.view.CentralView;
import com.kycox.game.view.down.PageEndView;
import com.kycox.game.view.left.PageLeftView;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class Engine {
	private final CentralView centralView;
	private final GameModel gameModel;
	private final GameSounds gameSounds;
	private final PageEndView pageEndView;
	private final PageLeftView pageLeftView;

	public Engine(GameModel gameModel, GameSounds gameSounds, CentralView centralView, PageEndView pageEndView, PageLeftView pageLeftView) {
		this.gameModel = gameModel;
		this.gameSounds = gameSounds;
		this.centralView = centralView;
		this.pageEndView = pageEndView;
		this.pageLeftView = pageLeftView;
	}

	/**
	 * note : use PAD CONTROLLER PadController padController = new
	 * PadController(gameModel) for joystick
	 */
	@PostConstruct
	public void init() {
		gameModel.addObserver(centralView);
		gameModel.addObserver(gameSounds);
		gameModel.addObserver(pageEndView);
		gameModel.addObserver(pageLeftView);
		// on récupère la longueur du son de la mort de ladybug et on l'affecte
		gameModel.getLadybugDying().setMillisecondLenght(gameSounds.getMillisecondLadybugDeath());
		gameModel.setBeginningMilliseconds(gameSounds.getMillisecondsBeginning());
		gameModel.setTimeEndingMilliseconds(1500);
		gameModel.setEndingLevelMilliseconds(gameSounds.getMillisecondsIntermission());
		centralView.setDurationLadybugNewLife(gameSounds.getMillisecondNewLife());
	}
}
