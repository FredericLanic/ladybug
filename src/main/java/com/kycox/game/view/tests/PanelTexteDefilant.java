package com.kycox.game.view.tests;

import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.JComponent;

// see : https://www.developpez.net/forums/d394537/java/interfaces-graphiques-java/awt-swing/texte-defilant-bas-sort-jpanel/
public class PanelTexteDefilant {
	private static class PourScroller implements java.awt.event.ActionListener {
		private JComponent m_àScroller;

		private PourScroller(JComponent àScroller) {
			m_àScroller = àScroller;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Container container;
			Point	  positionducomposant;
			container			   = m_àScroller.getParent();
			positionducomposant	   = m_àScroller.getLocation();
			positionducomposant.y -= 1;
//			if (positionducomposant.y > m_àScroller.getParent().getSize().height)
//				positionducomposant.y = 0;
			if (positionducomposant.y < -200)
				positionducomposant.y = m_àScroller.getParent().getSize().height;
			positionducomposant.x = 50;
			m_àScroller.setLocation(positionducomposant);
			m_àScroller.getParent().repaint();
		}
	}
//	public static void main(String[] args) {
//		StringBuilder sbText = new StringBuilder();
//		sbText.append("<html>");
//		sbText.append("LadyBug");
//		sbText.append(
//		        "<br/>Vous êtes Ladybug et vous avez très faim. Vous devez manger toutes les pastilles jaunes dans chaque labyrinthe.");
//		sbText.append(
//		        "<br/>Mais le labyrinthe est hanté, il y a des fantômes qui rodent et ils sont mortels et certains agressifs,");
//		sbText.append("<br/>Bonne Chance Ha ha ha ha …. !!!!");
//		sbText.append("<br/>Remerciements");
//		sbText.append(
//		        "<br/>Merci à Oxiane qui m’a permis d’apprendre la programmation Java ; j’ai pu y apporter les outils que je vais utiliser dans le cadre professionnel,");
//		sbText.append("<br/>Merci à Cyril C. qui a apporté une facilité non négligeable à rajouter des niveaux,");
//		sbText.append("<br/>Merci à Kykx et ExaltiTrt6, des tous jeunes geek, qui m’ont permis de");
//		sbText.append("<br/>tester en jouant");
//		sbText.append("<br/>apporter de nouvelles idées aux jeu,");
//		sbText.append("<br/>créer des nouveaux niveaux");
//		sbText.append("<br/>Projet sous copyright (voir le fichier COPYING dans chaque distribution)");
//		sbText.append("<br/>Les sources sont disponibles à https://github.com/FredericLanic/ladybug");
//		sbText.append("</html>");
//		EventQueue.invokeLater(new java.lang.Runnable() {
//			@Override
//			public void run() {
//				JFrame		 jf;
//				JPanel		 zonescroll;
//				JLabel		 àscroller;
//				Timer		 timing;
//				PourScroller pourscroller;
//				jf = new JFrame();
//				jf.setLayout(new BorderLayout());
//				zonescroll = new JPanel();
//				zonescroll.setPreferredSize(new Dimension(200, 400));
//				zonescroll.setLayout(null);
//				àscroller = new JLabel(sbText.toString());
//				àscroller.setSize(àscroller.getPreferredSize());
//				zonescroll.add(àscroller);
//				jf.add(zonescroll);
//				timing = new Timer(40, new PourScroller(àscroller));
//				timing.start();
//				jf.pack();
//				jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//				jf.setLocationRelativeTo(null);
//				jf.setVisible(true);
//			}
//		});
//	}
}