package com.kycox.ladybug.engine.element.ghost;

import java.awt.Point;
import java.util.List;
import java.util.Random;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.engine.element.BodyMovedByUser;
import com.kycox.ladybug.engine.element.SpeedFunction;
import com.kycox.ladybug.engine.element.ghost.action.GhostActions;
import com.kycox.ladybug.engine.element.ghost.set.GhostStatusEnum;
import com.kycox.ladybug.engine.element.ghost.set.GhostsSettingsEnum;
import com.kycox.ladybug.engine.element.pacman.Pacman;
import com.kycox.ladybug.engine.element.pacman.set.PacmanStatusEnum;
import com.kycox.ladybug.formules.GhostBehavious;
import com.kycox.ladybug.level.ScreenBlock;
import com.kycox.ladybug.level.ScreenData;
import com.kycox.ladybug.tools.Utils;
import com.kycox.ladybug.tools.dijkstra.Dijkstra;

/**
 * Sp�cificit� d'un fant�me
 *
 */
public abstract class Ghost extends BodyMovedByUser {

  private GhostBehavious     behaviousGhost = null;

  private GhostsSettingsEnum ghostSettings  = null;

  private GhostStatusEnum    status         = null;

  /**
   * Constructor
   */
  public Ghost(int numLevel) {
    super();
    // initialise le comportement du fant�me en fonction du niveau
    behaviousGhost = new GhostBehavious(numLevel);
    // statut de d�part NORMAL
    setStatus(GhostStatusEnum.NORMAL);
    // immobile pour commencer
    setDirection(Constants.POINT_ZERO);
  }

  /**
   * Retourne le setting du fant�me.
   * 
   * @return
   */
  public GhostsSettingsEnum getGhostSettings() {
    return ghostSettings;
  }

  /**
   * Cette fonction est a d�finir pour chaque fant�me nomm� (Blinky, Inky, Clyde &
   * Pinky)
   * 
   * @param numLevel
   */
  public abstract void getInitSpeed(int numLevel);

  /**
   * Getters / Setters pour le statut du fant�me
   * 
   * @return
   */
  public GhostStatusEnum getStatus() {
    return status;
  }

  /**
   * Retourne true si le fant�me est g�r� par l'ordinateur
   * 
   * @return
   */
  public boolean isComputed() {
    return ghostSettings.isComputed();
  }

  /**
   * D�placement agressif du fant�me
   * 
   * @param screenData
   * @param pacmanPosBlock
   */
  private void moveAgressive(Point pacmanPosBlock, ScreenData screenData) {
    /**
     * Note : le fant�me peut changer de direction uniquement lorsqu'il rempli le
     * block
     */
    if (changeBlock()) {
      Point ptCurrentBlockGhost = Utils.convertPointToBlockUnit(getPosition());
      List<Point> shorterWay = Dijkstra.getShorterWay(ptCurrentBlockGhost, pacmanPosBlock, screenData);

      Point point0 = shorterWay.get(0);
      if (shorterWay.size() != 1) {
        Point point1 = shorterWay.get(1);
        setDirection(new Point(point1.x - point0.x, point1.y - point0.y));
      } else {
        setDirection(new Point(ptCurrentBlockGhost.x - point0.x, ptCurrentBlockGhost.y - point0.y));
      }
    }
    getPosition().translate(getDirection().x * getSpeed(), getDirection().y * getSpeed());
  }

  /**
   * D�placement d'un fant�me en fonction de son comportement
   * 
   * (son �tat est NORMAL)
   * 
   * @param screenData
   * @param pacmanPosBlock
   */
  private void moveByBehaviour(Point pacmanPosBlock, ScreenData screenData) {
    switch (ghostSettings.getBehavious()) {
    case SMART:
    case AGGRESSIVE:
      moveAgressive(pacmanPosBlock, screenData);
      break;
    default:
      moveByDefault(screenData);
      break;
    }
  }

  /**
   * D�placement du Fant�me Renvoi un objet StateMoving A ce jour, cela ne sert �
   * rien, mais je pr�f�re le laisser pour les prochaines �volutions.
   */
  // FIXME : c'est une fonction un peu alambiqu�e en fait; un refacto me semble
  // n�cessaire
  private void moveByDefault(ScreenData screenData) {
    int count = 0;
    int[] dx = new int[4];
    int[] dy = new int[4];

    Point posPoint = getPosition();

    if (changeBlock()) {
      ScreenBlock currentScreenBlock = screenData.getBlock(Utils.convertPointToBlockUnit(posPoint));
      count = 0;
      if (!currentScreenBlock.isLeft() && getDirection().x != 1) {
        dx[count] = -1;
        dy[count] = 0;
        count++;
      }

      if (!currentScreenBlock.isUp() && getDirection().y != 1) {
        dx[count] = 0;
        dy[count] = -1;
        count++;
      }

      if (!currentScreenBlock.isRight() && getDirection().x != -1) {
        dx[count] = 1;
        dy[count] = 0;
        count++;
      }

      if (!currentScreenBlock.isDown() && getDirection().y != -1) {
        dx[count] = 0;
        dy[count] = 1;
        count++;
      }

      if (count == 0) {
        if (currentScreenBlock.isLeft() && currentScreenBlock.isRight() && currentScreenBlock.isUp()
            && currentScreenBlock.isDown()) {
          setDirection(Constants.POINT_ZERO);
        } else {
          setDirection(new Point(getDirection().x, -getDirection().y));
        }
      } else {
        count = new Random().nextInt(count);
        if (count > 3) {
          count = 3;
        }
        setDirection(new Point(dx[count], dy[count]));
      }
    }

    posPoint.translate(getDirection().x * getSpeed(), getDirection().y * getSpeed());
  }

  /**
   * D�placement du fant�me (g�r� par l'ordinateur) en fonction de l'emplacement
   * de Pacman
   * 
   * @param pacmanPosBlock
   */
  public void moveGhostByComputer(Point pacmanPosBlock, ScreenData screenData) {
    // D�placement en fonction du status du fant�me
    switch (getStatus()) {
    case DYING:
      moveToRegenerate(screenData);
      break;
    case FLASH:
    case SCARED:
      moveScared(pacmanPosBlock, screenData);
      break;
    case NORMAL:
      if (behaviousGhost.isActive())
        moveByBehaviour(pacmanPosBlock, screenData);
      else
        moveByDefault(screenData);
      break;
    default:
      System.out.println("Le statut " + getStatus() + " n'est pas reconnu, le fant�me est immobile !!");
      break;
    }
  }

  /**
   * D�placement du fant�me g�r� par l'utilisateur
   * 
   * @param pacmanPosBlock
   * @param screenData
   * @param blinkyRequest
   */
  public void moveGhostByUser(Point pacmanPosBlock, ScreenData screenData, Point blinkyRequest) {
    requeteDirectionPoint = blinkyRequest;
    switch (getStatus()) {
    case NORMAL:
      if (changeBlock())
        move(screenData.getBlock(Utils.convertPointToBlockUnit(getPosition())));
      getPosition().translate(getDirection().x * getSpeed(), getDirection().y * getSpeed());
      break;
    default:
      moveGhostByComputer(pacmanPosBlock, screenData);
      break;
    }
  }

  /**
   * D�placement quand le fant�me a peur
   * 
   * @param data
   * @param pacmanPosBlock
   */
  private void moveScared(Point pacmanPosBlock, ScreenData screenData) {
    Point ptCurrentScreenGhost = getPosition();
    boolean canScaredMove = false;
    Point scaredDirection = Constants.POINT_ZERO;

    if (changeBlock()) {
      Point ptCurrentBlockGhost = Utils.convertPointToBlockUnit(ptCurrentScreenGhost);
      ScreenBlock currentBlockGhost = screenData.getBlock(ptCurrentBlockGhost);
      List<Point> shorterWay = Dijkstra.getShorterWay(ptCurrentBlockGhost, pacmanPosBlock, screenData);
      if (shorterWay.size() != 1) {
        Point point0 = shorterWay.get(0);
        Point point1 = shorterWay.get(1);
        scaredDirection = new Point(point0.x - point1.x, point0.y - point1.y);

        canScaredMove = canMove(scaredDirection, currentBlockGhost);
      }
    }

    if (canScaredMove) {
      setDirection(scaredDirection);
      ptCurrentScreenGhost.translate(getDirection().x * getSpeed(), getDirection().y * getSpeed());
    } else {
      moveByDefault(screenData);
    }
  }

  /**
   * D�placement du fant�me mang�
   * 
   * @param data
   * @param coordinateRevivorGhost
   */
  private void moveToRegenerate(ScreenData screenData) {
    // Le fant�me est arriv� au limite du block
    if (changeBlock()) {
      // calcul du chemin le plus court :
      List<Point> shorterWay = Dijkstra.getShorterWay(Utils.convertPointToBlockUnit(getPosition()),
          Utils.convertPointToBlockUnit(screenData.getRevivorGhostPos()), screenData);

      // S'il ne reste plus qu'un bloc � parcourir, le fant�me est arriv�
      if (shorterWay.size() == 1) {
        // Le fant�me est arriv� au point de reg�n�ration, il redevient "normal" avec
        // une vitesse "normale" aussi
        setSpeedIndex(getStartIndexSpeed());
        setStatus(GhostStatusEnum.REGENERATING);
      } else {
        // On prend le premier block cible
        Point currentPoint = shorterWay.get(0);
        // calcul du prochain endroit � d�placer le fant�me mang�
        Point nextPoint = shorterWay.get(1);
        int moveX = nextPoint.x - currentPoint.x;
        int moveY = nextPoint.y - currentPoint.y;

        if (moveX > 0) {
          setDirection(Constants.POINT_RIGHT);
        } else if (moveX < 0) {
          setDirection(Constants.POINT_LEFT);
        } else if (moveY > 0) {
          setDirection(Constants.POINT_DOWN);
        } else if (moveY < 0) {
          setDirection(Constants.POINT_UP);
        }
      }
    }

    getPosition().translate(getDirection().x * getSpeed(), getDirection().y * getSpeed());
  }

  /**
   * Retourne les actions d�tect�es issu du fant�me.
   * 
   * @param pacman
   * @return
   */
  public GhostActions setGhostActions(Pacman pacman) {
    GhostActions ghostActions = new GhostActions();
    ghostActions.setGhost(this);

    // D�tection de la collision avec un fant�me et pacman
    if (getPosition().distance(pacman.getPosition()) < Constants.DISTANCE_MIN
        && !getStatus().equals(GhostStatusEnum.DYING) && !getStatus().equals(GhostStatusEnum.REGENERATING)
        && !pacman.getStatus().equals(PacmanStatusEnum.DYING) && !pacman.getStatus().equals(PacmanStatusEnum.DEAD)) {
      if (GhostStatusEnum.isScared().test(this) || GhostStatusEnum.isFlashing().test(this)) {
        // FANTOME
        ghostActions.setEaten(true);
      } else {
        // Mise � mort de PackMan !!!
        ghostActions.setEatPacman(true);
      }
    }

    return ghostActions;
  }

  /**
   * Affecte la configuration du fant�me
   */
  public void setGhostSettings(GhostsSettingsEnum ghostSettings) {
    this.ghostSettings = ghostSettings;
  }

  /**
   * Affecte des param�tres au fant�mes qui vient d'�tre mang�
   */
  public void setSettingJustAfterBeEaten(int numLevel) {
    setPosition(Utils.convertPointToGraphicUnit(Utils.convertPointToBlockUnit(getPosition())));
    // � d�placer dans
    setStatus(GhostStatusEnum.DYING);
    setSpeedIndex(SpeedFunction.getInstance().getRealIndexSpeedPlus(numLevel));
  }

  public abstract void setSpeedDuringGame();

  /**
   * Affecte l'�tat du statut du fant�me
   * 
   * @param status
   */
  public void setStatus(GhostStatusEnum status) {
    this.status = status;
  }
}