package com.kycox.game.launcher;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kycox.game.Application;
import com.kycox.game.body.GhostsGroup;
import com.kycox.game.body.ghost.Ghost;
import com.kycox.game.engine.model.GameModel;

/**
 * Test 3.3 (cas 2.3) : un XML imposé par {@code ladybug.config} remplace celui
 * du jar. Ici, une copie sans Clyde dans le groupe de fantômes.
 */
@SpringBootTest(classes = Application.class,
        properties = ConfigLocations.SPRING_XML_PROPERTY + "=classpath:test-context-without-clyde.xml")
class ExternalSpringXmlTest {
	static {
		System.setProperty(GameModel.LOOP_AUTOSTART_PROPERTY, "false");
	}

	@Autowired
	private GhostsGroup ghostsGroup;

	@Test
	void theImposedXmlReplacesTheOneInTheJar() {
		assertThat(ghostsGroup.getGhosts()).extracting(Ghost::getName)
		        .containsExactly("Blinky", "Inky", "Pinky");
	}
}
