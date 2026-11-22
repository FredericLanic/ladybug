package com.kycox.game.engine.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kycox.game.body.GhostsGroup;
import com.kycox.game.body.ghost.Ghost;
import com.kycox.game.input.InputSource;

/**
 * Le salon (étape 20.2, idée I20) : avant une partie à plusieurs, chaque joueur
 * rejoint avec son appareil, choisit son fantôme, puis valide.
 *
 * <p>Les règles sont ici, et non dans l'écran : la vue (20.3) ne fait qu'afficher
 * la photo du salon, et le réseau (étape 9) réutilisera le même salon.
 * <ul>
 * <li>au plus 3 fantômes humains, et toujours au moins un fantôme pour
 * l'ordinateur (C52) ;</li>
 * <li>appareils des fantômes : ZQSD, IJKL, manettes 2 à 4 ; les flèches et la
 * manette 1 restent à Ladybug (C54) ;</li>
 * <li>un appareil ne rejoint qu'une fois ; un fantôme n'a qu'un joueur.</li>
 * </ul>
 */
@Component
public class Lobby {
	/** Au plus 3 fantômes joués par des humains (C52). */
	static final int MAX_HUMAN_GHOSTS = 3;
	/** Les appareils qui peuvent jouer un fantôme (C54). */
	static final Set<InputSource> GHOST_DEVICES = Set.of(InputSource.KEYBOARD_ZQSD, InputSource.KEYBOARD_IJKL,
	        InputSource.XBOX_PAD_2, InputSource.XBOX_PAD_3, InputSource.XBOX_PAD_4);

	/** Une place du salon : un appareil, le fantôme choisi, validé ou non. */
	public record Seat(InputSource device, String ghost, boolean confirmed) {
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

	/** Places possibles : 3 au plus, et au moins un fantôme pour l'ordinateur. */
	public int maxSeats() {
		return Math.max(0, Math.min(MAX_HUMAN_GHOSTS, ghostNames.get().size() - 1));
	}

	/** Rejoindre avec le premier fantôme libre ; faux si refusé. */
	public boolean join(InputSource device) {
		if (!GHOST_DEVICES.contains(device) || seatOf(device).isPresent() || seats.size() >= maxSeats()) {
			return false;
		}
		var free = ghostNames.get().stream().filter(this::isFree).findFirst();
		free.ifPresent(ghost -> seats.add(new Seat(device, ghost, false)));
		return free.isPresent();
	}

	/** Fantôme suivant parmi les libres (tant que le choix n'est pas validé). */
	public void next(InputSource device) {
		change(device, 1);
	}

	/** Fantôme précédent parmi les libres (tant que le choix n'est pas validé). */
	public void previous(InputSource device) {
		change(device, -1);
	}

	public void confirm(InputSource device) {
		replace(device, seat -> new Seat(seat.device(), seat.ghost(), true));
	}

	/** « Retour » : annule d'abord la validation, puis fait quitter le salon. */
	public void cancel(InputSource device) {
		seatOf(device).ifPresent(seat -> {
			if (seat.confirmed()) {
				replace(device, s -> new Seat(s.device(), s.ghost(), false));
			} else {
				seats.remove(seat);
			}
		});
	}

	/** Prêt quand tous les joueurs ont validé (un salon vide : partie solo). */
	public boolean isReady() {
		return seats.stream().allMatch(Seat::confirmed);
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

	private boolean isFree(String ghost) {
		return seats.stream().noneMatch(seat -> seat.ghost().equals(ghost));
	}

	private void change(InputSource device, int step) {
		seatOf(device).filter(seat -> !seat.confirmed()).ifPresent(seat -> {
			var names = ghostNames.get();
			var index = names.indexOf(seat.ghost());
			for (var tries = 1; tries < names.size(); tries++) {
				var candidate = names.get(Math.floorMod(index + step * tries, names.size()));
				if (isFree(candidate)) {
					replace(device, s -> new Seat(s.device(), candidate, false));
					return;
				}
			}
		});
	}

	private void replace(InputSource device, java.util.function.UnaryOperator<Seat> change) {
		for (var i = 0; i < seats.size(); i++) {
			if (seats.get(i).device() == device) {
				seats.set(i, change.apply(seats.get(i)));
				return;
			}
		}
	}
}
