package com.kycox.game.engine.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.kycox.game.input.InputSource;

/**
 * Le salon (étape 20.2) : qui a rejoint, avec quel appareil, quel fantôme. Les
 * règles sont ici, sans écran : au plus 3 fantômes humains (C52), un fantôme
 * par joueur, un appareil ne rejoint qu'une fois.
 */
class LobbyTest {
	private static final List<String> FOUR_GHOSTS = List.of("Blinky", "Pinky", "Inky", "Clyde");

	private final Lobby lobby = new Lobby(() -> FOUR_GHOSTS);

	@Test
	void aPlayerJoinsWithTheFirstFreeGhost() {
		assertThat(lobby.join(InputSource.KEYBOARD_ZQSD)).isTrue();
		assertThat(lobby.seats()).containsExactly(new Lobby.Seat(InputSource.KEYBOARD_ZQSD, "Blinky", false));
	}

	@Test
	void aDeviceJoinsOnlyOnce() {
		lobby.join(InputSource.KEYBOARD_ZQSD);
		assertThat(lobby.join(InputSource.KEYBOARD_ZQSD)).isFalse();
		assertThat(lobby.seats()).hasSize(1);
	}

	@Test
	void atMostThreeHumanGhosts() {
		assertThat(lobby.join(InputSource.KEYBOARD_ZQSD)).isTrue();
		assertThat(lobby.join(InputSource.KEYBOARD_IJKL)).isTrue();
		assertThat(lobby.join(InputSource.XBOX_PAD_2)).isTrue();
		assertThat(lobby.join(InputSource.XBOX_PAD_3)).isFalse();
		assertThat(lobby.seats()).extracting(Lobby.Seat::ghost).containsExactly("Blinky", "Pinky", "Inky");
		assertThat(lobby.maxSeats()).isEqualTo(3);
	}

	@Test
	void withTwoGhostsOnlyOneCanBeHuman() {
		var small = new Lobby(() -> List.of("Blinky", "Pinky"));
		assertThat(small.join(InputSource.KEYBOARD_ZQSD)).isTrue();
		assertThat(small.join(InputSource.KEYBOARD_IJKL)).isFalse();
		assertThat(small.maxSeats()).isEqualTo(1);
	}

	@Test
	void choosingSkipsTheGhostsTakenByOthers() {
		lobby.join(InputSource.KEYBOARD_ZQSD); // Blinky
		lobby.join(InputSource.KEYBOARD_IJKL); // Pinky
		lobby.next(InputSource.KEYBOARD_ZQSD); // Blinky -> (Pinky pris) -> Inky
		assertThat(lobby.seatOf(InputSource.KEYBOARD_ZQSD)).map(Lobby.Seat::ghost).contains("Inky");
		lobby.next(InputSource.KEYBOARD_ZQSD); // Clyde
		lobby.next(InputSource.KEYBOARD_ZQSD); // (Blinky libre) -> Blinky
		assertThat(lobby.seatOf(InputSource.KEYBOARD_ZQSD)).map(Lobby.Seat::ghost).contains("Blinky");
		lobby.previous(InputSource.KEYBOARD_ZQSD); // Clyde
		assertThat(lobby.seatOf(InputSource.KEYBOARD_ZQSD)).map(Lobby.Seat::ghost).contains("Clyde");
	}

	@Test
	void aConfirmedPlayerKeepsHisGhost() {
		lobby.join(InputSource.KEYBOARD_ZQSD);
		lobby.confirm(InputSource.KEYBOARD_ZQSD);
		lobby.next(InputSource.KEYBOARD_ZQSD);
		assertThat(lobby.seatOf(InputSource.KEYBOARD_ZQSD)).contains(new Lobby.Seat(InputSource.KEYBOARD_ZQSD, "Blinky", true));
	}

	@Test
	void readyWhenEveryoneHasConfirmed() {
		assertThat(lobby.isReady()).as("salon vide : partie solo").isTrue();
		lobby.join(InputSource.KEYBOARD_ZQSD);
		lobby.join(InputSource.XBOX_PAD_2);
		lobby.confirm(InputSource.KEYBOARD_ZQSD);
		assertThat(lobby.isReady()).isFalse();
		lobby.confirm(InputSource.XBOX_PAD_2);
		assertThat(lobby.isReady()).isTrue();
	}

	@Test
	void leavingFreesTheGhostAndCancelingAConfirmation() {
		lobby.join(InputSource.KEYBOARD_ZQSD);
		lobby.confirm(InputSource.KEYBOARD_ZQSD);
		lobby.cancel(InputSource.KEYBOARD_ZQSD); // d'abord : annule la validation
		assertThat(lobby.seatOf(InputSource.KEYBOARD_ZQSD)).map(Lobby.Seat::confirmed).contains(false);
		lobby.cancel(InputSource.KEYBOARD_ZQSD); // ensuite : quitte le salon
		assertThat(lobby.seats()).isEmpty();
		lobby.join(InputSource.KEYBOARD_IJKL);
		assertThat(lobby.seats()).extracting(Lobby.Seat::ghost).containsExactly("Blinky");
	}

	@Test
	void clearEmptiesTheLobby() {
		lobby.join(InputSource.KEYBOARD_ZQSD);
		lobby.clear();
		assertThat(lobby.seats()).isEmpty();
	}

	@Test
	void onlyGhostDevicesCanJoin() {
		assertThat(lobby.join(InputSource.KEYBOARD_ARROWS)).as("les flèches restent à Ladybug (C54)").isFalse();
		assertThat(lobby.join(InputSource.XBOX_PAD_1)).as("la manette 1 reste à Ladybug (C54)").isFalse();
	}
}
