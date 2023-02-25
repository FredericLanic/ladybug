package com.kycox.game.view.map;

import com.kycox.game.tools.Utils;
import com.kycox.game.view.map.rendering.Rendering;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ManageRendering {

	private final List<Rendering> renderings;

	public ManageRendering(List<Rendering> renderings) {
		this.renderings = renderings;
	}

	public Rendering getRamdomizeRendering() {
		return renderings.get(Utils.generateRandomInt(renderings.size()));
	}

	public Rendering getFirstRendering() {
		return renderings.get(0);
	}
}
