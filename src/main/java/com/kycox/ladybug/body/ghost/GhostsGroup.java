/**
  Copyright (C) 2019  Frédéric Lanic frederic.lanic@outlook.fr

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
package com.kycox.ladybug.body.ghost;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.kycox.ladybug.action.ghost.GhostsGroupActions;
import com.kycox.ladybug.body.ladybug.Ladybug;
import com.kycox.ladybug.constant.ghost.GhostStatusEnum;
import com.kycox.ladybug.level.ScreenData;
import com.kycox.ladybug.tools.Utils;

import lombok.Getter;
import lombok.Setter;

/**
 * Gestion des actions sur l'ensemble des fantômes
 *
 * Singleton
 *
 */
public class GhostsGroup {
	@Getter
	@Setter
	private List<Ghost> lstGhosts = new ArrayList<>();

	/**
	 * Retourne le fantôme qui est géré par un utilisateur
	 *
	 * ou null si aucun fantôme n'est géré par un utilisateur
	 */
	public Ghost getGhostNotComputed() {
		Optional<Ghost> ghost = lstGhosts.stream().filter(g -> !g.isComputed()).findFirst();
		if (ghost.isPresent())
			return ghost.get();
		return null;
	}

	/**
	 * Retourne vrai si le fantôme n'a plus de vie
	 */
	public boolean GhostUserIsDead() {
		long nbrDeadKeyGhosts = lstGhosts.stream().filter(g -> !g.isComputed()).filter(g -> (g.getLeftLifes() <= 0))
		        .count();
		return (nbrDeadKeyGhosts >= 1);
	}

	public boolean hasDyingGhost() {
		return (lstGhosts.stream().filter(g -> GhostStatusEnum.isDying().test(g)).count() > 0);
	}

	public boolean hasRegeneratedGhost() {
		return (lstGhosts.stream().filter(g -> GhostStatusEnum.isRegenerating().test(g)).count() > 0);
	}

	public boolean hasScaredGhost() {
		return ((lstGhosts.stream().filter(g -> GhostStatusEnum.isScared().test(g)).count()
		        + lstGhosts.stream().filter(g -> GhostStatusEnum.isFlashing().test(g)).count()) > 0);
	}

	/**
	 * Déplacement des fantômes
	 *
	 * @param screenData   : la map
	 * @param ladybug      : ladybug
	 * @param ghostRequest : requête du fantôme non computé
	 */
	public void moveGhosts(ScreenData screenData, Ladybug ladybug, Point ghostRequest) {
		// Déplacement des fantômes gérés par l'ordinateur
		lstGhosts.stream().filter(Ghost::isComputed)
		        .forEach(g -> g.moveGhostByComputer(Utils.convertPointToBlockUnit(ladybug.getPosition()), screenData));
		// Déplacement des fantômes gérés par l'humain
		lstGhosts.stream().filter(g -> !g.isComputed()).forEach(
		        g -> g.moveGhostByUser(Utils.convertPointToBlockUnit(ladybug.getPosition()), screenData, ghostRequest));
	}

	/**
	 * Vérification des fantômes
	 *
	 * @param inGame
	 * @param numLevel
	 */
	public GhostsGroupActions setAllGhostsActions(Ladybug ladybug) {
		GhostsGroupActions ghostsActions = new GhostsGroupActions();
		lstGhosts.stream().forEach(g -> ghostsActions.addGhostAction(g.setGhostActions(ladybug)));
		return ghostsActions;
	}

	/**
	 * Informe tous les fantômes (non mangés) qu'ils ont peur ou pas
	 *
	 * @param active
	 */
	public void setFear(boolean fear) {
		if (fear) {
			lstGhosts.stream().filter(g -> !g.getStatus().equals(GhostStatusEnum.DYING))
			        .forEach(g -> g.setStatus(GhostStatusEnum.SCARED));
		} else {
			lstGhosts.stream().filter(g -> !g.getStatus().equals(GhostStatusEnum.DYING))
			        .forEach(g -> g.setStatus(GhostStatusEnum.NORMAL));
		}
	}

	/**
	 * Tous les fantômes doivent clignotter
	 */
	public void setGhostFlashActive() {
		lstGhosts.stream().filter(GhostStatusEnum.isScared()).forEach(g -> g.setStatus(GhostStatusEnum.FLASH));
	}

	/**
	 * Status des fantômes de REGENERATING à NORMAL
	 */
	public void setGhostStatusAfterRegeneration() {
		lstGhosts.stream().filter(g -> GhostStatusEnum.isRegenerating().test(g))
		        .forEach(g -> g.setStatus(GhostStatusEnum.NORMAL));
	}

	/**
	 * Initialise les vitesses des fantômes
	 *
	 * @param numLevel
	 */
	public void setInitSpeeds(int numLevel) {
		lstGhosts.stream().forEach(g -> g.setInitSpeed(numLevel));
	}

	/**
	 * Initialise le nombre de vie des fantômes
	 *
	 * @param numLeftLives
	 */
	public void setLeftLifes(int numLeftLives) {
		lstGhosts.stream().forEach(g -> g.setLeftLifes(numLeftLives));
	}

	public void setNumLevel(int numLevel) {
		lstGhosts.stream().forEach(g -> g.setNumLevel(numLevel));
	}

	/**
	 * Initialise les positions des fantômes
	 */
	public void setPositions(ScreenData screenData) {
		lstGhosts.stream().forEach(g -> g.setPosition(screenData.getRevivorGhostPos()));
	}

	/**
	 * Affecte la vitesse de chaque fantôme au cours du niveau en cours
	 */
	public void setSpeed(int numLevel, int perCent) {
		lstGhosts.stream().forEach(g -> g.setSpeed(numLevel, perCent));
	}

	/**
	 * Initialise les fantômes lors du début de niveau
	 *
	 * @param numLevel
	 */
	public void setStartLevel(int numLevel, ScreenData screenData) {
		setStatus(GhostStatusEnum.NORMAL);
		setInitSpeeds(numLevel);
		setPositions(screenData);
	}

	/**
	 * Affecte le même status à tous les fantômes
	 *
	 * @param status
	 */
	public void setStatus(GhostStatusEnum status) {
		lstGhosts.stream().forEach(g -> g.setStatus(status));
	}
}
