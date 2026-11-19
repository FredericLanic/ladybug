/**
  Copyright (C) 2021  Frédéric Lanic frederic.lanic@outlook.fr

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.kycox.game;

import com.kycox.game.engine.view.MainFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import com.kycox.game.launcher.GuiComponent;

import javax.swing.SwingUtilities;

/**
 * Ouvre la fenêtre du jeu une fois le contexte Spring prêt (étape 2.1).
 *
 * <p>Spring Boot appelle {@link #run(ApplicationArguments)} à la fin du
 * démarrage. La fenêtre est affichée sur le fil Swing (EDT) par
 * {@link SwingUtilities#invokeLater(Runnable)}, comme le veut Swing.
 */
@GuiComponent
public class MainLadybug implements ApplicationRunner {
	/** Version affichée quand le jeu est lancé hors du jar (depuis l'IDE). */
	static final String DEVELOPMENT_VERSION = "development";
	private static final Logger logger = LoggerFactory.getLogger(MainLadybug.class);

	private final MainFrame mainFrame;

	public MainLadybug(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	@Override
	public void run(ApplicationArguments args) {
		SwingUtilities.invokeLater(this::launchTheGame);
	}

	/**
	 * Version du jeu, lue dans le manifest du jar ({@code Implementation-Version},
	 * écrite par Maven à partir du pom) : plus de numéro en dur (étape 2.2, D03).
	 */
	static String gameVersion() {
		var version = MainLadybug.class.getPackage().getImplementationVersion();
		return version != null ? version : DEVELOPMENT_VERSION;
	}

	/**
	 * Affiche la fenêtre du jeu.
	 *
	 * <p>L'ancien contrôle « Java 19+ » a été retiré (étape 2.2) : le jeu est
	 * compilé pour Java 21 (pom), et une JVM plus ancienne refuse de charger ses
	 * classes ({@code UnsupportedClassVersionError}) avant même d'arriver ici. Le
	 * test ne pouvait donc jamais échouer.
	 */
	public void launchTheGame() {
		logger.info("LadyBug {} by Kycox, running on Java {}", gameVersion(), Runtime.version());
		mainFrame.setVisible(true);
	}
}
