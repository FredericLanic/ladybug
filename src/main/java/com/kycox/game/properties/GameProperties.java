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
	// ladybug.color
	private Optional<String> ladybugColor = Optional.empty();
	// ladybug.ski
	private Optional<String> ladybugSkin = Optional.empty();
	
	private Optional<String> ladybugLeft = Optional.empty();
	private Optional<String> ladybugRight = Optional.empty();
	private Optional<String> ladybugUp = Optional.empty();
	private Optional<String> ladybugDown = Optional.empty();	
	
	// ghosts.headband
	private Optional<String> ghostsHeadband = Optional.empty();
	
	private ClassPathResource classPathResource;
	private Properties props;
	
	private static String TRUE_PROPERTY = "1";
	private static final Log	 logger		   = LogFactory.getLog(GameProperties.class);
	
	@PostConstruct
	public void init() {
		try {
			classPathResource = new ClassPathResource("ladybug.properties");
			props = PropertiesLoaderUtils.loadProperties(classPathResource);
			ladybugColor = readProperty("ladybug.color");
			ladybugSkin = readProperty("ladybug.skin");
			
			ladybugLeft = readProperty("ladybug.left");
			ladybugRight = readProperty("ladybug.right");
			ladybugUp = readProperty("ladybug.up");
			ladybugDown = readProperty("ladybug.down");
			
			ghostsHeadband = readProperty("ghosts.headband");
		} 
		catch (IOException ioException)
		{
			logger.error(ioException);
		}
		displayPropertiesInLog();
	}
	
	private void displayPropertiesInLog() {
		logger.info("Game Properties");
		logger.info("Ladybug color:" + getLadybugColor());
		logger.info("Ladybug skin:" + hasLadybugSkin());
		logger.info("Ghost headband:" + hasGhostHeadBand());
	}
	
	private Optional<String> readProperty(String property) {				
		return Optional.ofNullable(props.getProperty(property));		
	}
	
	public String getLadybugColor() {
		return ladybugColor.isPresent() ? ladybugColor.get(): "blue";
	}

	public boolean hasLadybugSkin() {
		return ladybugSkin.isPresent() && TRUE_PROPERTY.equals(ladybugSkin.get());
	}

	public boolean hasGhostHeadBand() {
		return ghostsHeadband.isPresent() && TRUE_PROPERTY.equals(ghostsHeadband.get());
	}
	
	public int getLadybugLeft() {
		return ladybugLeft.isPresent()?Integer.parseInt(ladybugLeft.get()): 0x25;
	}

}

