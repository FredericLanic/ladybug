package com.kycox.game.engine.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kycox.game.body.GhostsGroup;
import com.kycox.game.body.ghost.Ghost;
import com.kycox.game.input.InputSource;

/**
 * Le salon (étape 20.2, idée I20) : avant une partie à plusieurs, chaque joueur
 * rejoint avec son appareil, choisit son personnage, puis valide.
 *
 * <p>Les règles sont ici, et non dans l'écran : la vue (20.3) ne fait qu'afficher
 * la photo du salon, et le réseau (étape 9) réutilisera le même salon.
 * <ul>
 * <li>tout appareil peut jouer tout personnage : flèches, ZQSD, IJKL, manettes
 * 1 à 4 ; Ladybug ou un fantôme (C56) ;</li>
 * <li>Ladybug a toujours exactement un joueur : la partie ne part pas sans
 * lui ;</li>
 * <li>au plus 3 fantômes humains, et toujours au moins un fantôme pour
 * l'ordinateur (C52) ;</li>
 * <li>un appareil ne rejoint qu'une fois ; un personnage n'a qu'un joueur.</li>
 * </ul>
 */
@Component
public class Lobby {
	/** Le nom de Ladybug parmi les personnages. */
	public static final String LADYBUG = "Ladybug";
	/** Au plus 3 fantômes joués par des humains (C52). */
	static final int MAX_HUMAN_GHOSTS = 3;

	/** Une place du salon : un appareil, le personnage choisi, validé ou non. */
	public record Seat(InputSource device, String character, boolean confirmed) {
		public boolean isLadybug() {
			return LADYBUG.equals(character);
		}
	}

	private final Supplier<List<String>> ghostNames;
	private final List<Seat> seats = new ArrayList<>();

	/** Le salon du jeu : les fantômes sont ceux du XML, lus à chaque fois. */
	@Autowired
	public Lobby(GhostsGroup ghostsGroup) {
		this(() -> ghostsGroup.getGhosts().stream().map(Ghost::getName).toList());
	}

	/** @param ghostNames les fantômes de la partie, dans l'ordre du XML */
	public Lobby(Supplier<List<String>> ghostNames) {
		this.ghostNames = ghostNames;
	}

	/** Fantômes jouables par des humains : 3 au plus, et au moins un pour l'ordinateur. */
	public int maxHumanGhosts() {
		return Math.max(0, Math.min(MAX_HUMAN_GHOSTS, ghostNames.get().size() - 1));
	}

	/** Places possibles : Ladybug et les fantômes jouables. */
	public int maxSeats() {
		return 1 + maxHumanGhosts();
	}

	/** Les personnages, Ladybug d'abord, puis les fantômes dans l'ordre du XML. */
	public List<String> characters() {
		var characters = new ArrayList<String>();
		characters.add(LADYBUG);
		characters.addAll(ghostNames.get());
		return characters;
	}

	/** Rejoindre avec le premier personnage possible (Ladybug d'abord) ; faux si refusé. */
	public boolean join(InputSource device) {
		if (seatOf(device).isPresent()) {
			return false;
		}
		var free = characters().stream().filter(c -> canTake(c, null)).findFirst();
		free.ifPresent(character -> seats.add(new Seat(device, character, false)));
		return free.isPresent();
	}

	/** Personnage suivant parmi les possibles (tant que le choix n'est pas validé). */
	public void next(InputSource device) {
		change(device, 1);
	}

	/** Personnage précédent parmi les possibles (tant que le choix n'est pas validé). */
	public void previous(InputSource device) {
		change(device, -1);
	}

	public void confirm(InputSource device) {
		replace(device, seat -> new Seat(seat.device(), seat.character(), true));
	}

	/** « Retour » : annule d'abord la validation, puis fait quitter le salon. */
	public void cancel(InputSource device) {
		seatOf(device).ifPresent(seat -> {
			if (seat.confirmed()) {
				replace(device, s -> new Seat(s.device(), s.character(), false));
			} else {
				seats.remove(seat);
			}
		});
	}

	/** Prêt quand Ladybug a un joueur et que tous les joueurs ont validé. */
	public boolean isReady() {
		return seats.stream().anyMatch(Seat::isLadybug) && seats.stream().allMatch(Seat::confirmed);
	}

	public void clear() {
		seats.clear();
	}

	public List<Seat> seats() {
		return List.copyOf(seats);
	}

	public Optional<Seat> seatOf(InputSource device) {
		return seats.stream().filter(seat -> seat.device() == device).findFirst();
	}

	/**
	 * Ce personnage peut-il être pris, par un nouveau joueur ({@code current} nul)
	 * ou par celui qui occupe {@code current} ? Il doit être libre ; un fantôme de
	 * plus n'est permis que s'il reste une place de fantôme humain.
	 */
	private boolean canTake(String character, Seat current) {
		if (seats.stream().anyMatch(seat -> seat.character().equals(character))) {
			return false;
		}
		if (LADYBUG.equals(character)) {
			return true;
		}
		var otherGhostSeats = seats.stream().filter(seat -> !seat.isLadybug() && seat != current).count();
		return otherGhostSeats < maxHumanGhosts();
	}

	private void change(InputSource device, int step) {
		seatOf(device).filter(seat -> !seat.confirmed()).ifPresent(seat -> {
			var characters = characters();
			var index = characters.indexOf(seat.character());
			for (var tries = 1; tries < characters.size(); tries++) {
				var candidate = characters.get(Math.floorMod(index + step * tries, characters.size()));
				if (canTake(candidate, seat)) {
					replace(device, s -> new Seat(s.device(), candidate, false));
					return;
				}
			}
		});
	}

	private void replace(InputSource device, UnaryOperator<Seat> change) {
		for (var i = 0; i < seats.size(); i++) {
			if (seats.get(i).device() == device) {
				seats.set(i, change.apply(seats.get(i)));
				return;
			}
		}
	}
}
