package com.kycox.game.contract;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;

public interface IMainGraphicStructrure {
	public void add(Component comp, Object constraints);

	default void addPanel(JPanel jPanel, Dimension dimension, String borderLayout) {
		jPanel.setBackground(Color.BLACK);
		jPanel.setPreferredSize(dimension);
		add(jPanel, borderLayout);
	}
}
