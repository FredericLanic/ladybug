package com.kycox.ladybug.level;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.kycox.ladybug.constant.Constants;
import com.kycox.ladybug.engine.element.pacman.action.PacmanActions;
import com.kycox.ladybug.tools.Utils;
import com.kycox.ladybug.tools.dijkstra.UnitDijkstra;

/**
 * La map du jeu en cours
 *
 */
public final class ScreenData {

  // chargement des niveaux
  private Levels            gameLevels = new Levels();
  private int               initNbrBlockPoint;
  private Point             initPacmanBlockPoint;
  private List<ScreenBlock> lstBlocks  = new ArrayList<>();
  private int               nbrBlocksPerLine;
  private int               nbrLines;
  private Point             regenBlockPoint;

  // FIXME : un peu de refacto ?
  private boolean checkBlock(Point point) {
    boolean hasChanged = false;

    Point checkedPoint;
    ScreenBlock previousScreenBlock;
    ScreenBlock screenBlock = getBlock(point);

    if (screenBlock == null)
      return false;

    boolean isLeft = screenBlock.isLeft();
    boolean isRight = screenBlock.isRight();
    boolean isUp = screenBlock.isUp();
    boolean isDown = screenBlock.isDown();

    // v�rification des bordures !!!

    if (point.x == 0 && !isLeft) {
      screenBlock.addLeft();
      hasChanged = true;
    }

    if (point.x == (nbrBlocksPerLine - 1) && !isRight) {
      screenBlock.addRight();
      hasChanged = true;
    }

    if (point.y == 0 && !isUp) {
      screenBlock.addUp();
      hasChanged = true;
    }

    if (point.y == (nbrLines - 1) && !isDown) {
      screenBlock.addDown();
      hasChanged = true;
    }

    // v�rification de la bordure en haut
    if (isUp && point.y > 0) {
      checkedPoint = new Point(point.x, point.y - 1);
      previousScreenBlock = getBlock(checkedPoint);
      if (previousScreenBlock != null && !previousScreenBlock.isDown()) {
        hasChanged = true;
        previousScreenBlock.addDown();
      }
    }

    // v�rification de la bordure en bas
    if (isDown && point.y < nbrLines - 1) {
      checkedPoint = new Point(point.x, point.y + 1);
      previousScreenBlock = getBlock(checkedPoint);
      if (previousScreenBlock != null && !previousScreenBlock.isUp()) {
        hasChanged = true;
        previousScreenBlock.addUp();
      }
    }

    // v�rification de la bordure � droite
    if (isRight && point.x < nbrBlocksPerLine - 1) {
      checkedPoint = new Point(point.x + 1, point.y);
      previousScreenBlock = getBlock(checkedPoint);
      if (previousScreenBlock != null && !previousScreenBlock.isLeft()) {
        hasChanged = true;
        previousScreenBlock.addLeft();
      }
    }

    // v�rification de la bordure � gauche
    if (isLeft && point.x > 0) {
      checkedPoint = new Point(point.x - 1, point.y);
      previousScreenBlock = getBlock(checkedPoint);
      if (previousScreenBlock != null && !previousScreenBlock.isRight()) {
        hasChanged = true;
        previousScreenBlock.addRight();
      }

    }
    return hasChanged;
  }

  /**
   * V�rifie les blocks
   */
  public void checkBlocks() {
    for (int x = 0; x < nbrBlocksPerLine; x++) {
      for (int y = 0; y < nbrLines; y++) {
        checkBlock(new Point(x, y));
      }
    }
  }

  /**
   * Convert le IData en une List<UnitDijkstra>
   * 
   * @return
   */
  public List<UnitDijkstra> convertToDjistraList() {
    List<UnitDijkstra> lstUnitDijkstra = new ArrayList<>();
    // java8 : faire un stream
    for (ScreenBlock block : lstBlocks) {
      lstUnitDijkstra.add(new UnitDijkstra(block));
    }

    return lstUnitDijkstra;
  }

  /**
   * Retourne un objet de type ScreenBlock qui repr�sente le block
   * 
   * @param pointPos : coordonn�es BLOCK (x,y) dans la fen�tre
   * @return
   */
  public ScreenBlock getBlock(Point posPoint) {
    // java8 :
    return lstBlocks.stream().filter(b -> b.getCoordinate().equals(posPoint)).findFirst().orElse(null);
  }

  /**
   * Retourne la position initiale de Pacman
   * 
   * @return
   */
  public Point getInitPacmanPos() {
    return Utils.convertPointToGraphicUnit(initPacmanBlockPoint);
  }

  /**
   * Retourne le nombre de block par ligne de la map du niveau
   * 
   * @return
   */
  public int getNbrBlocksPerLine() {
    return nbrBlocksPerLine;
  }

  /**
   * Retourne le nombre de points � manger par pacman pr�sents dans le IData
   */
  public int getNbrBlocksWithPoint() {
    return (int) lstBlocks.stream().filter(ScreenBlock::isPoint).count();
  }

  /**
   * Retourne le nombre de lignes de la map du niveau
   * 
   * @return
   */
  public int getNbrLines() {
    return nbrLines;
  }

  /**
   * Retourne le nombre de m�ga point � mettre al�atoirement dans la map
   * 
   * @return
   */
  // FIXME : mettre ce nombre dans le Level ? ou bien faire un calcul du nombre de
  // m�ga point en fonction du niveau
  public int getNbrMegaPoint() {
    return 2;
  }

  /**
   * Return the percentage of eaten point
   * 
   * @return
   */
  public int getPercentageEatenPoint() {
    return (initNbrBlockPoint - getNbrBlocksWithPoint()) * 100 / initNbrBlockPoint;
  }

  /**
   * Retourne la position dans le IData du num�ro du block ayant un point
   * 
   * @param numPoint
   * @return
   */
  private int getPosNumPoint(int numPoint) {
    int nbrPoint = 0;
    int pos = 0;
    for (int i = 0; i < lstBlocks.size(); i++) {
      if (lstBlocks.get(i).isPoint()) {
        nbrPoint++;
        if (nbrPoint == numPoint)
          pos = i;
      }
    }
    return pos;
  }

  /**
   * Retourne une position al�atoire du tableau o� se trouve un point
   */
  private int getRandomPosNumPoint() {
    // nombre de points dans le IData courant
    int nbrPoints = getNbrBlocksWithPoint();
    int randomPoint = new Random().nextInt(nbrPoints);
    return getPosNumPoint(randomPoint);
  }

  /**
   * Retourne les coordonn�es al�atoire GRAPHIQUE d'un block qui contient un point
   * � manger par pacman
   * 
   * @return
   */
  public Point getRandomPosOnAPoint() {
    int pos = getRandomPosNumPoint();
    int yBlock = pos / getNbrBlocksPerLine();
    int xBlock = pos % getNbrBlocksPerLine();
    return new Point(xBlock, yBlock);
  }

  /**
   * Retourne le point ou les fant�mes renaissent
   * 
   * @return
   */
  public Point getRevivorGhostPos() {
    return Utils.convertPointToGraphicUnit(regenBlockPoint);
  }

  /**
   * Retourne la hauteur GRAPHIQUE de la map du jeu (coord y)
   * 
   * @return
   */
  public int getScreenHeight() {
    return getNbrLines() * Constants.BLOCK_SIZE;
  }

  /**
   * Retourne la largeur GRAPHIQUE de la map du jeu (coord x)
   * 
   * @return
   */
  public int getScreenWidth() {
    return getNbrBlocksPerLine() * Constants.BLOCK_SIZE;
  }

  /***
   * Affecte la map en focntion du numero de niveau
   * 
   * @param numLevel
   */
  public void setLevelMap(int numLevel, boolean isInGame) {
    ILevel level = gameLevels.getLevel(numLevel);
    lstBlocks = level.getLstBlocks();
    checkBlocks();
    // nbr lignes / nbr colonnes
    nbrBlocksPerLine = level.getNbrBlocksByLine();
    nbrLines = level.getNbrLines();
    // position initial de pacman
    initPacmanBlockPoint = level.getInitPacmanBlockPos();
    // ajoute le point regen�ration des fant�mes
    regenBlockPoint = level.getGhostRegenerateBlockPoint();
    lstBlocks.stream().filter(b -> b.getCoordinate().equals(regenBlockPoint)).forEach(ScreenBlock::addSurvivorPoint);
    // ajout des mega points al�atoires
    if (isInGame) {
      for (int i = 0; i < level.getNbrMegaPoints(); i++) {
        Point randomPoint = getRandomPosOnAPoint();
        lstBlocks.stream().filter(b -> b.getCoordinate().equals(randomPoint)).forEach(ScreenBlock::setMegaPoint);
      }
    }
    initNbrBlockPoint = getNbrBlocksWithPoint();
  }

  /**
   * Update Point
   * 
   * FIXME : � mettre ailleurs
   */
  public void updateScreenBlock(PacmanActions pacmanActions) {
    if (pacmanActions.hasEatenAMegaPoint() || pacmanActions.hasEatenAPoint()) {
      ScreenBlock screenBlock = pacmanActions.getCurrentPoint();
      screenBlock.removePoint();
    }
  }
}
