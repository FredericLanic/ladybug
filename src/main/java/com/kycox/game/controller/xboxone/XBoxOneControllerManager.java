package com.kycox.game.controller.xboxone;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import com.studiohartman.jamepad.ControllerManager;

import lombok.Getter;

@Named("XBoxOneControllerManager")
public class XBoxOneControllerManager {

	@Getter
	private ControllerManager controllerManager;

	@PostConstruct
	public void initialize() {
		controllerManager = new ControllerManager();
		controllerManager.initSDLGamepad();
	}

}
