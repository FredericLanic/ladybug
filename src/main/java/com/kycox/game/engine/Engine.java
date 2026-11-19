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

import com.kycox.game.constant.sounds.Sounds;
import com.kycox.game.engine.sound.SoundDurations;
import com.kycox.game.engine.view.CentralView;
import jakarta.annotation.PostConstruct;
import com.kycox.game.launcher.GuiComponent;

/**
 * Réglages de l'interface liés à la durée des sons.
 *
 * <p>Depuis l'étape 3.5.3b, le modèle règle lui-même ses attentes (début de
 * niveau, intermède, agonie) avec {@link SoundDurations} ; il ne reste ici que la
 * couleur du labyrinthe après une vie gagnée, qui dure le temps de son son.
 */
@GuiComponent
public class Engine {
	private final CentralView centralView;
	private final SoundDurations soundDurations;

	public Engine(SoundDurations soundDurations, CentralView centralView) {
		this.soundDurations = soundDurations;
		this.centralView = centralView;
	}

	@PostConstruct
	public void init() {
		centralView.setDurationLadybugNewLife(soundDurations.of(Sounds.LADYBUG_EXTRA_PAC));
	}
}
