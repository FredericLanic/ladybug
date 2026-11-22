package com.kycox.game.engine.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.kycox.game.input.InputSource;

/**
 * Le salon (étape 20.2) : qui a rejoint, avec quel appareil, quel personnage.
 * Les règles sont ici, sans écran (C56, C52) : tout appareil peut jouer tout
 * personnage ; Ladybug a toujours un joueur ; au plus 3 fantômes humains ; un
 * personnage par joueur, un appareil ne rejoint qu'une fois.
 */
class LobbyTest {
	private static final List<String> FOUR_GHOSTS = List.of("Clyde", "Blinky", "Inky", "Pinky");

	private final Lobby lobby = new Lobby(() -> FOUR_GHOSTS);

	@Test
	void theFirstPlayerGetsLadybugTheNextOnesTheFirstFreeGhost() {
		assertThat(lobby.join(InputSource.XBOX_PAD_2)).isTrue();
		assertThat(lobby.join(InputSource.KEYBOARD_ARROWS)).isTrue();
		assertThat(lobby.seats()).containsExactly(new Lobby.Seat(InputSource.XBOX_PAD_2, "Ladybug", false),
		        new Lobby.Seat(InputSource.KEYBOARD_ARROWS, "Clyde", false));
	}

	@Test
	void anyDeviceCanJoin() {
		for (var device : InputSource.values()) {
			assertThat(new Lobby(() -> FOUR_GHOSTS).join(device)).as(device.name()).isTrue();
		}
	}

	@Test
	void aDeviceJoinsOnlyOnce() {
		lobby.join(InputSource.KEYBOARD_ZQSD);
		assertThat(lobby.join(InputSource.KEYBOARD_ZQSD)).isFalse();
		assertThat(lobby.seats()).hasSize(1);
	}

	@Test
	void ladybugAndAtMostThreeHumanGhosts() {
		assertThat(lobby.join(InputSource.KEYBOARD_ARROWS)).isTrue(); // Ladybug
		assertThat(lobby.join(InputSource.KEYBOARD_ZQSD)).isTrue();
		assertThat(lobby.join(InputSource.KEYBOARD_IJKL)).isTrue();
		assertThat(lobby.join(InputSource.XBOX_PAD_1)).isTrue();
		assertThat(lobby.join(InputSource.XBOX_PAD_2)).as("un fantôme reste à l'ordinateur").isFalse();
		assertThat(lobby.seats()).extracting(Lobby.Seat::character).containsExactly("Ladybug", "Clyde", "Blinky",
		        "Inky");
		assertThat(lobby.maxSeats()).isEqualTo(4);
	}

	@Test
	void withTwoGhostsOnlyOneCanBeHuman() {
		var small = new Lobby(() -> List.of("Blinky", "Pinky"));
		assertThat(small.join(InputSource.KEYBOARD_ARROWS)).isTrue(); // Ladybug
		assertThat(small.join(InputSource.KEYBOARD_ZQSD)).isTrue(); // Blinky
		assertThat(small.join(InputSource.KEYBOARD_IJKL)).isFalse();
		assertThat(small.maxHumanGhosts()).isEqualTo(1);
	}

	@Test
	void choosingSkipsTheCharactersTakenByOthers() {
		lobby.join(InputSource.KEYBOARD_ARROWS); // Ladybug
		lobby.join(InputSource.KEYBOARD_ZQSD); // Clyde
		lobby.next(InputSource.KEYBOARD_ZQSD); // Blinky
		lobby.next(InputSource.KEYBOARD_ZQSD); // Inky
		lobby.next(InputSource.KEYBOARD_ZQSD); // Pinky
		lobby.next(InputSource.KEYBOARD_ZQSD); // (Ladybug prise) -> Clyde
		assertThat(lobby.seatOf(InputSource.KEYBOARD_ZQSD)).map(Lobby.Seat::character).contains("Clyde");
		lobby.previous(InputSource.KEYBOARD_ZQSD); // (Ladybug prise) -> Pinky
		assertThat(lobby.seatOf(InputSource.KEYBOARD_ZQSD)).map(Lobby.Seat::character).contains("Pinky");
	}

	@Test
	void ladybugCanBeChosenWhenFree() {
		lobby.join(InputSource.KEYBOARD_ARROWS); // Ladybug
		lobby.join(InputSource.XBOX_PAD_1); // Clyde
		lobby.cancel(InputSource.KEYBOARD_ARROWS); // les flèches quittent : Ladybug est libre
		lobby.previous(InputSource.XBOX_PAD_1); // Clyde -> Ladybug
		assertThat(lobby.seatOf(InputSource.XBOX_PAD_1)).map(Lobby.Seat::character).contains("Ladybug");
	}

	@Test
	void aGhostCannotBeChosenWhenAllGhostSeatsAreTaken() {
		var small = new Lobby(() -> List.of("Blinky", "Pinky")); // 1 fantôme humain au plus
		small.join(InputSource.KEYBOARD_ZQSD); // Ladybug
		small.join(InputSource.KEYBOARD_IJKL); // Blinky
		small.next(InputSource.KEYBOARD_ZQSD); // Ladybug -> pas de fantôme possible : reste Ladybug
		assertThat(small.seatOf(InputSource.KEYBOARD_ZQSD)).map(Lobby.Seat::character).contains("Ladybug");
		small.next(InputSource.KEYBOARD_IJKL); // Blinky -> Pinky (un fantôme pour un autre : permis)
		assertThat(small.seatOf(InputSource.KEYBOARD_IJKL)).map(Lobby.Seat::character).contains("Pinky");
	}

	@Test
	void aConfirmedPlayerKeepsHisCharacter() {
		lobby.join(InputSource.KEYBOARD_ZQSD);
		lobby.confirm(InputSource.KEYBOARD_ZQSD);
		lobby.next(InputSource.KEYBOARD_ZQSD);
		assertThat(lobby.seatOf(InputSource.KEYBOARD_ZQSD))
		        .contains(new Lobby.Seat(InputSource.KEYBOARD_ZQSD, "Ladybug", true));
	}

	@Test
	void readyWhenLadybugHasAPlayerAndEveryoneHasConfirmed() {
		assertThat(lobby.isReady()).as("salon vide : pas de Ladybug").isFalse();
		lobby.join(InputSource.KEYBOARD_ARROWS); // Ladybug
		lobby.join(InputSource.XBOX_PAD_2); // Clyde
		lobby.confirm(InputSource.KEYBOARD_ARROWS);
		assertThat(lobby.isReady()).isFalse();
		lobby.confirm(InputSource.XBOX_PAD_2);
		assertThat(lobby.isReady()).isTrue();
	}

	@Test
	void notReadyWithoutLadybug() {
		lobby.join(InputSource.KEYBOARD_ARROWS); // Ladybug
		lobby.join(InputSource.XBOX_PAD_2); // Clyde
		lobby.cancel(InputSource.KEYBOARD_ARROWS); // Ladybug libre
		lobby.confirm(InputSource.XBOX_PAD_2);
		assertThat(lobby.isReady()).isFalse();
	}

	@Test
	void cancelingFirstUnconfirmsThenLeaves() {
		lobby.join(InputSource.KEYBOARD_ZQSD);
		lobby.confirm(InputSource.KEYBOARD_ZQSD);
		lobby.cancel(InputSource.KEYBOARD_ZQSD);
		assertThat(lobby.seatOf(InputSource.KEYBOARD_ZQSD)).map(Lobby.Seat::confirmed).contains(false);
		lobby.cancel(InputSource.KEYBOARD_ZQSD);
		assertThat(lobby.seats()).isEmpty();
	}

	@Test
	void clearEmptiesTheLobby() {
		lobby.join(InputSource.KEYBOARD_ZQSD);
		lobby.clear();
		assertThat(lobby.seats()).isEmpty();
	}
}
