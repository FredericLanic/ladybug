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
package com.kycox.game.input;

import com.kycox.game.body.GhostsGroup;
import com.kycox.game.body.ladybug.Ladybug;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Où trouver la boîte aux lettres de chaque joueur (étape 5). Les contrôleurs
 * (clavier, manette) et les tests passent par ici pour donner leurs ordres,
 * sans connaître le modèle du jeu.
 *
 * <p>Depuis 20.4, une <b>table de routage</b> « appareil → personnage » dit à
 * qui va chaque appareil : le salon la remplit au lancement d'une partie à
 * plusieurs ({@link #assign}). Sans table (solo, ou ancien mode deux joueurs),
 * la répartition d'avant reste : flèches et manette 1 pour Ladybug, ZQSD et
 * manette 2 pour le fantôme humain.
 */
@Component
public class PlayerInputs {
	/** Le nom de Ladybug dans la table (les fantômes y sont par leur nom du XML). */
	public static final String LADYBUG = "Ladybug";

	private final Ladybug ladybug;
	private final GhostsGroup ghostsGroup;
	/** La table remplie par le salon ; vide : répartition par défaut. */
	private Map<InputSource, String> assignments = Map.of();

	public PlayerInputs(Ladybug ladybug, GhostsGroup ghostsGroup) {
		this.ladybug = ladybug;
		this.ghostsGroup = ghostsGroup;
	}

	/** La table du salon : chaque appareil pilote ce personnage, les autres ne pilotent rien. */
	public void assign(Map<InputSource, String> table) {
		assignments = Map.copyOf(table);
		waiting = null;
	}

	/** Retour à la répartition par défaut (solo, ou ancien mode deux joueurs). */
	public void useDefaultAssignments() {
		assignments = Map.of();
		waiting = null;
	}

	/** Un personnage dont la manette a été perdue en partie (C57) : la partie l'attend. */
	public record Waiting(String character, InputSource lostDevice, long sinceMillis) {
	}

	private Waiting waiting;

	/** Le personnage qui attend un appareil, s'il y en a un. */
	public Optional<Waiting> waiting() {
		return Optional.ofNullable(waiting);
	}

	/** Note qu'un personnage attend (un seul à la fois : le premier perdu). */
	public void waitFor(String character, InputSource lostDevice, long nowMillis) {
		if (waiting == null) {
			waiting = new Waiting(character, lostDevice, nowMillis);
		}
	}

	/** Plus personne n'attend (nouvelle partie, partie arrêtée). */
	public void clearWaiting() {
		waiting = null;
	}

	/** Vrai si un autre appareil que celui-ci pilote encore ce personnage (flèches et manette 1, par défaut). */
	public boolean hasOtherDevice(String character, InputSource device) {
		return Arrays.stream(InputSource.values())
		        .anyMatch(other -> other != device && characterOf(other).filter(character::equals).isPresent());
	}

	/**
	 * Reprise (C57) : le premier appareil libre qui agit (ou la manette perdue,
	 * rebranchée) prend le personnage qui attend. Rend vrai si la reprise a eu lieu.
	 */
	public boolean resume(InputSource device) {
		if (waiting == null || device != waiting.lostDevice() && characterOf(device).isPresent()) {
			return false;
		}
		var table = explicitTable();
		table.remove(waiting.lostDevice());
		table.put(device, waiting.character());
		assignments = Map.copyOf(table);
		waiting = null;
		return true;
	}

	/**
	 * Le fantôme qui attend depuis plus de {@code limitMillis} est rendu à
	 * l'ordinateur (C57) : son nom est rendu, sa place dans la table libérée.
	 * Ladybug, elle, attend sans limite.
	 */
	public Optional<String> releaseExpiredGhost(long nowMillis, long limitMillis) {
		if (waiting == null || LADYBUG.equals(waiting.character()) || nowMillis - waiting.sinceMillis() < limitMillis) {
			return Optional.empty();
		}
		var name = waiting.character();
		var table = explicitTable();
		table.remove(waiting.lostDevice());
		assignments = Map.copyOf(table);
		waiting = null;
		return Optional.of(name);
	}

	/** La table réellement utilisée (répartition par défaut comprise), pour la modifier. */
	private Map<InputSource, String> explicitTable() {
		var table = new EnumMap<InputSource, String>(InputSource.class);
		for (var device : InputSource.values()) {
			characterOf(device).ifPresent(character -> table.put(device, character));
		}
		return table;
	}

	/**
	 * Le personnage piloté par cet appareil ({@link #LADYBUG} ou le nom d'un
	 * fantôme), s'il en pilote un.
	 */
	public Optional<String> characterOf(InputSource device) {
		if (assignments.isEmpty()) {
			return switch (device) {
				case KEYBOARD_ARROWS, XBOX_PAD_1 -> Optional.of(LADYBUG);
				case KEYBOARD_ZQSD, XBOX_PAD_2 -> ghostsGroup.getHumanGhosts().stream().findFirst()
				        .map(ghost -> ghost.getName());
				default -> Optional.empty();
			};
		}
		return Optional.ofNullable(assignments.get(device));
	}

	/** La boîte aux lettres du personnage piloté par cet appareil, s'il en pilote un. */
	public Optional<PlayerInput> of(InputSource device) {
		return characterOf(device).flatMap(character -> LADYBUG.equals(character) ? Optional.of(ladybug())
		        : ghost(character));
	}

	/** Les ordres donnés à Ladybug. */
	public PlayerInput ladybug() {
		return ladybug.getPlayerInput();
	}

	/**
	 * Les ordres donnés au fantôme joué par le deuxième joueur, s'il y en a un
	 * (étape 5.2). En solo, il n'y en a pas : les touches ZQSD ne font rien.
	 */
	public Optional<PlayerInput> humanGhost() {
		return ghostsGroup.getHumanGhosts().stream().findFirst().map(ghost -> ghost.getPlayerInput());
	}

	/**
	 * Les ordres donnés à ce fantôme, s'il est joué par un humain (étape 5.3).
	 * Avec plusieurs fantômes humains, chaque joueur écrit dans la boîte du sien.
	 */
	public Optional<PlayerInput> ghost(String name) {
		return ghostsGroup.getHumanGhosts().stream().filter(ghost -> ghost.getName().equals(name)).findFirst()
		        .map(ghost -> ghost.getPlayerInput());
	}
}
