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
import org.springframework.stereotype.Component;

import javax.swing.SwingUtilities;

/**
 * Ouvre la fenêtre du jeu une fois le contexte Spring prêt (étape 2.1).
 *
 * <p>Spring Boot appelle {@link #run(ApplicationArguments)} à la fin du
 * démarrage. La fenêtre est affichée sur le fil Swing (EDT) par
 * {@link SwingUtilities#invokeLater(Runnable)}, comme le veut Swing.
 */
@Component
public class MainLadybug implements ApplicationRunner {
	private static final String cruxLine = "******************************";
	private static final Logger logger = LoggerFactory.getLogger(MainLadybug.class);

	private final MainFrame mainFrame;

	public MainLadybug(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	@Override
	public void run(ApplicationArguments args) {
		SwingUtilities.invokeLater(this::launchTheGame);
	}

	public void launchTheGame() {
		var javaVersion = Runtime.version();
		logger.info(cruxLine);
		logger.info("*  LadyBug v0.6.0-SNAPSHOT   *");
		logger.info("*  for Java 19+ by Kycox     *");
		if (javaVersion.feature() >= 19) {
			logger.info(cruxLine);
			mainFrame.setVisible(true);
		} else {
			logger.info("*   Please Check your JVM    *");
			logger.info(cruxLine);
			System.exit(2);
		}
	}
}
