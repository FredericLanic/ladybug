package com.kycox.game.input;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kycox.game.body.GhostsGroup;
import com.kycox.game.body.ghost.Blinky;
import com.kycox.game.body.ghost.GhostColor;
import com.kycox.game.body.ghost.Pinky;
import com.kycox.game.body.ladybug.Ladybug;
import com.kycox.game.constant.game.GameMainConstants;

/**
 * Où trouver la boîte aux lettres de chaque joueur (étape 5.2) : celle du
 * fantôme humain n'existe qu'en mode 2 joueurs, et repart vide.
 */
class PlayerInputsTest {
	private final Blinky blinky = new Blinky();
	private final Pinky pinky = new Pinky();
	private final GhostsGroup ghosts = new GhostsGroup();
	private PlayerInputs inputs;

	@BeforeEach
	void setUp() {
		blinky.setColor(GhostColor.RED);
		pinky.setColor(GhostColor.PINK);
		ghosts.setGhosts(List.of(blinky, pinky));
		ghosts.setAllComputed();
		inputs = new PlayerInputs(mock(Ladybug.class), ghosts);
	}

	@Test
	@DisplayName("en solo, il n'y a pas de fantôme humain à piloter")
	void noHumanGhostInSolo() {
		assertThat(inputs.humanGhost()).isEmpty();
	}

	@Test
	@DisplayName("en 2 joueurs, la boîte est celle du fantôme choisi")
	void theHumanGhostInputIsTheChosenGhostOne() {
		ghosts.setHumanPlayer(pinky);
		assertThat(inputs.humanGhost()).containsSame(pinky.getPlayerInput());
	}

	@Test
	@DisplayName("un fantôme qui devient humain ne garde aucun ordre d'une ancienne partie")
	void aNewHumanGhostStartsWithoutOrders() {
		pinky.getPlayerInput().request(GameMainConstants.POINT_LEFT);
		ghosts.setHumanPlayer(pinky);
		assertThat(pinky.getPlayerInput().hasRequest()).isFalse();
	}

	@Test
	@DisplayName("sans table du salon : flèches et manette 1 pour Ladybug, ZQSD et manette 2 pour le fantôme humain")
	void withoutTableTheDefaultRouting() {
		ghosts.setHumanPlayer(pinky);
		assertThat(inputs.characterOf(InputSource.KEYBOARD_ARROWS)).contains(PlayerInputs.LADYBUG);
		assertThat(inputs.characterOf(InputSource.XBOX_PAD_1)).contains(PlayerInputs.LADYBUG);
		assertThat(inputs.of(InputSource.KEYBOARD_ZQSD)).containsSame(pinky.getPlayerInput());
		assertThat(inputs.of(InputSource.XBOX_PAD_2)).containsSame(pinky.getPlayerInput());
		assertThat(inputs.of(InputSource.KEYBOARD_IJKL)).isEmpty();
	}

	@Test
	@DisplayName("avec la table du salon, chaque appareil pilote le personnage choisi, les autres rien")
	void theLobbyTableRoutesEachDevice() {
		ghosts.setHumanPlayer(blinky);
		ghosts.setHumanPlayer(pinky);
		inputs.assign(java.util.Map.of(InputSource.XBOX_PAD_2, PlayerInputs.LADYBUG, InputSource.KEYBOARD_IJKL,
		        blinky.getName(), InputSource.KEYBOARD_ARROWS, pinky.getName()));
		assertThat(inputs.characterOf(InputSource.XBOX_PAD_2)).contains(PlayerInputs.LADYBUG);
		assertThat(inputs.of(InputSource.KEYBOARD_IJKL)).containsSame(blinky.getPlayerInput());
		assertThat(inputs.of(InputSource.KEYBOARD_ARROWS)).containsSame(pinky.getPlayerInput());
		assertThat(inputs.of(InputSource.XBOX_PAD_1)).as("appareil hors de la table").isEmpty();
		assertThat(inputs.of(InputSource.KEYBOARD_ZQSD)).isEmpty();

		inputs.useDefaultAssignments();
		assertThat(inputs.characterOf(InputSource.KEYBOARD_ARROWS)).contains(PlayerInputs.LADYBUG);
	}

	// --- manette perdue (C57, 20.5.3b)

	private static final java.util.Map<InputSource, String> TABLE = java.util.Map.of(InputSource.XBOX_PAD_1,
	        PlayerInputs.LADYBUG, InputSource.XBOX_PAD_2, "Blinky");

	@Test
	@DisplayName("un appareil libre prend le personnage qui attend ; un appareil déjà pris, non")
	void aFreeDeviceTakesTheWaitingCharacter() {
		inputs.assign(TABLE);
		inputs.waitFor("Blinky", InputSource.XBOX_PAD_2, 0);
		assertThat(inputs.resume(InputSource.XBOX_PAD_1)).isFalse(); // pilote déjà Ladybug
		assertThat(inputs.resume(InputSource.XBOX_PAD_3)).isTrue();
		assertThat(inputs.characterOf(InputSource.XBOX_PAD_3)).contains("Blinky");
		assertThat(inputs.characterOf(InputSource.XBOX_PAD_2)).isEmpty();
		assertThat(inputs.waiting()).isEmpty();
	}

	@Test
	@DisplayName("la manette perdue, rebranchée, reprend sa place")
	void theLostPadComingBackTakesItsPlaceAgain() {
		inputs.assign(TABLE);
		inputs.waitFor("Blinky", InputSource.XBOX_PAD_2, 0);
		assertThat(inputs.resume(InputSource.XBOX_PAD_2)).isTrue();
		assertThat(inputs.characterOf(InputSource.XBOX_PAD_2)).contains("Blinky");
	}

	@Test
	@DisplayName("un fantôme qui attend 60 s est rendu à l'ordinateur ; Ladybug attend sans limite")
	void aWaitingGhostIsGivenBackAfterTheLimit() {
		inputs.assign(TABLE);
		inputs.waitFor("Blinky", InputSource.XBOX_PAD_2, 1000);
		assertThat(inputs.releaseExpiredGhost(60_999, 60_000)).isEmpty();
		assertThat(inputs.releaseExpiredGhost(61_000, 60_000)).contains("Blinky");
		assertThat(inputs.characterOf(InputSource.XBOX_PAD_2)).isEmpty();
		assertThat(inputs.characterOf(InputSource.XBOX_PAD_1)).contains(PlayerInputs.LADYBUG);

		inputs.waitFor(PlayerInputs.LADYBUG, InputSource.XBOX_PAD_1, 0);
		assertThat(inputs.releaseExpiredGhost(10_000_000, 60_000)).isEmpty();
		assertThat(inputs.waiting()).isPresent();
	}

	@Test
	@DisplayName("par défaut, les flèches et la manette 1 pilotent Ladybug : l'un a un autre appareil")
	void defaultDevicesShareLadybug() {
		assertThat(inputs.hasOtherDevice(PlayerInputs.LADYBUG, InputSource.XBOX_PAD_1)).isTrue();
		inputs.assign(TABLE);
		assertThat(inputs.hasOtherDevice(PlayerInputs.LADYBUG, InputSource.XBOX_PAD_1)).isFalse();
	}
}
