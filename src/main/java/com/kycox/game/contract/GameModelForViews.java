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
package com.kycox.game.contract;

import java.util.Optional;

import com.kycox.game.body.ghost.Ghost;
import com.kycox.game.level.ScreenData;
import com.kycox.game.message.GameMessaging;
import com.kycox.game.score.GroupMessages;
import com.kycox.game.score.Score;

public interface GameModelForViews extends GameCommon {
	GameStatusForGameView getCurrentProgramStatus();

	GameMessaging getGameMessaging();

	Score getGameScore();

	int getGhostLeftLives();

	GroupGhostForGameView getGroupGhosts();

	GroupMessages getGroupMessages();

	int getIncrementScore();

	LadybugForGameView getLadybug();

	int getNbrPlayers();

	int getNbrPointsForNewLife();

	ScreenData getScreenData();

	Optional<Ghost> getUnComputedGhost();

	boolean isAtLeastOneXboxOneConnected();

	boolean isShowHelpForKeys();

	boolean isShowHelpForXboxes();

	boolean isSoundActive();
}