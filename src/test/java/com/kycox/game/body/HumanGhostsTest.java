package com.kycox.game.body;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kycox.game.body.ghost.Blinky;
import com.kycox.game.body.ghost.GhostColor;
import com.kycox.game.body.ghost.Inky;
import com.kycox.game.body.ghost.Pinky;
import com.kycox.game.body.ladybug.Ladybug;
import com.kycox.game.input.PlayerInputs;

/**
 * Plusieurs fantômes joués par des humains (étape 5.3) et la règle C50 : un
 * fantôme humain sans vie est rendu à l'ordinateur ; la partie des fantômes
 * s'arrête quand tous les fantômes humains sont éliminés.
 */
class HumanGhostsTest {
	private final Blinky blinky = new Blinky();
	private final Pinky pinky = new Pinky();
	private final Inky inky = new Inky();
	private final GhostsGroup ghosts = new GhostsGroup();

	@BeforeEach
	void setUp() {
		blinky.setColor(GhostColor.RED);
		pinky.setColor(GhostColor.PINK);
		inky.setColor(GhostColor.CYAN);
		ghosts.setGhosts(List.of(blinky, pinky, inky));
		ghosts.setAllComputed();
		ghosts.setLeftLifes(3);
	}

	@Test
	@DisplayName("deux fantômes humains, chacun avec sa boîte aux lettres")
	void twoHumanGhostsHaveTheirOwnInputs() {
		ghosts.setHumanPlayer(GhostColor.PINK);
		ghosts.setHumanPlayer(GhostColor.CYAN);
		var inputs = new PlayerInputs(mock(Ladybug.class), ghosts);

		assertThat(ghosts.getHumanGhosts()).containsExactly(pinky, inky);
		assertThat(inputs.ghost("Pinky")).containsSame(pinky.getPlayerInput());
		assertThat(inputs.ghost("Inky")).containsSame(inky.getPlayerInput());
		assertThat(inputs.ghost("Blinky")).as("joué par l'ordinateur").isEmpty();
	}

	@Test
	@DisplayName("un fantôme humain éliminé est rendu à l'ordinateur, la partie continue")
	void anEliminatedHumanGhostGoesBackToTheComputer() {
		ghosts.setHumanPlayer(GhostColor.PINK);
		ghosts.setHumanPlayer(GhostColor.CYAN);
		pinky.setLeftLifes(0);

		ghosts.releaseEliminatedHumanGhosts();

		assertThat(pinky.isComputed()).isTrue();
		assertThat(ghosts.getHumanGhosts()).containsExactly(inky);
		assertThat(ghosts.allHumanGhostsEliminated()).isFalse();
	}

	@Test
	@DisplayName("le dernier fantôme humain éliminé termine la partie, sans être rendu")
	void theLastHumanGhostEndsTheGame() {
		ghosts.setHumanPlayer(GhostColor.PINK);
		pinky.setLeftLifes(0);

		ghosts.releaseEliminatedHumanGhosts();

		assertThat(pinky.isComputed()).as("comme avant l'étape 5").isFalse();
		assertThat(ghosts.allHumanGhostsEliminated()).isTrue();
	}

	@Test
	@DisplayName("deux fantômes humains éliminés au même battement : la partie s'arrête")
	void twoEliminatedAtOnceEndTheGame() {
		ghosts.setHumanPlayer(GhostColor.PINK);
		ghosts.setHumanPlayer(GhostColor.CYAN);
		pinky.setLeftLifes(0);
		inky.setLeftLifes(0);

		ghosts.releaseEliminatedHumanGhosts();

		assertThat(ghosts.allHumanGhostsEliminated()).isTrue();
	}

	@Test
	@DisplayName("en solo, la partie des fantômes ne s'arrête jamais")
	void soloNeverEndsTheGhostsGame() {
		assertThat(ghosts.allHumanGhostsEliminated()).isFalse();
	}
}
