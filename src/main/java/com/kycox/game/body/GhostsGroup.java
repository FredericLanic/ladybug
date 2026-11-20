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
package com.kycox.game.body;

import com.kycox.game.tools.Position;
import com.kycox.game.body.ghost.Ghost;
import com.kycox.game.body.ghost.GhostColor;
import com.kycox.game.body.ladybug.Ladybug;
import com.kycox.game.constant.game.GameMainConstants;
import com.kycox.game.constant.ghost.GhostStatus;
import com.kycox.game.engine.maze.Maze;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Setter
@Getter
public class GhostsGroup {
	private List<Ghost> ghosts;

	public boolean eatLadybug() {
		return ghosts.stream().anyMatch(g -> g.getGhostActions().isEatLadybug());
	}

	public int getLeftLives() {
		return ghosts.stream().filter(g -> !g.isComputed())
		        .findFirst()
		        .map(Ghost::getLeftLifes)
		        .orElse(0);
	}

	public int getNbrEatenGhosts() {
		return (int) ghosts.stream().filter(g -> g.getGhostActions().isEatenByLadybug()).count();
	}

	public boolean hasDyingGhost() {
		return (ghosts.stream().anyMatch(g -> g.getStatus() == GhostStatus.DYING));
	}

	public boolean hasRegeneratedGhost() {
		return (ghosts.stream().anyMatch(g -> g.getStatus() == GhostStatus.REGENERATED));
	}

	public boolean hasScaredOrFlashedGhost() {
		return ghosts.stream().anyMatch(g -> g.getStatus() == GhostStatus.SCARED || g.getStatus() == GhostStatus.FLASH);
	}

	public boolean hasTeleportedGhosts() {
		return ghosts.stream().anyMatch(g -> g.getGhostActions().isToBeTeleported());
	}

	public void manageNewLifeUnComputedGhost() {
		ghosts.stream().filter(g -> !g.isComputed()).forEach(Ghost::manageNewLife);
	}

	/**
	 * En jeu : l'ordinateur déplace ses fantômes, chaque fantôme humain suit les
	 * ordres de sa boîte aux lettres (étape 5.2).
	 */
	public void move(Ladybug ladybug, Maze maze) {
		ghosts.stream().filter(Ghost::isComputed).forEach(g -> g.moveComputedGhost(ladybug, maze));
		ghosts.stream().filter(g -> !g.isComputed())
		        .forEach(g -> g.moveGhostUser(ladybug, maze, g.getPlayerInput().requested()));
	}

	/**
	 * Présentation et question « reprendre l'ancien niveau ? » : les fantômes
	 * humains bougent sans suivre d'ordre, mais leurs ordres sont gardés pour la
	 * partie.
	 */
	public void moveWithoutPlayerRequests(Ladybug ladybug, Maze maze) {
		ghosts.stream().filter(Ghost::isComputed).forEach(g -> g.moveComputedGhost(ladybug, maze));
		ghosts.stream().filter(g -> !g.isComputed())
		        .forEach(g -> g.moveGhostUser(ladybug, maze, GameMainConstants.POINT_ZERO));
	}

	/** Les fantômes joués par des humains, dans l'ordre du groupe. */
	public List<Ghost> getHumanGhosts() {
		return ghosts.stream().filter(g -> !g.isComputed()).toList();
	}

	/** Tous les fantômes sont joués par l'ordinateur (mode un joueur). */
	public void setAllComputed() {
		ghosts.forEach(ghost -> ghost.setComputed(true));
	}

	/**
	 * Ce fantôme précis est joué par un humain (étape 5.4 : un joueur, un fantôme,
	 * et non plus tous les fantômes d'une couleur). Sa boîte aux lettres est
	 * vidée : il ne garde aucun ordre d'une ancienne partie (étape 5.2).
	 */
	public void setHumanPlayer(Ghost ghost) {
		ghost.setComputed(false);
		ghost.getPlayerInput().reset();
	}

	/**
	 * Le fantôme donné au deuxième joueur à partir de la couleur tirée au hasard :
	 * le premier de cette couleur (même résultat qu'avant 5.4 pour une même
	 * graine) ; s'il n'y en a pas (XML sans cette couleur), un autre fantôme, au
	 * lieu d'aucun.
	 */
	public Optional<Ghost> chooseHumanGhost(GhostColor drawnColor) {
		if (ghosts.isEmpty()) {
			return Optional.empty();
		}
		return ghosts.stream().filter(ghost -> ghost.getColor() == drawnColor).findFirst()
		        .or(() -> Optional.of(ghosts.get(drawnColor.ordinal() % ghosts.size())));
	}

	public void setActions(Ladybug ladybug, Maze maze) {
		ghosts.forEach(g -> g.setGhostActions(ladybug, maze));
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
		ghosts.stream().filter(g -> g.getStatus() == GhostStatus.SCARED).forEach(g -> g.setStatus(GhostStatus.FLASH));
	}

	private void setGhostPositionAfterTeleportation(Maze maze) {
		ghosts.stream().filter(g -> g.getGhostActions().isToBeTeleported()).forEach(g -> g.teleport(maze));
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
		ghosts.stream().filter(g -> g.getStatus() == GhostStatus.AUTOGENERATED)
		        .forEach(g -> g.setStatus(GhostStatus.REGENERATED));
	}

	public void setInitSpeedsComputedGhost(int numLevel) {
		ghosts.stream().filter(Ghost::isComputed).forEach(g -> g.setInitSpeed(numLevel));
	}

	public void setInitSpeedsForPresentation(int numLevel) {
		ghosts.forEach(g -> g.setInitSpeed(numLevel));
	}

	public void setLeftLifes(int numLeftLives) {
		ghosts.forEach(g -> g.setLeftLifes(numLeftLives));
	}

	public void setNumLevel(int numLevel) {
		ghosts.forEach(g -> g.setNumLevel(numLevel));
	}

	public void setPositionToRevivorGhostPoint(Maze maze) {
		ghosts.forEach(g -> g.setPosition(maze.getRevivorGhostPos()));
	}

	private void setSpeed(int numLevel, int perCent) {
		ghosts.forEach(g -> g.setSpeed(numLevel, perCent));
	}

	public void setStartLevel(int numLevel, Maze maze) {
		setStatus(GhostStatus.NORMAL);
		setInitSpeedsComputedGhost(numLevel);
		setPositionToRevivorGhostPoint(maze);
	}

	public void setStatus(GhostStatus status) {
		ghosts.forEach(g -> g.setStatus(status));
	}

	public void updateSeetings(int numLevel, Maze maze) {
		setGhostSettingAfterLadybugContact(numLevel);
		// modifier la vitesse des fantôme en cours de partie
		setSpeed(numLevel, maze.getPercentageEatenPoint());
		// Etat des fantômes de REGENERATING à NORMAL
		setGhostStatusAfterRegenerated();
		// Passage de l'état TOBEREGENERATED to REGENERATED
		setGhostStatusAfterToBeRegenerated();
		// Téléportation du fantôme
		setGhostPositionAfterTeleportation(maze);
	}

	/**
	 * Règle C50 (étape 5.3) : un fantôme humain qui n'a plus de vie est rendu à
	 * l'ordinateur, tant qu'il reste d'autres fantômes humains en vie. Le dernier
	 * n'est pas rendu : c'est lui qui termine la partie des fantômes.
	 */
	public void releaseEliminatedHumanGhosts() {
		var humans = getHumanGhosts();
		var eliminated = humans.stream().filter(g -> g.getLeftLifes() <= 0).toList();
		if (!eliminated.isEmpty() && eliminated.size() < humans.size()) {
			eliminated.forEach(ghost -> ghost.setComputed(true));
		}
	}

	/**
	 * Vrai quand tous les fantômes joués par des humains sont éliminés (règle C50).
	 * Avec un seul fantôme humain : dès qu'il n'a plus de vie, comme avant 5.3.
	 */
	public boolean allHumanGhostsEliminated() {
		var humans = getHumanGhosts();
		return !humans.isEmpty() && humans.stream().allMatch(g -> g.getLeftLifes() <= 0);
	}

	public void changeGhostCamouflage() {
		ghosts.forEach(ghost -> ghost.setCamouflage(!ghost.isCamouflage()));
	}
}
