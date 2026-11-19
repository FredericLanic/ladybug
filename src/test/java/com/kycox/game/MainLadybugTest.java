package com.kycox.game;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Version du jeu (étape 2.2) : lue dans le manifest du jar, jamais écrite en dur.
 */
class MainLadybugTest {

	@Test
	void versionIsNeverHardCoded() {
		// En test, les classes ne viennent pas du jar : pas de manifest, donc la
		// version de développement. Dans le jar, c'est la version du pom.
		var expected = MainLadybug.class.getPackage().getImplementationVersion();

		assertThat(MainLadybug.gameVersion())
		        .isEqualTo(expected != null ? expected : MainLadybug.DEVELOPMENT_VERSION);
	}
}
