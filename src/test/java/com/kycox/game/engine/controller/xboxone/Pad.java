package com.kycox.game.engine.controller.xboxone;

/**
 * Fausse manette pour les tests (étape 3.5.5) : on ne précise que ce qui compte.
 * <pre>Pad.connected().a().leftStick(-1, 0).build()</pre>
 */
public final class Pad {
	private boolean connected;
	private float leftX;
	private float leftY;
	private float rightX;
	private float rightY;
	private boolean a;
	private boolean b;
	private boolean x;
	private boolean y;
	private boolean back;
	private boolean dpadUp;
	private boolean dpadDown;
	private boolean dpadLeft;
	private boolean dpadRight;
	private boolean start;
	private boolean leftClick;
	private boolean rightClick;

	public static Pad connected() {
		var pad = new Pad();
		pad.connected = true;
		return pad;
	}

	public static PadState disconnected() {
		return PadState.disconnected();
	}

	public Pad leftStick(float horizontal, float vertical) { leftX = horizontal; leftY = vertical; return this; }
	public Pad rightStick(float horizontal, float vertical) { rightX = horizontal; rightY = vertical; return this; }
	public Pad a() { a = true; return this; }
	public Pad b() { b = true; return this; }
	public Pad x() { x = true; return this; }
	public Pad y() { y = true; return this; }
	public Pad back() { back = true; return this; }
	public Pad dpadUp() { dpadUp = true; return this; }
	public Pad dpadDown() { dpadDown = true; return this; }
	public Pad dpadLeft() { dpadLeft = true; return this; }
	public Pad dpadRight() { dpadRight = true; return this; }
	public Pad start() { start = true; return this; }
	public Pad rightStickClick() { rightClick = true; return this; }

	public PadState build() {
		return new PadState(connected, leftX, leftY, rightX, rightY, a, b, x, y, back, dpadUp, dpadDown, dpadLeft,
		        dpadRight, start, leftClick, rightClick);
	}
}
