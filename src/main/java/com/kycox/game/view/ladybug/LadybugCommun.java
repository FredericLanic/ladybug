package com.kycox.game.view.ladybug;

import java.awt.Image;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.kycox.game.constant.Constants;
import com.kycox.game.constant.GameImages;
import com.kycox.game.constant.ladybug.LadybugImages;
import com.kycox.game.properties.GameProperties;
import com.kycox.game.tools.ImageUtils;
import com.kycox.game.view.body.BodyImg;

import lombok.Getter;
import lombok.Setter;

@Named("LadybugCommun")
public abstract class LadybugCommun {
	@Getter
	@Setter
	private BodyImg				bodyUpCurrent;
	private Map<Point, Integer>	convertPointToDegrees = new HashMap<>();

	@Inject 
	private GameProperties 	 	 gameProperties;
	
	public Image getImage(Point direction) {
		Image ladybugImage = ImageUtils.rotateImage(bodyUpCurrent.getImage(), convertPointToDegrees.get(direction));
		return addSkin(ladybugImage, direction);
	}

	public Image getStaticView() {
		Point direction	   = Constants.POINT_RIGHT;
		Image ladybugImage = ImageUtils.rotateImage(LadybugImages.LADYBUG_UP_2.getImage(),
		        convertPointToDegrees.get(direction));
		
		return addSkin(ladybugImage, direction);
	}

	private Image addSkin(Image image, Point direction) {
		if (gameProperties.hasLadybugSkin()) {		
			Image imagePluginOX = ImageUtils.rotateImage(GameImages.LADYBUG_PLUGIN_OX_UP.getImage(),
			        convertPointToDegrees.get(direction));
			return ImageUtils.appendImages(image, imagePluginOX);
		}
		return image;
	}

	@PostConstruct
	private void initConvertPointToDegrees() {
		convertPointToDegrees.put(Constants.POINT_UP, 0);
		convertPointToDegrees.put(Constants.POINT_ZERO, 0);
		convertPointToDegrees.put(Constants.POINT_LEFT, 90);
		convertPointToDegrees.put(Constants.POINT_RIGHT, -90);
		convertPointToDegrees.put(Constants.POINT_DOWN, 180);
	}

	protected void addConvertPointToDegrees(Point point, Integer integer) {
		convertPointToDegrees.put(point, integer);
	}

	protected void setNextImage() {
		bodyUpCurrent = bodyUpCurrent.getNext();
	}
}
