package com.kycox.game.constant.ladybug;

import java.awt.Image;
import java.util.EnumSet;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.ImageIcon;

import org.springframework.stereotype.Component;

import com.kycox.game.constant.GameImages;
import com.kycox.game.properties.GameProperties;

import lombok.Getter;
import lombok.Setter;

public enum LadybugImages {
		
    // ladybug
	LADYBUG_UP_1("up1.png"), LADYBUG_UP_2("up2.png"),
	LADYBUG_UP_3("up3.png"), LADYBUG_UP_4("up4.png"),
	LADYBUG_UP_5("up5.png"), LADYBUG_UP_6("up6.png"),
	LADYBUG_UP_7("up7.png"), LADYBUG_UP_8("up8.png"),
	LADYBUG_UP_9("up9.png"), LADYBUG_UP_FULL("upFull.png");

	@Getter
	private Image image;
	@Setter
	private String color;
	private String fileName;
	
	@Component
    public static class GamePropertiesInjector {
		
		@Inject 
		private GameProperties 	 	 gameProperties;

		@PostConstruct
        public void postConstruct() {
            for (LadybugImages rt : EnumSet.allOf(LadybugImages.class)) {
            	String pathName = "images/ladybug/color/" + gameProperties.getLadybugColor() + "/";
            	rt.setImage(pathName);
            }
        }
    }
	
	private LadybugImages(String fileName) {
		this.fileName = fileName;
	}
		
	private void setImage(String pathName) {	
		image = new ImageIcon(GameImages.class.getClassLoader().getResource(pathName + fileName)).getImage();		
	}
	
}
