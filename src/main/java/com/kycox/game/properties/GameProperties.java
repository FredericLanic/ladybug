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
package com.kycox.game.properties;

import jakarta.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:ladybug.properties")
public class GameProperties {
	private static final Log logger = LogFactory.getLog(GameProperties.class);
	private static final String TRUE_PROPERTY = "1";
	private static final String FALSE_PROPERTY = "0";

	@Value( "${ghosts.hat}" )
	private String ghostHat;
	@Value( "${ghosts.headband}" )
	private String ghostsHeadband;
	@Value( "${ladybug.color}" )
	private String ladybugColor;
	@Value( "${ladybug.skin}" )
	private String ladybugSkin;

	public void changeGhostHat() {
		if (hasHatSkin()) {
			ghostHat = FALSE_PROPERTY;
		} else {
			ghostHat = TRUE_PROPERTY;
		}
	}

	public void changeGhostHeadBand() {
		if (hasGhostHeadBand()) {
			ghostsHeadband = FALSE_PROPERTY;
		} else {
			ghostsHeadband = TRUE_PROPERTY;
		}
	}

	public void changeLadybugSkin() {
		if (hasLadybugSkin()) {
			ladybugSkin = FALSE_PROPERTY;
		} else {
			ladybugSkin = TRUE_PROPERTY;
		}
	}

	private void displayPropertiesInLog() {
		logger.info("Game Properties");
		logger.info("Ladybug color:" + getLadybugColor());
		logger.info("Ladybug skin:" + hasLadybugSkin());
		logger.info("Ghost headband:" + hasGhostHeadBand());
	}

	public String getLadybugColor() {
		return ladybugColor;
	}

	public boolean hasGhostHeadBand() {
		return ghostsHeadband.equals(TRUE_PROPERTY);
	}

	public boolean hasHatSkin() {
		return ghostHat.equals(TRUE_PROPERTY);
	}

	public boolean hasLadybugSkin() {
		return ladybugSkin.equals(TRUE_PROPERTY);
	}

	@PostConstruct
	public void init() {
		displayPropertiesInLog();
	}
}
