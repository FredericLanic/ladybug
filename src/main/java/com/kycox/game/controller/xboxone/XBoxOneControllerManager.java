package com.kycox.game.controller.xboxone;

import com.studiohartman.jamepad.ControllerManager;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class XBoxOneControllerManager {

	@Getter
	private ControllerManager controllerManager;

	@PostConstruct
	public void initialize() {
		controllerManager = new ControllerManager();
		controllerManager.initSDLGamepad();
	}
}
