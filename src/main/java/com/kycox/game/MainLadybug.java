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

import java.lang.Runtime.Version;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.kycox.game.view.MainFrame;

/**
 * Classe principale du jeu
 *
 * C'est elle qui faut lancer :)
 */
public class MainLadybug {
	private static String	 cruxLine = "******************************";
	private static final Log logger	  = LogFactory.getLog(MainLadybug.class);

	/** Lancement du jeu */
	public static void main(String[] args) {
		new MainLadybug().launchTheGame();
	}

	/** lecture du contexte Spring de l'application */
	private ApplicationContext applicationContext = new ClassPathXmlApplicationContext("application-context.xml");
	@Autowired
	private MainFrame		   mainFrame		  = applicationContext.getBean("MainFrame", MainFrame.class);

	public void launchTheGame() {
		Version javaVersion = Runtime.version();
		logger.info(cruxLine);
		logger.info("*  LadyBug v0.3.0 SnapShot   *");
		logger.info("*  for Java 14+ by kycox     *");
		if (javaVersion.feature() >= 14) {
			logger.info(cruxLine);
			mainFrame.setVisible(true);
		} else {
			logger.info("*   Please Check your JVM    *");
			logger.info(cruxLine);
		}
	}
}
