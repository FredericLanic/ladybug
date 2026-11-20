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
package com.kycox.game.engine.model;

import com.kycox.game.constant.level.LevelRendering;
import com.kycox.game.tools.maths.LitLampMode;
import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * Ce que le jeu décide pour le niveau en cours, en plus du labyrinthe : le mode
 * lampe (cercle de lumière autour des joueurs) et le style de dessin des murs.
 *
 * <p>Étape 4.5 : ces deux choix vivaient dans le labyrinthe ({@code Maze}),
 * alors qu'ils ne décrivent pas sa forme. Ils font partie de l'état partagé du
 * jeu : en réseau (étape 9), l'hôte les décidera pour tous les joueurs. À ne pas
 * confondre avec les préférences propres à chaque poste
 * ({@code LocalPreferences}).</p>
 */
@Component
public class LevelState {
	@Getter
	private boolean litLampMode = false;
	@Getter
	private LevelRendering levelRendering = LevelRendering.getDefaultLevelRendering();

	/**
	 * Tire au hasard le mode lampe du niveau (plus probable dans les niveaux
	 * avancés).
	 */
	public void drawLitLampMode(int numLevel) {
		litLampMode = LitLampMode.isLitLampMode(numLevel);
	}

	/** Touche F6 : allume ou éteint la lampe. */
	public void toggleLitLampMode() {
		litLampMode = !litLampMode;
	}

	/**
	 * Choisit le style de dessin des murs : au hasard pendant une partie, le style
	 * par défaut sur l'écran de présentation.
	 */
	public void drawLevelRendering(boolean isInGame) {
		levelRendering = isInGame ? LevelRendering.getRandomizeLevelRendering()
		        : LevelRendering.getDefaultLevelRendering();
	}
}
