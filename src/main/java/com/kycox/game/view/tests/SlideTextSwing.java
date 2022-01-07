package com.kycox.game.view.tests;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.Timer;

public class SlideTextSwing {
//	public static void main(String args[]) {
//		EventQueue.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				SlideTextSwing windowTest = new SlideTextSwing();
//			}
//		});
//	}
	private JLabel label = new JLabel("Slide Text Swing, Slide Text Swing, ..........");
	private JWindow window = new JWindow();
	private JPanel windowContents = new JPanel();

	public SlideTextSwing() {
		windowContents.add(label);
		window.add(windowContents);
		window.pack();
		window.setLocationRelativeTo(null);
		final var desiredWidth = window.getWidth();
		window.getContentPane().setLayout(null);
		window.setSize(0, window.getHeight());
		window.setVisible(true);
		var timer = new Timer(20, e -> {
			int newWidth = Math.min(window.getWidth() + 1, desiredWidth);
			window.setSize(newWidth, window.getHeight());
			windowContents.setLocation(newWidth - desiredWidth, 0);
			if (newWidth >= desiredWidth) {
				((Timer) e.getSource()).stop();
				label.setForeground(Color.red);
				mainKill();
			}
		});
		timer.start();
	}

	public void mainKill() {
		var timer = new Timer(500, e -> System.exit(0));
		timer.start();
	}
}
