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
import org.springframework.stereotype.Component;

//@SpringBootApplication
@Component
public class MainLadybug {
	private static final String cruxLine = "******************************";
	private static final Logger logger = LoggerFactory.getLogger(MainLadybug.class);

	private final MainFrame mainFrame;

	public MainLadybug(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
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
