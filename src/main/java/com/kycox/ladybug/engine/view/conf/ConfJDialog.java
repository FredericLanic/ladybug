package com.kycox.ladybug.engine.view.conf;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.kycox.ladybug.constant.PicturesEnum;
import com.kycox.ladybug.engine.element.ghost.set.GhostsSettingsEnum;

public class ConfJDialog extends JDialog implements ActionListener {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  ButtonGroup               group            = new ButtonGroup();

  public ConfJDialog(JFrame mainFrame) {
    super(mainFrame, "Configuration", true);

    // setsize of dialog
    setSize(300, 150);
    setResizable(false);
    setLocationRelativeTo(mainFrame);
    setBackground(Color.BLACK);
    setUndecorated(true);

    JPanel northPanel = new JPanel();
    northPanel.setBackground(Color.GRAY);
    JLabel jLabelTitle = new JLabel("GAME CONFIGURATION");
    jLabelTitle.setForeground(Color.WHITE);
    northPanel.add(jLabelTitle);

    JPanel centerPanel = new JPanel();
    centerPanel.setBackground(Color.BLACK);

    JPanel ghostPanel = new JGhostChoice(group);
    ghostPanel.setBackground(Color.BLACK);

    JLabel ladybugImg = new JLabel(new ImageIcon(PicturesEnum.LADYBUG_RIGHT_2.getImg()));
    ladybugImg.setHorizontalAlignment(JLabel.CENTER);
    centerPanel.setLayout(new GridLayout(1, 2));
    centerPanel.add(ghostPanel);

    // Sud : les boutons 1 ou 2 joueurs
    JPanel southPanel = new JPanel();
    southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.LINE_AXIS));
    southPanel.setBackground(Color.GRAY);
    // bouton 1 joueur
    Button button1Player = new Button("1 player");
    button1Player.addActionListener(this);
    button1Player.setBackground(Color.BLACK);
    button1Player.setForeground(Color.WHITE);
    // bouton 2 joueurs
    Button button2Players = new Button("2 players");
    button2Players.addActionListener(this);
    button2Players.setBackground(Color.BLACK);
    button2Players.setForeground(Color.WHITE);
    southPanel.add(button1Player);
    southPanel.add(button2Players);

    setLayout(new BorderLayout());
    add(northPanel, BorderLayout.NORTH);
    add(centerPanel, BorderLayout.CENTER);
    add(southPanel, BorderLayout.SOUTH);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    switch (e.getActionCommand()) {
    case "1 player":
      // on met un fantôme jouable par les touches
      GhostsSettingsEnum.CLYDE.setComputed(true);
      GhostsSettingsEnum.BLINKY.setComputed(true);
      GhostsSettingsEnum.INKY.setComputed(true);
      GhostsSettingsEnum.PINKY.setComputed(true);
      this.dispose();
      break;
    case "2 players":
      GhostsSettingsEnum.CLYDE.setComputed(true);
      GhostsSettingsEnum.BLINKY.setComputed(true);
      GhostsSettingsEnum.INKY.setComputed(true);
      GhostsSettingsEnum.PINKY.setComputed(true);

      switch (group.getSelection().getActionCommand()) {
      case JGhostChoice.BLINKY_CHOICE:
        GhostsSettingsEnum.BLINKY.setComputed(false);
        break;
      case JGhostChoice.INKY_CHOICE:
        GhostsSettingsEnum.INKY.setComputed(false);
        break;
      case JGhostChoice.CLYDE_CHOICE:
        GhostsSettingsEnum.CLYDE.setComputed(false);
        break;
      case JGhostChoice.PINKY_CHOICE:
        GhostsSettingsEnum.PINKY.setComputed(false);
        break;
      default:
        System.out.println("Erreur de configuration :" + group.getSelection().getActionCommand());
        break;
      }

      this.dispose();
    default:
      break;
    }
    System.out.println(e);

    this.dispose();

  }
}
