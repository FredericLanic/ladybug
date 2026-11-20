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
package com.kycox.game.snapshot;

import com.kycox.game.constant.game.GameStatus;
import com.kycox.game.constant.level.LevelRendering;
import com.kycox.game.constant.sounds.Sounds;

import java.util.List;
import java.util.Optional;

/**
 * Une photo figée de la partie, prise à la fin de chaque battement : tout ce
 * que les vues, les sons et le journal des tests ont besoin de savoir, et rien
 * d'autre.
 *
 * <p>Étape 4.6 (pattern Snapshot) : les lecteurs ne voient plus le modèle
 * vivant, seulement cette photo, que personne ne peut modifier (des
 * {@code record} et des listes non modifiables). C'est aussi ce que l'hôte
 * enverra aux autres postes en réseau (étape 9). Voir
 * {@code doc/patterns/snapshot.md}.</p>
 *
 * @param status         statut de la partie (présentation, en jeu, fin de niveau…)
 * @param numLevel       numéro du niveau
 * @param score          score de la partie
 * @param nbrPlayers     1, ou 2 si un fantôme est joué par un humain
 * @param ghostLeftLives vies du fantôme humain
 * @param ladybug        Ladybug
 * @param ghosts         les fantômes, dans l'ordre du groupe
 * @param maze           le labyrinthe
 * @param litLampMode    mode lampe (cercle de lumière autour des joueurs)
 * @param levelRendering style de dessin des murs
 * @param scoreMessages  messages de score affichés dans le labyrinthe
 * @param sounds         sons demandés pendant ce battement, dans l'ordre de l'énumération
 */
public record GameSnapshot(GameStatus status, int numLevel, int score, int nbrPlayers, int ghostLeftLives,
        LadybugSnapshot ladybug, List<GhostSnapshot> ghosts, MazeSnapshot maze, boolean litLampMode,
        LevelRendering levelRendering, List<ScoreMessageSnapshot> scoreMessages, List<Sounds> sounds) {

	public GameSnapshot {
		ghosts = List.copyOf(ghosts);
		scoreMessages = List.copyOf(scoreMessages);
		sounds = List.copyOf(sounds);
	}

	/** Le fantôme joué par un humain, s'il y en a un. */
	public Optional<GhostSnapshot> humanGhost() {
		return ghosts.stream().filter(ghost -> !ghost.computed()).findFirst();
	}

	/** Vrai pendant une partie (et non pendant la présentation ou un changement de niveau). */
	public boolean isInGame() {
		return is(GameStatus.IN_GAME);
	}

	/** Vrai si la partie est dans ce statut. */
	public boolean is(GameStatus expected) {
		return status == expected;
	}

	/** Vrai si ce son a été demandé pendant ce battement. */
	public boolean hasSound(Sounds sound) {
		return sounds.contains(sound);
	}
}
