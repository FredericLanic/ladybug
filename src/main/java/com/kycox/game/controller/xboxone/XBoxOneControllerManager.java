package com.kycox.game.controller.xboxone;

import com.studiohartman.jamepad.ControllerManager;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;


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
