package com.kycox.game.engine.score;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Point;

import org.junit.jupiter.api.Test;

/**
 * Tests des messages affichés en battements (étape 3.5.1d) : 1 s = 25 battements.
 */
class GroupMessagesTest {

	private static void tick(GroupMessages group, int beats) {
		for (var i = 0; i < beats; i++) {
			group.tick();
		}
	}

	@Test
	void messageLivesTwentyFiveBeats() {
		var message = new Message(new Point(1, 2), "15", MessageType.POINT);

		for (var i = 0; i < 24; i++) {
			message.tick();
		}
		assertThat(message.isDying()).isFalse();

		message.tick();
		assertThat(message.isDying()).isTrue();
	}

	@Test
	void dyingMessagesAreRemovedOthersStay() {
		var group = new GroupMessages();
		group.add(new Point(0, 0), "15", MessageType.POINT);
		tick(group, 10);
		group.add(new Point(1, 1), "100", MessageType.POINT);

		tick(group, 15); // le premier a 25 battements, le second 15
		group.removeIfDying();

		assertThat(group.getMessages()).extracting(Message::getValue).containsExactly("100");

		tick(group, 10);
		group.removeIfDying();
		assertThat(group.getMessages()).isEmpty();
	}

	@Test
	void messageKeepsItsOwnPosition() {
		var group = new GroupMessages();
		var position = new Point(3, 4);
		group.add(position, "15", MessageType.POINT);
		position.translate(1, 1); // la position d'origine bouge ensuite

		assertThat(group.getMessages().get(0).getPosition()).isEqualTo(new Point(3, 4));
	}
}
