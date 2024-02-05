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

import com.kycox.game.view.MainFrame;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

//@SpringBootApplication
public class MainLadybug {
	private static final String cruxLine = "******************************";
	private static final Log logger = LogFactory.getLog(MainLadybug.class);

	/** Lancement du jeu */
	public static void main(String[] args) {
		new MainLadybug().launchTheGame();
	}

//	public static void main(String[] args) {
//		SpringApplication.run(MainLadybug.class, args);
//	}

	/** lecture du contexte Spring de l'application */
	private final ApplicationContext applicationContext = new ClassPathXmlApplicationContext("application-context.xml");
	private final MainFrame mainFrame = applicationContext.getBean("MainFrame", MainFrame.class);

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
		}
	}
}
