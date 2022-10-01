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

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

@Component
public class GameProperties {
	// @TODO : properties à la mode de Spring
	private static final Log logger = LogFactory.getLog(GameProperties.class);
	private static final String TRUE_PROPERTY = "1";
	private Optional<String> ghostHat = Optional.empty();
	private Optional<String> ghostsHeadband = Optional.empty();
	private Optional<String> ladybugColor = Optional.empty();
	private Optional<String> ladybugSkin = Optional.empty();
	private Properties props;

	public void changeGhostHat() {
		if (hasHatSkin()) {
			ghostHat = Optional.empty();
		} else {
			ghostHat = Optional.of(TRUE_PROPERTY);
		}
	}

	public void changeGhostHeadBand() {
		if (hasGhostHeadBand()) {
			ghostsHeadband = Optional.empty();
		} else {
			ghostsHeadband = Optional.of(TRUE_PROPERTY);
		}
	}

	public void changeLadybugSkin() {
		if (hasLadybugSkin()) {
			ladybugSkin = Optional.empty();
		} else {
			ladybugSkin = Optional.of(TRUE_PROPERTY);
		}
	}

	private void displayPropertiesInLog() {
		logger.info("Game Properties");
		logger.info("Ladybug color:" + getLadybugColor());
		logger.info("Ladybug skin:" + hasLadybugSkin());
		logger.info("Ghost headband:" + hasGhostHeadBand());
	}

	public String getLadybugColor() {
		return ladybugColor.isPresent() ? ladybugColor.get() : "blue";
	}

	public boolean hasGhostHeadBand() {
		return ghostsHeadband.isPresent() && TRUE_PROPERTY.equals(ghostsHeadband.get());
	}

	public boolean hasHatSkin() {
		return ghostHat.isPresent() && TRUE_PROPERTY.equals(ghostHat.get());
	}

	public boolean hasLadybugSkin() {
		return ladybugSkin.isPresent() && TRUE_PROPERTY.equals(ladybugSkin.get());
	}

	@PostConstruct
	public void init() {
		try {
			props = PropertiesLoaderUtils.loadProperties(new ClassPathResource("ladybug.properties"));
			ladybugColor = readProperty("ladybug.color");
			ladybugSkin = readProperty("ladybug.skin");
			ghostsHeadband = readProperty("ghosts.headband");
			ghostHat = readProperty("ghosts.hat");
		} catch (IOException ioException) {
			logger.error(ioException);
		}
		displayPropertiesInLog();
	}

	private Optional<String> readProperty(String property) {
		return Optional.ofNullable(props.getProperty(property));
	}
}
