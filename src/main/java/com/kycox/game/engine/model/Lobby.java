package com.kycox.game.engine.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kycox.game.body.GhostsGroup;
import com.kycox.game.body.ghost.Ghost;
import com.kycox.game.engine.model.LobbyCommand.ChangeCharacter;
import com.kycox.game.engine.model.LobbyCommand.ChangeControls;
import com.kycox.game.engine.model.LobbyCommand.ChangeValue;
import com.kycox.game.engine.model.LobbyCommand.Undo;
import com.kycox.game.engine.model.LobbyCommand.Claim;
import com.kycox.game.engine.model.LobbyCommand.MoveRow;
import com.kycox.game.engine.model.LobbyCommand.SwitchColumn;
import com.kycox.game.input.InputSource;

/**
 * Le salon (étapes 20.2 et 20.3, idée I20) : avant une partie à plusieurs, une
 * ligne par joueur, avec son personnage et ses commandes.
 *
 * <p>Une seule personne configure tout : une ligne active (surlignée à l'écran),
 * une colonne active (personnage ou commandes). Chaque modification est une
 * {@link LobbyCommand} : le clavier et les manettes les produisent ; demain, les
 * postes du réseau enverront les mêmes, et l'hôte restera seul à les appliquer
 * (étape 9).
 *
 * <p>Les règles :
 * <ul>
 * <li>personnages : Cox (Ladybug) ou un fantôme ; Cox doit avoir un joueur ;</li>
 * <li>commandes : flèches, ZQSD, IJKL, manettes 1 à 4, ou « appuie sur A » : la
 * première manette sur laquelle on appuie sur A prend la ligne (C56) ;</li>
 * <li>un personnage et des commandes ne servent qu'à une ligne ;</li>
 * <li>au plus 3 fantômes humains, et toujours au moins un fantôme pour
 * l'ordinateur (C52) ; les fantômes sans ligne sont joués par l'ordinateur.</li>
 * </ul>
 */
@Component
public class Lobby {
	/** Le nom de Ladybug parmi les personnages. */
	public static final String LADYBUG = "Ladybug";
	/** Au plus 3 fantômes joués par des humains (C52). */
	static final int MAX_HUMAN_GHOSTS = 3;
	/** Les commandes possibles, dans l'ordre de la liste ; {@code null} : « appuie sur A ». */
	private static final List<InputSource> CONTROLS = Arrays.asList(InputSource.KEYBOARD_ARROWS,
	        InputSource.KEYBOARD_ZQSD, InputSource.KEYBOARD_IJKL, InputSource.XBOX_PAD_1, InputSource.XBOX_PAD_2,
	        InputSource.XBOX_PAD_3, InputSource.XBOX_PAD_4, null);

	/** La colonne active de la ligne active. */
	public enum Column {
		CHARACTER, CONTROLS
	}

	/**
	 * Une ligne : le personnage ({@code null} : pas de joueur), ses commandes
	 * ({@code null} tant qu'elles ne sont pas choisies), et l'attente d'une manette
	 * (« appuie sur A »).
	 */
	public record Row(String character, InputSource device, boolean waitingForPad) {
		public boolean isEmpty() {
			return character == null;
		}

		public boolean isLadybug() {
			return LADYBUG.equals(character);
		}

		/** Un personnage et des commandes : la ligne peut jouer. */
		public boolean isComplete() {
			return character != null && device != null;
		}

		static Row empty() {
			return new Row(null, null, false);
		}
	}

	private final Supplier<List<String>> ghostNames;
	private final List<Row> rows = new ArrayList<>();
	/** Pour chaque ligne, ses états précédents : « annuler » revient au dernier. */
	private final List<java.util.Deque<Row>> history = new ArrayList<>();
	private int activeRow;
	/** Les manettes branchées (aucune tant que le contrôleur n'en a pas signalé). */
	private java.util.Set<InputSource> connectedPads = java.util.Set.of();
	private Column activeColumn = Column.CHARACTER;

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

	/** Les lignes : Cox et les fantômes jouables (créées au premier usage, puis gardées d'une partie à l'autre). */
	public List<Row> rows() {
		ensureRows();
		return List.copyOf(rows);
	}

	public int activeRow() {
		return activeRow;
	}

	public Column activeColumn() {
		return activeColumn;
	}

	/** Revient sur la première ligne, colonne « personnage » (à l'ouverture du salon). */
	public void resetCursor() {
		activeRow = 0;
		activeColumn = Column.CHARACTER;
	}

	/** Applique une modification : la seule porte d'entrée du salon. */
	public void apply(LobbyCommand command) {
		ensureRows();
		switch (command) {
			case MoveRow move -> activeRow = Math.floorMod(activeRow + move.step(), rows.size());
			case SwitchColumn switchColumn ->
				activeColumn = activeColumn == Column.CHARACTER ? Column.CONTROLS : Column.CHARACTER;
			case ChangeValue change -> {
				if (activeColumn == Column.CHARACTER) {
					changeCharacter(change.step());
				} else {
					changeControls(change.step());
				}
			}
			case ChangeCharacter change -> changeCharacter(change.step());
			case ChangeControls change -> changeControls(change.step());
			case Undo undo -> undo();
			case LobbyCommand.Confirm confirm -> confirm();
			case LobbyCommand.Back back -> back();
			case Claim claim -> claim(claim.device());
		}
	}

	/** Remplace une ligne en gardant son état précédent, pour pouvoir l'annuler. */
	private void setRow(int index, Row row) {
		var previous = rows.get(index);
		if (!previous.equals(row)) {
			history.get(index).push(previous);
			rows.set(index, row);
		}
	}

	/** Le personnage d'abord, puis les commandes, puis la ligne suivante. */
	private void confirm() {
		var row = rows.get(activeRow);
		if (activeColumn == Column.CHARACTER && !row.isEmpty()) {
			activeColumn = Column.CONTROLS;
		} else if (activeColumn == Column.CHARACTER || !row.waitingForPad()) {
			// ligne sans joueur, ou commandes choisies : ligne suivante
			activeRow = Math.floorMod(activeRow + 1, rows.size());
			activeColumn = Column.CHARACTER;
		}
		// commandes en attente d'une manette : c'est la manette qui appuie qui doit la prendre
	}

	private void back() {
		if (activeColumn == Column.CONTROLS) {
			activeColumn = Column.CHARACTER;
		} else {
			undo();
		}
	}

	private void undo() {
		var previous = history.get(activeRow).poll();
		if (previous != null) {
			rows.set(activeRow, previous);
		}
	}

	/** Prêt quand Cox a un joueur et que chaque ligne occupée a ses commandes. */
	public boolean isReady() {
		ensureRows();
		return rows.stream().anyMatch(row -> row.isLadybug() && row.isComplete())
		        && rows.stream().filter(row -> !row.isEmpty()).allMatch(Row::isComplete);
	}

	/** Quel appareil pilote quel personnage (pour 20.4). */
	public Map<InputSource, String> assignments() {
		ensureRows();
		var assignments = new LinkedHashMap<InputSource, String>();
		rows.stream().filter(Row::isComplete).forEach(row -> assignments.put(row.device(), row.character()));
		return assignments;
	}

	private void ensureRows() {
		var size = 1 + maxHumanGhosts();
		if (rows.size() == size) {
			return;
		}
		rows.clear();
		history.clear();
		rows.add(new Row(LADYBUG, InputSource.KEYBOARD_ARROWS, false));
		while (rows.size() < size) {
			rows.add(Row.empty());
		}
		rows.forEach(row -> history.add(new java.util.ArrayDeque<>()));
		activeRow = 0;
	}

	private void changeCharacter(int step) {
		var row = rows.get(activeRow);
		var choices = new ArrayList<String>();
		choices.add(null); // pas de joueur
		choices.add(LADYBUG);
		choices.addAll(ghostNames.get());
		var index = choices.indexOf(row.character());
		for (var tries = 1; tries < choices.size(); tries++) {
			var candidate = choices.get(Math.floorMod(index + step * tries, choices.size()));
			if (canTakeCharacter(candidate)) {
				setCharacter(candidate);
				return;
			}
		}
	}

	private boolean canTakeCharacter(String character) {
		if (character == null) {
			return true;
		}
		if (otherRows().anyMatch(row -> character.equals(row.character()))) {
			return false;
		}
		return LADYBUG.equals(character) || otherRows().filter(row -> !row.isEmpty() && !row.isLadybug())
		        .count() < maxHumanGhosts();
	}

	private void setCharacter(String character) {
		var row = rows.get(activeRow);
		if (character == null) {
			setRow(activeRow, Row.empty());
		} else if (row.isEmpty()) {
			// une ligne qui s'ouvre prend les premières commandes libres du clavier
			var free = CONTROLS.stream().filter(Objects::nonNull).limit(3).filter(this::isFreeControl).findFirst();
			setRow(activeRow, new Row(character, free.orElse(null), free.isEmpty()));
		} else {
			setRow(activeRow, new Row(character, row.device(), row.waitingForPad()));
		}
	}

	private void changeControls(int step) {
		var row = rows.get(activeRow);
		if (row.isEmpty()) {
			return;
		}
		var index = row.waitingForPad() ? CONTROLS.size() - 1 : CONTROLS.indexOf(row.device());
		for (var tries = 1; tries < CONTROLS.size(); tries++) {
			var candidate = CONTROLS.get(Math.floorMod(index + step * tries, CONTROLS.size()));
			var offered = candidate == null ? !connectedPads.isEmpty() : isConnected(candidate) && isFreeControl(candidate);
			if (offered) {
				setRow(activeRow, new Row(row.character(), candidate, candidate == null));
				return;
			}
		}
	}

	/** Le clavier est toujours là ; une manette, seulement si elle est branchée. */
	private boolean isConnected(InputSource device) {
		return !device.name().startsWith("XBOX_PAD") || connectedPads.contains(device);
	}

	/**
	 * Les manettes branchées, signalées à chaque battement par le contrôleur des
	 * manettes (demain, le réseau signalera ses postes de la même façon). Une ligne
	 * dont la manette se débranche repasse en attente « appuie sur A ».
	 */
	public void setConnectedPads(java.util.Set<InputSource> pads) {
		ensureRows();
		connectedPads = java.util.Set.copyOf(pads);
		for (var i = 0; i < rows.size(); i++) {
			var row = rows.get(i);
			if (row.device() != null && !isConnected(row.device())) {
				rows.set(i, new Row(row.character(), null, true));
			}
		}
	}

	private boolean isFreeControl(InputSource device) {
		return otherRows().noneMatch(row -> row.device() == device);
	}

	/** Un appareil s'annonce : il prend la ligne active si elle attend, sinon la première qui attend. */
	private void claim(InputSource device) {
		var target = rows.get(activeRow).waitingForPad() ? activeRow : firstWaitingRow();
		if (target < 0) {
			return;
		}
		for (var i = 0; i < rows.size(); i++) {
			if (i != target && rows.get(i).device() == device) {
				setRow(i, new Row(rows.get(i).character(), null, true));
			}
		}
		setRow(target, new Row(rows.get(target).character(), device, false));
	}

	private int firstWaitingRow() {
		for (var i = 0; i < rows.size(); i++) {
			if (rows.get(i).waitingForPad()) {
				return i;
			}
		}
		return -1;
	}

	private java.util.stream.Stream<Row> otherRows() {
		var active = activeRow;
		return java.util.stream.IntStream.range(0, rows.size()).filter(i -> i != active).mapToObj(rows::get);
	}
}
