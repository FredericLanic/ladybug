package com.kycox.game.engine.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.kycox.game.engine.model.Lobby.Column;
import com.kycox.game.engine.model.LobbyCommand.ChangeValue;
import com.kycox.game.engine.model.LobbyCommand.Claim;
import com.kycox.game.engine.model.LobbyCommand.MoveRow;
import com.kycox.game.engine.model.LobbyCommand.SwitchColumn;
import com.kycox.game.input.InputSource;

/**
 * Le salon (étape 20.3) : une ligne par joueur, avec son personnage et ses
 * commandes ; une ligne active, surlignée à l'écran. Les règles sont ici, sans
 * écran : Cox doit avoir un joueur ; au plus 3 fantômes humains (C52) ; un
 * personnage et des commandes ne servent qu'une fois ; une manette s'attribue en
 * appuyant sur A.
 */
class LobbyTest {
	private static final List<String> FOUR_GHOSTS = List.of("Clyde", "Blinky", "Inky", "Pinky");

	private final Lobby lobby = new Lobby(() -> FOUR_GHOSTS);

	@org.junit.jupiter.api.BeforeEach
	void twoPadsAreConnected() {
		lobby.setConnectedPads(java.util.Set.of(InputSource.XBOX_PAD_1, InputSource.XBOX_PAD_2));
	}

	@Test
	void byDefaultCoxWithTheArrowsAndThreeFreeRows() {
		assertThat(lobby.rows()).hasSize(4);
		assertThat(lobby.rows().get(0)).isEqualTo(new Lobby.Row(Lobby.LADYBUG, InputSource.KEYBOARD_ARROWS, false));
		assertThat(lobby.rows().subList(1, 4)).allMatch(Lobby.Row::isEmpty);
		assertThat(lobby.activeRow()).isZero();
		assertThat(lobby.activeColumn()).as("Cox : seules ses commandes se choisissent").isEqualTo(Column.CONTROLS);
		assertThat(lobby.isReady()).isTrue();
	}

	@Test
	void movingBetweenRowsWrapsAround() {
		lobby.apply(new MoveRow(-1)); // au-dessus de Cox : la ligne des boutons, la dernière
		assertThat(lobby.activeRow()).isEqualTo(4);
		assertThat(lobby.isOnButtons()).isTrue();
		lobby.apply(new MoveRow(1));
		assertThat(lobby.activeRow()).isZero();
	}

	@Test
	void theButtonsLineStartsOnPlayAndLeftRightSwitchesButton() {
		lobby.apply(new MoveRow(4));
		assertThat(lobby.activeColumn()).isEqualTo(Column.PLAY);
		lobby.apply(new ChangeValue(1));
		assertThat(lobby.activeColumn()).isEqualTo(Column.BACK);
		lobby.apply(new SwitchColumn());
		assertThat(lobby.activeColumn()).isEqualTo(Column.PLAY);
	}

	@Test
	void confirmingTheLastRowGoesToPlay() {
		lobby.apply(new MoveRow(3)); // dernière ligne de joueur, vide
		lobby.apply(new LobbyCommand.Confirm());
		assertThat(lobby.isOnButtons()).isTrue();
		assertThat(lobby.activeColumn()).isEqualTo(Column.PLAY);
	}

	@Test
	void playIsGreyedUntilTheLobbyIsReady() {
		lobby.setConnectedPads(java.util.Set.of(InputSource.XBOX_PAD_2));
		lobby.apply(new MoveRow(1));
		lobby.apply(new LobbyCommand.ChangeCharacter(1)); // Clyde, ZQSD
		lobby.apply(new LobbyCommand.ChangeControls(-1)); // ZQSD -> (flèches de Cox) -> « appuie sur A »
		assertThat(lobby.isReady()).isFalse();

		lobby.apply(new MoveRow(3)); // les boutons, sur « jouer »
		assertThat(lobby.apply(new LobbyCommand.Confirm())).as("jouer grisé").isEqualTo(Lobby.Outcome.NONE);
		lobby.apply(new Claim(InputSource.XBOX_PAD_2)); // la manette prend la ligne qui attend
		assertThat(lobby.apply(new LobbyCommand.Confirm())).isEqualTo(Lobby.Outcome.PLAY);
	}

	@Test
	void theBackButtonAsksToLeave() {
		lobby.apply(new MoveRow(-1));
		lobby.apply(new SwitchColumn());
		assertThat(lobby.apply(new LobbyCommand.Confirm())).isEqualTo(Lobby.Outcome.BACK);
	}

	@Test
	void onTheButtonsNothingElseChanges() {
		lobby.apply(new MoveRow(-1));
		var before = lobby.rows();
		lobby.apply(new LobbyCommand.ChangeCharacter(1));
		lobby.apply(new LobbyCommand.ChangeControls(1));
		lobby.apply(new LobbyCommand.Undo());
		assertThat(lobby.apply(new LobbyCommand.Back())).isEqualTo(Lobby.Outcome.NONE);
		assertThat(lobby.rows()).isEqualTo(before);
		assertThat(lobby.isOnButtons()).isTrue();
	}

	@Test
	void choosingACharacterSkipsTheTakenOnes() {
		lobby.apply(new MoveRow(1)); // ligne 2, vide
		lobby.apply(new ChangeValue(1)); // --- -> (Cox pris) -> Clyde
		assertThat(lobby.rows().get(1).character()).isEqualTo("Clyde");
		lobby.apply(new ChangeValue(-1)); // Clyde -> (Cox pris) -> ---
		assertThat(lobby.rows().get(1).character()).isNull();
	}

	@Test
	void choosingAGhostGivesTheRowFreeControls() {
		lobby.apply(new MoveRow(1));
		lobby.apply(new ChangeValue(1)); // Clyde
		assertThat(lobby.rows().get(1).device()).as("des commandes libres, pas les flèches de Cox")
		        .isEqualTo(InputSource.KEYBOARD_ZQSD);
	}

	@Test
	void choosingControlsSkipsTheTakenOnes() {
		lobby.apply(new MoveRow(1));
		lobby.apply(new ChangeValue(1)); // Clyde, ZQSD
		lobby.apply(new SwitchColumn());
		lobby.apply(new ChangeValue(1)); // ZQSD -> IJKL
		assertThat(lobby.rows().get(1).device()).isEqualTo(InputSource.KEYBOARD_IJKL);
		lobby.apply(new ChangeValue(-1)); // IJKL -> ZQSD
		lobby.apply(new ChangeValue(-1)); // ZQSD -> (flèches prises) -> « appuie sur A »
		assertThat(lobby.rows().get(1).waitingForPad()).isTrue();
	}

	@Test
	void aPadPressingAClaimsTheWaitingRow() {
		lobby.apply(new MoveRow(1));
		lobby.apply(new ChangeValue(1)); // Clyde
		lobby.apply(new SwitchColumn());
		lobby.apply(new ChangeValue(-1)); // « appuie sur A »
		assertThat(lobby.isReady()).as("une ligne attend sa manette").isFalse();

		lobby.apply(new Claim(InputSource.XBOX_PAD_2));
		assertThat(lobby.rows().get(1)).isEqualTo(new Lobby.Row("Clyde", InputSource.XBOX_PAD_2, false));
		assertThat(lobby.isReady()).isTrue();
	}

	@Test
	void aClaimWithoutAWaitingRowDoesNothing() {
		lobby.apply(new Claim(InputSource.XBOX_PAD_2));
		assertThat(lobby.rows().get(0).device()).isEqualTo(InputSource.KEYBOARD_ARROWS);
	}

	@Test
	void aPadAlreadyUsedMovesToTheWaitingRow() {
		// Cox à la manette 1, puis la manette 1 réclame la ligne de Clyde : elle quitte Cox
		lobby.apply(new ChangeValue(-1)); // Cox (colonne commandes) : flèches -> « appuie sur A »
		lobby.apply(new Claim(InputSource.XBOX_PAD_1));
		lobby.apply(new MoveRow(1)); // ligne 2, colonne personnage
		lobby.apply(new ChangeValue(1)); // Clyde, ZQSD
		lobby.apply(new SwitchColumn());
		lobby.apply(new ChangeValue(-1)); // « appuie sur A »
		lobby.apply(new Claim(InputSource.XBOX_PAD_1));
		assertThat(lobby.rows().get(1).device()).isEqualTo(InputSource.XBOX_PAD_1);
		assertThat(lobby.rows().get(0).waitingForPad()).as("Cox attend de nouvelles commandes").isTrue();
	}

	@Test
	void atMostThreeHumanGhosts() {
		for (var row = 1; row <= 3; row++) {
			lobby.apply(new MoveRow(1));
			lobby.apply(new ChangeValue(1)); // le premier fantôme libre
		}
		assertThat(lobby.rows()).extracting(Lobby.Row::character).containsExactly("Ladybug", "Clyde", "Blinky",
		        "Inky");
		lobby.apply(new ChangeValue(1)); // Inky -> Pinky : permis (un fantôme pour un autre)
		assertThat(lobby.rows().get(3).character()).isEqualTo("Pinky");
	}

	@Test
	void withTwoGhostsOnlyTwoRows() {
		var small = new Lobby(() -> List.of("Blinky", "Pinky"));
		assertThat(small.rows()).hasSize(2); // Cox et un fantôme humain au plus
	}

	@Test
	void coxStaysOnTheFirstRowOnlyHisControlsChange() {
		lobby.apply(new LobbyCommand.ChangeCharacter(1));
		lobby.apply(new SwitchColumn());
		lobby.apply(new ChangeValue(1)); // sur la ligne de Cox : les commandes
		assertThat(lobby.rows().get(0)).isEqualTo(new Lobby.Row(Lobby.LADYBUG, InputSource.KEYBOARD_ZQSD, false));
		assertThat(lobby.activeColumn()).isEqualTo(Column.CONTROLS);
	}

	@Test
	void theOtherRowsOfferOnlyGhosts() {
		lobby.apply(new MoveRow(1));
		for (var i = 0; i < 6; i++) {
			lobby.apply(new ChangeValue(1));
			assertThat(lobby.rows().get(1).character()).isNotEqualTo(Lobby.LADYBUG);
		}
	}

	@Test
	void assignmentsTellWhichDeviceDrivesWhichCharacter() {
		lobby.apply(new MoveRow(1));
		lobby.apply(new ChangeValue(1)); // Clyde, ZQSD
		assertThat(lobby.assignments()).isEqualTo(
		        Map.of(InputSource.KEYBOARD_ARROWS, Lobby.LADYBUG, InputSource.KEYBOARD_ZQSD, "Clyde"));
	}

	@Test
	void theStickChangesCharacterOrControlsWhateverTheColumn() {
		lobby.apply(new MoveRow(1));
		lobby.apply(new LobbyCommand.ChangeCharacter(1)); // Clyde, ZQSD
		lobby.apply(new LobbyCommand.ChangeControls(1)); // ZQSD -> IJKL, sans changer de colonne
		assertThat(lobby.rows().get(1)).isEqualTo(new Lobby.Row("Clyde", InputSource.KEYBOARD_IJKL, false));
		assertThat(lobby.activeColumn()).isEqualTo(Column.CHARACTER);
	}

	@Test
	void undoCancelsTheLastChoiceOfTheActiveRow() {
		lobby.apply(new MoveRow(1));
		lobby.apply(new LobbyCommand.ChangeCharacter(1)); // Clyde, ZQSD
		lobby.apply(new LobbyCommand.ChangeControls(1)); // IJKL
		lobby.apply(new LobbyCommand.Undo());
		assertThat(lobby.rows().get(1)).isEqualTo(new Lobby.Row("Clyde", InputSource.KEYBOARD_ZQSD, false));
		lobby.apply(new LobbyCommand.Undo());
		assertThat(lobby.rows().get(1).isEmpty()).isTrue();
		lobby.apply(new LobbyCommand.Undo()); // plus rien à annuler
		assertThat(lobby.rows().get(1).isEmpty()).isTrue();
		assertThat(lobby.rows().get(0).isLadybug()).as("les autres lignes ne bougent pas").isTrue();
	}

	@Test
	void onlyConnectedPadsAreOffered() {
		lobby.setConnectedPads(java.util.Set.of(InputSource.XBOX_PAD_2));
		lobby.apply(new SwitchColumn());
		lobby.apply(new ChangeValue(1)); // flèches -> ZQSD
		lobby.apply(new ChangeValue(1)); // IJKL
		lobby.apply(new ChangeValue(1)); // (manette 1 débranchée) -> manette 2
		assertThat(lobby.rows().get(0).device()).isEqualTo(InputSource.XBOX_PAD_2);
		lobby.apply(new ChangeValue(1)); // (manettes 3 et 4 débranchées) -> « appuie sur A »
		assertThat(lobby.rows().get(0).waitingForPad()).isTrue();
	}

	@Test
	void withoutAnyPadNeitherPadsNorPressAAreOffered() {
		lobby.setConnectedPads(java.util.Set.of());
		lobby.apply(new SwitchColumn());
		lobby.apply(new ChangeValue(-1)); // flèches -> (rien d'autre que le clavier) -> IJKL
		assertThat(lobby.rows().get(0).device()).isEqualTo(InputSource.KEYBOARD_IJKL);
	}

	@Test
	void aRowWhosePadIsUnpluggedWaitsAgain() {
		lobby.apply(new SwitchColumn());
		lobby.apply(new ChangeValue(-1)); // « appuie sur A »
		lobby.apply(new Claim(InputSource.XBOX_PAD_1));
		lobby.setConnectedPads(java.util.Set.of(InputSource.XBOX_PAD_2)); // manette 1 débranchée
		assertThat(lobby.rows().get(0).waitingForPad()).isTrue();
		assertThat(lobby.isReady()).isFalse();
	}

	@Test
	void aRowIsDoneInTwoStepsCharacterThenControls() {
		lobby.apply(new MoveRow(1));
		lobby.apply(new ChangeValue(1)); // Clyde
		lobby.apply(new LobbyCommand.Confirm()); // personnage validé
		assertThat(lobby.activeColumn()).isEqualTo(Column.CONTROLS);
		lobby.apply(new ChangeValue(1)); // ZQSD -> IJKL
		lobby.apply(new LobbyCommand.Confirm()); // commandes validées : ligne suivante
		assertThat(lobby.activeRow()).isEqualTo(2);
		assertThat(lobby.activeColumn()).isEqualTo(Column.CHARACTER);
		assertThat(lobby.rows().get(1)).isEqualTo(new Lobby.Row("Clyde", InputSource.KEYBOARD_IJKL, false));
	}

	@Test
	void confirmingWhileWaitingForAPadWaits() {
		lobby.apply(new MoveRow(1));
		lobby.apply(new ChangeValue(1)); // Clyde
		lobby.apply(new LobbyCommand.Confirm());
		lobby.apply(new ChangeValue(-1)); // ZQSD -> (flèches prises) -> « appuie sur A »
		lobby.apply(new LobbyCommand.Confirm());
		assertThat(lobby.activeRow()).as("la ligne attend sa manette").isEqualTo(1);
	}

	@Test
	void backGoesFromControlsToCharacterThenUndoes() {
		lobby.apply(new MoveRow(1));
		lobby.apply(new ChangeValue(1)); // Clyde
		lobby.apply(new LobbyCommand.Confirm());
		lobby.apply(new LobbyCommand.Back());
		assertThat(lobby.activeColumn()).isEqualTo(Column.CHARACTER);
		lobby.apply(new LobbyCommand.Back()); // annule Clyde
		assertThat(lobby.rows().get(1).isEmpty()).isTrue();
	}
}
