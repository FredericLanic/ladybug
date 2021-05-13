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
package com.kycox.game.body.ghost;

import java.awt.Point;
import java.util.List;

import com.kycox.game.body.ladybug.Ladybug;
import com.kycox.game.constant.ghost.GhostStatus;
import com.kycox.game.contract.IGroupGhostForGameView;
import com.kycox.game.level.ScreenData;

import lombok.Getter;
import lombok.Setter;

public class GhostsGroup implements IGroupGhostForGameView {
	@Getter
	@Setter
	private List<Ghost> lstGhosts;

	public boolean eatLadybug() {
		return lstGhosts.stream().filter(g -> g.getGhostActions().isEatLadybug()).count() > 0;
	}

	public int getLeftLives() {
		if (hasGhostUser()) {
			return 0;
		}
		return lstGhosts.stream().filter(g -> !g.isComputed()).findFirst().get().getLeftLifes();
	}

	public int getNbrEatenGhost() {
		return (int) lstGhosts.stream().filter(g -> g.getGhostActions().isEatenByLadybug()).count();
	}

	public boolean hasDyingGhost() {
		return (lstGhosts.stream().filter(g -> g.getStatus() == GhostStatus.DYING).count() > 0);
	}

	public boolean hasGhostUser() {
		return lstGhosts.stream().filter(g -> !g.isComputed()).findFirst().isPresent();
	}

	public boolean hasRegeneratedGhost() {
		return (lstGhosts.stream().filter(g -> g.getStatus() == GhostStatus.REGENERATING).count() > 0);
	}

	public boolean hasScaredOrFlashedGhost() {
		return ((lstGhosts.stream().filter(g -> g.getStatus() == GhostStatus.SCARED).count()
		        + lstGhosts.stream().filter(g -> g.getStatus() == GhostStatus.FLASH).count()) > 0);
	}

	public void initializePositions(ScreenData screenData) {
		lstGhosts.stream().forEach(g -> g.setPosition(screenData.getRevivorGhostPos()));
	}

	public void manageNewLife() {
		lstGhosts.stream().filter(g -> !g.isComputed()).forEach(Ghost::manageNewLife);
	}

	public void move(Ladybug ladybug, ScreenData screenData, Point ghostRequest) {
		lstGhosts.stream().filter(Ghost::isComputed).forEach(g -> g.moveComputedGhost(ladybug, screenData));
		lstGhosts.stream().filter(g -> !g.isComputed())
		        .forEach(g -> g.moveGhostUser(ladybug, screenData, ghostRequest));
	}

	public void setActions(Ladybug ladybug) {
		lstGhosts.stream().forEach(g -> g.setGhostActions(ladybug));
	}

	public void setFear(boolean fear) {
		if (fear) {
			lstGhosts.stream().filter(g -> g.getStatus() != GhostStatus.DYING)
			        .forEach(g -> g.setStatus(GhostStatus.SCARED));
		} else {
			lstGhosts.stream().filter(g -> g.getStatus() != GhostStatus.DYING)
			        .forEach(g -> g.setStatus(GhostStatus.NORMAL));
		}
	}

	public void setFlashActive() {
		lstGhosts.stream().filter(g -> g.getStatus() == GhostStatus.SCARED)
		        .forEach(g -> g.setStatus(GhostStatus.FLASH));
	}

	private void setGhostSettingAfterLadybugContact(int numLevel) {
		// Mis à jour du statut
		lstGhosts.stream().filter(g -> g.getGhostActions().isEatenByLadybug())
		        .forEach(g -> g.setSettingAfterBeEaten(numLevel));
		lstGhosts.stream().filter(g -> g.getGhostActions().isEatenByLadybug()).forEach(Ghost::lostsALife);
	}

	private void setGhostStatusAfterRegeneration() {
		lstGhosts.stream().filter(g -> g.getStatus() == GhostStatus.REGENERATING)
		        .forEach(g -> g.setStatus(GhostStatus.NORMAL));
	}

	public void setInitSpeeds(int numLevel) {
		lstGhosts.stream().forEach(g -> g.setInitSpeed(numLevel));
	}

	public void setLeftLifes(int numLeftLives) {
		lstGhosts.stream().forEach(g -> g.setLeftLifes(numLeftLives));
	}

	public void setNumLevel(int numLevel) {
		lstGhosts.stream().forEach(g -> g.setNumLevel(numLevel));
	}

	private void setSpeed(int numLevel, int perCent) {
		lstGhosts.stream().forEach(g -> g.setSpeed(numLevel, perCent));
	}

	public void setStartLevel(int numLevel, ScreenData screenData) {
		setStatus(GhostStatus.NORMAL);
		setInitSpeeds(numLevel);
		initializePositions(screenData);
	}

	public void setStatus(GhostStatus status) {
		lstGhosts.stream().forEach(g -> g.setStatus(status));
	}

	public void updateSeetings(int numLevel, int percentageEatenPoints) {
		setGhostSettingAfterLadybugContact(numLevel);
		// modifier la vitesse des fantôme en cours de partie
		setSpeed(numLevel, percentageEatenPoints);
		// Etat des fantômes de REGENERATING à NORMAL
		setGhostStatusAfterRegeneration();
	}

	/**
	 * Retourne vrai si le fantôme n'a plus de vie
	 */
	public boolean userGhostHasLife() {
		long nbrDeadUserGhost = lstGhosts.stream().filter(g -> !g.isComputed()).filter(g -> (g.getLeftLifes() <= 0))
		        .count();
		return (nbrDeadUserGhost > 0);
	}
}
