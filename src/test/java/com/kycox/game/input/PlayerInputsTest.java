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
}
