package com.kycox.game.launcher;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import com.kycox.game.Application;
import com.kycox.game.body.GhostsGroup;
import com.kycox.game.body.ghost.Ghost;
import com.kycox.game.engine.model.GameModel;
import com.kycox.game.engine.view.MainFrame;

/**
 * Test 3.3 : le vrai contexte Spring du jeu (Spring Boot + application-context.xml)
 * se charge sans écran. Le profil « gui » n'est pas actif : ni fenêtre, ni vue,
 * ni clavier, ni son ; seulement le modèle.
 */
@SpringBootTest(classes = Application.class)
class SpringContextTest {
	static {
		// la boucle du jeu ne démarre pas : le test reste maître du temps (3.5.1e)
		System.setProperty(GameModel.LOOP_AUTOSTART_PROPERTY, "false");
	}

	@Autowired
	private ApplicationContext context;
	@Autowired
	private GhostsGroup ghostsGroup;

	@Test
	void theXmlCreatesTheFourGhosts() {
		assertThat(ghostsGroup.getGhosts()).extracting(Ghost::getName)
		        .containsExactly("Clyde", "Blinky", "Inky", "Pinky");
	}

	@Test
	void theModelIsThereButNoWindow() {
		assertThat(context.getBean(GameModel.class)).isNotNull();
		assertThat(context.getBeansOfType(MainFrame.class)).isEmpty();
		assertThat(context.getEnvironment().getActiveProfiles()).doesNotContain(GuiComponent.PROFILE);
	}
}
