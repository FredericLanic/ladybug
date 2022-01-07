package com.kycox.game.view.tests;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.WindowConstants;

public class JSliderVertical extends JPanel {
//	public static void main(String[] args) {
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				JSliderVertical.showFrame();
//			}
//		});
//	}
	public static void showFrame() {
		JPanel panel = new JSliderVertical();
		panel.setOpaque(true);
		var frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setTitle("Vertical JSlider");
		frame.setContentPane(panel);
		frame.pack();
		frame.setVisible(true);
	}

	public JSliderVertical() {
		initializeUI();
	}

	private void initializeUI() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(500, 200));
		// Creates a vertical JSlider that accept value in the
		// range between 0 and 20. The initial value is set to 4.
		var slider = new JSlider(JSlider.VERTICAL, 0, 20, 4);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setMinorTickSpacing(1);
		slider.setMajorTickSpacing(4);
		add(slider, BorderLayout.CENTER);
	}
}
