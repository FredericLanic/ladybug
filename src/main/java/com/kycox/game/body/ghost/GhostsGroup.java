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
import com.kycox.game.contract.GroupGhostForGameView;
import com.kycox.game.level.ScreenData;

import lombok.Getter;
import lombok.Setter;

public class GhostsGroup implements GroupGhostForGameView {
	@Getter
	@Setter
	private List<Ghost> ghosts;

	public boolean eatLadybug() {
		return ghosts.stream().filter(g -> g.getGhostActions().isEatLadybug()).count() > 0;
	}

	public int getLeftLives() {
		var optGhost = ghosts.stream().filter(g -> !g.isComputed()).findFirst();
		if (optGhost.isPresent()) {
			return optGhost.get().getLeftLifes();
		}
		return 0;
	}

	public int getNbrEatenGhosts() {
		return (int) ghosts.stream().filter(g -> g.getGhostActions().isEatenByLadybug()).count();
	}

	public boolean hasDyingGhost() {
		return (ghosts.stream().filter(g -> g.getStatus() == GhostStatus.DYING).count() > 0);
	}

	public boolean hasGhostUser() {
		return ghosts.stream().filter(g -> !g.isComputed()).findFirst().isPresent();
	}

	public boolean hasRegeneratedGhost() {
		return (ghosts.stream().filter(g -> g.getStatus() == GhostStatus.REGENERATED).count() > 0);
	}

	public boolean hasScaredOrFlashedGhost() {
		return ((ghosts.stream().filter(g -> g.getStatus() == GhostStatus.SCARED).count()
		        + ghosts.stream().filter(g -> g.getStatus() == GhostStatus.FLASH).count()) > 0);
	}

	public boolean hasTeleportedGhosts() {
		var nbrTeleportedGhosts = ghosts.stream().filter(g -> g.getGhostActions().isToBeTeleported()).count();
		return (nbrTeleportedGhosts > 0);
	}

	public void initializePositions(ScreenData screenData) {
		ghosts.stream().forEach(g -> g.setPosition(screenData.getRevivorGhostPos()));
	}

	public void manageNewLife() {
		ghosts.stream().filter(g -> !g.isComputed()).forEach(Ghost::manageNewLife);
	}

	public void move(Ladybug ladybug, ScreenData screenData, Point ghostRequest) {
		ghosts.stream().filter(Ghost::isComputed).forEach(g -> g.moveComputedGhost(ladybug, screenData));
		ghosts.stream().filter(g -> !g.isComputed())
		        .forEach(g -> g.moveGhostUser(ladybug, screenData, ghostRequest));
	}

	public void setActions(Ladybug ladybug, ScreenData screenData) {
		ghosts.stream().forEach(g -> g.setGhostActions(ladybug, screenData));
	}

	public void setFear(boolean fear) {
		if (fear) {
			ghosts.stream().filter(g -> g.getStatus() != GhostStatus.DYING)
			        .forEach(g -> g.setStatus(GhostStatus.SCARED));
		} else {
			ghosts.stream().filter(g -> g.getStatus() != GhostStatus.DYING)
			        .forEach(g -> g.setStatus(GhostStatus.NORMAL));
		}
	}

	public void setFlashActive() {
		ghosts.stream().filter(g -> g.getStatus() == GhostStatus.SCARED)
		        .forEach(g -> g.setStatus(GhostStatus.FLASH));
	}

	private void setGhostPositionAfterTeleportation(ScreenData screenData) {
		ghosts.stream().filter(g -> g.getGhostActions().isToBeTeleported()).forEach(g -> g.teleport(screenData));
	}

	private void setGhostSettingAfterLadybugContact(int numLevel) {
		// Mis à jour du statut
		ghosts.stream().filter(g -> g.getGhostActions().isEatenByLadybug())
		        .forEach(g -> g.setSettingAfterBeEaten(numLevel));
		ghosts.stream().filter(g -> g.getGhostActions().isEatenByLadybug()).forEach(Ghost::lostsALife);
	}

	private void setGhostStatusAfterRegenerated() {
		ghosts.stream().filter(g -> g.getStatus() == GhostStatus.REGENERATED)
		        .forEach(g -> g.setStatus(GhostStatus.NORMAL));
	}

	private void setGhostStatusAfterToBeRegenerated() {
		ghosts.stream().filter(g -> g.getStatus() == GhostStatus.TOBEREGERENATED)
		        .forEach(g -> g.setStatus(GhostStatus.REGENERATED));
	}

	public void setInitSpeeds(int numLevel) {
		ghosts.stream().forEach(g -> g.setInitSpeed(numLevel));
	}

	public void setLeftLifes(int numLeftLives) {
		ghosts.stream().forEach(g -> g.setLeftLifes(numLeftLives));
	}

	public void setNumLevel(int numLevel) {
		ghosts.stream().forEach(g -> g.setNumLevel(numLevel));
	}

	private void setSpeed(int numLevel, int perCent) {
		ghosts.stream().forEach(g -> g.setSpeed(numLevel, perCent));
	}

	public void setStartLevel(int numLevel, ScreenData screenData) {
		setStatus(GhostStatus.NORMAL);
		setInitSpeeds(numLevel);
		initializePositions(screenData);
	}

	public void setStatus(GhostStatus status) {
		ghosts.stream().forEach(g -> g.setStatus(status));
	}

	public void updateSeetings(int numLevel, ScreenData screenData) {
		setGhostSettingAfterLadybugContact(numLevel);
		// modifier la vitesse des fantôme en cours de partie
		setSpeed(numLevel, screenData.getPercentageEatenPoint());
		// Etat des fantômes de REGENERATING à NORMAL
		setGhostStatusAfterRegenerated();
		// Passage de l'état TOBEREGENERATED to REGENERATED
		setGhostStatusAfterToBeRegenerated();
		// Téléportation du fantôme
		setGhostPositionAfterTeleportation(screenData);
	}

	/**
	 * Retourne vrai si le fantôme n'a plus de vie
	 */
	public boolean userGhostHasNoLife() {
		var nbrDeadUserGhost = ghosts.stream().filter(g -> !g.isComputed()).filter(g -> (g.getLeftLifes() <= 0))
		        .count();
		return (nbrDeadUserGhost > 0);
	}
}
