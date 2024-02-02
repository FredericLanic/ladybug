package com.kycox.game.controller.xboxone;

import com.kycox.game.os.WindowsHost;
import com.studiohartman.jamepad.ControllerManager;
import jakarta.annotation.PostConstruct;
import lombok.Getter;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;


@Component
@Conditional(WindowsHost.class)
public class XBoxOneControllerManager {

	@Getter
	private ControllerManager controllerManager;

	@PostConstruct
	public void initialize() {
		controllerManager = new ControllerManager();
		controllerManager.initSDLGamepad();
	}
}
