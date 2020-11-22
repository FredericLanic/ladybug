/**
  Copyright (C) 2020  Frédéric Lanic frederic.lanic@outlook.fr

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
package com.kycox.ladybug.contract;

import com.kycox.ladybug.body.ghost.GhostsGroup;
import com.kycox.ladybug.body.ladybug.Ladybug;
import com.kycox.ladybug.constant.ladybug.KinematicLadybugDeath;
import com.kycox.ladybug.level.ScreenData;
import com.kycox.ladybug.model.GameStatus;
import com.kycox.ladybug.score.GameScore;
import com.kycox.ladybug.score.GroupIncrementScores;

public interface IGameModelForGameView {
	public GameScore getGameScore();

	public GameStatus getGameStatus();

	public GhostsGroup getGroupGhosts();

	public GroupIncrementScores getGroupIncrementScores();

	public KinematicLadybugDeath getKinematicLadybugDeath();

	public Ladybug getLadybug();

	public int getNbrPlayers();

	public ScreenData getScreenData();

	public boolean isBeginNewLevel();

	// FIXME : hmmm, c'est pas interdit ?
	public void setBeginNewLevel(boolean beginNewLevel);
}
