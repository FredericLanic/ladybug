package com.kycox.ladybug.level;

import java.awt.Point;

import lombok.Getter;

public class Teleportation {
	@Getter
	private Point coordinateFrom;
	@Getter
	private Point coordinateTo;

	public Teleportation(Point coordinateFrom, Point coordinateTo) {
		this.coordinateFrom	= coordinateFrom;
		this.coordinateTo	= coordinateTo;
	}
}
