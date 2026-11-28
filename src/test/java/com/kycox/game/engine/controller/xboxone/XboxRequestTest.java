package com.kycox.game.engine.controller.xboxone;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Lecture d'une manette (étape 3.5.5) : zone morte des sticks, directions,
 * connexion et déconnexion.
 */
class XboxRequestTest {
	private final XboxRequest request = new XboxRequest();

	@Test
	void smallStickMovesAreIgnored() {
		// zone morte : 0,45 ; une manette au repos envoie environ ±0,05
		request.readCurrentState(Pad.connected().leftStick(0.44f, -0.44f).rightStick(-0.44f, 0.44f).build());

		assertThat(request.isLeftLeftStick() || request.isRightLeftStick() || request.isUpLeftStick()
		        || request.isDownLeftStick()).isFalse();
		assertThat(request.isLeftRightStick() || request.isRightRightStick() || request.isUpRightStick()
		        || request.isDownRightStick()).isFalse();
	}

	@Test
	void leftStickDirections() {
		request.readCurrentState(Pad.connected().leftStick(0.46f, 0).build());
		assertThat(request.isRightLeftStick()).isTrue();
		assertThat(request.isLeftLeftStick()).isFalse();

		request.readCurrentState(Pad.connected().leftStick(-0.8f, 0).build());
		assertThat(request.isLeftLeftStick()).isTrue();

		request.readCurrentState(Pad.connected().leftStick(0, 0.8f).build());
		assertThat(request.isUpLeftStick()).isTrue();
		assertThat(request.isDownLeftStick()).isFalse();

		request.readCurrentState(Pad.connected().leftStick(0, -0.8f).build());
		assertThat(request.isDownLeftStick()).isTrue();
	}

	@Test
	void rightStickDirections() {
		request.readCurrentState(Pad.connected().rightStick(0.9f, 0).build());
		assertThat(request.isRightRightStick()).isTrue();
		request.readCurrentState(Pad.connected().rightStick(-0.9f, 0).build());
		assertThat(request.isLeftRightStick()).isTrue();
		request.readCurrentState(Pad.connected().rightStick(0, 0.9f).build());
		assertThat(request.isUpRightStick()).isTrue();
		request.readCurrentState(Pad.connected().rightStick(0, -0.9f).build());
		assertThat(request.isDownRightStick()).isTrue();
	}

	@Test
	void buttonsAreCopied() {
		request.readCurrentState(Pad.connected().build()); // branchée, au repos (D28)
		request.readCurrentState(Pad.connected().a().b().x().y().back().start().dpadUp().dpadDown().dpadLeft()
		        .dpadRight().rightStickClick().build());

		assertThat(request.isAButton()).isTrue();
		assertThat(request.isBButton()).isTrue();
		assertThat(request.isXButton()).isTrue();
		assertThat(request.isYButton()).isTrue();
		assertThat(request.isBackButton()).isTrue();
		assertThat(request.isStart()).isTrue();
		assertThat(request.isDpadUp()).isTrue();
		assertThat(request.isDpadDown()).isTrue();
		assertThat(request.isDpadLeft()).isTrue();
		assertThat(request.isDpadRight()).isTrue();
		assertThat(request.isRightStick()).isTrue();
		assertThat(request.isLeftStick()).isFalse();
	}

	@Test
	void connectionAndDisconnectionAreSignalledOnce() {
		request.readCurrentState(Pad.disconnected());
		assertThat(request.isConnected()).isFalse();
		assertThat(request.isHasBeenConnected()).isFalse();

		request.readCurrentState(Pad.connected().build());
		assertThat(request.isConnected()).isTrue();
		assertThat(request.isHasBeenConnected()).isTrue();

		request.readCurrentState(Pad.connected().build());
		assertThat(request.isHasBeenConnected()).isFalse(); // une seule fois

		request.readCurrentState(Pad.disconnected());
		assertThat(request.isHasBeenDisConnected()).isTrue();
	}

	/**
	 * D28 (étape 20.1) : un bouton tenu enfoncé ne compte qu'une fois, au moment de
	 * l'appui. Avant, la manette relue à chaque battement redéclenchait l'action à
	 * l'écran suivant (une partie quittée 7 ms après son début).
	 */
	@Test
	void aHeldButtonCountsOnlyOnceWhenPressed() {
		var held = Pad.connected().a().b().x().y().start().dpadUp().rightStickClick().build();
		request.readCurrentState(Pad.connected().build()); // branchée, au repos

		request.readCurrentState(held);
		assertThat(request.isAButton() && request.isBButton() && request.isXButton() && request.isYButton()
		        && request.isStart() && request.isDpadUp() && request.isRightStick()).isTrue();

		request.readCurrentState(held);
		assertThat(request.isAButton() || request.isBButton() || request.isXButton() || request.isYButton()
		        || request.isStart() || request.isDpadUp() || request.isRightStick()).isFalse();

		request.readCurrentState(Pad.connected().build()); // relâché
		request.readCurrentState(Pad.connected().a().build()); // de nouveau appuyé
		assertThat(request.isAButton()).isTrue();
	}

	/** Back affiche l'aide tant qu'il est tenu : lui reste « enfoncé », pas « appuyé ». */
	@Test
	void backStaysTrueWhileHeld() {
		var held = Pad.connected().back().build();
		request.readCurrentState(held);
		request.readCurrentState(held);
		assertThat(request.isBackButton()).isTrue();
	}

	/** Une manette branchée avec un bouton déjà enfoncé ne déclenche rien. */
	@Test
	void aButtonHeldWhileConnectingIsNotAPress() {
		request.readCurrentState(Pad.disconnected());
		request.readCurrentState(Pad.connected().a().build());
		assertThat(request.isAButton()).isFalse();
	}
}
