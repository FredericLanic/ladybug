package com.kycox.ladybug.tools.dijkstra;

import java.awt.Point;
import java.util.List;

import com.kycox.ladybug.level.ScreenBlock;
import com.kycox.ladybug.level.ScreenData;

/**
 * Algorithme Dijkstra pour trouver un plus court chemin dans un ScreenData
 *
 */
public class Dijkstra {
  /**
   * Retourne une liste de point en coordonn�es (unit� block) pour aller du point
   * A au point B
   * 
   * @param screenData      : jeu en cours
   * @param startCoordinate : coordonn�es de d�part
   * @param endCoordinate   : coordonn�es d'arriv�e
   * @return List<Point>
   */
  public static List<Point> getShorterWay(Point startCoordinate, Point endCoordinate, ScreenData screenData) {
    List<Point> shorterWay;
    Dijkstra dijkstra = new Dijkstra(screenData);
    dijkstra.init(startCoordinate, screenData);

    // Lancement de la recherche
    dijkstra.search(startCoordinate, null, screenData);

    // R�cup�ration de l'unit� final
    int idx = dijkstra.getPos(endCoordinate, screenData);
    UnitDijkstra unitDijstraEnd = dijkstra.getListUnitDijkstra().get(idx);
    // R�cup�ration du chemin le plus court
    shorterWay = unitDijstraEnd.getShorterWay();
    // FIXME : peut �tre des v�rifications ? / capture d'exceptions au cas de Level
    // non valide ?
    return shorterWay;
  }

  private List<UnitDijkstra> listUnitDijkstra;
  int                        nbrAccessibleBlocks;

  /**
   * @param level
   */
  /**
   * Calcul du parcours le plus court en partant de startCoordonnate (coordonn�es
   * de Block) dans un Level de Pacman IMPORTANT : On part du principe que le
   * level a �t� v�rifi� : - tous les blocks ont les bonnes bordures
   * INDIVIDUELLEMENT
   * 
   * @param screenData      : jeu Pacman en cours
   * @param startCoordinate : coordonn�es de d�part (unit� : le block du
   *                        screenData)
   */
  private Dijkstra(ScreenData screenData) {
    // Cr�ation de la list des unit�s Dijkstra
    listUnitDijkstra = screenData.convertToDjistraList();
  }

  /**
   * Getter et Setter
   * 
   * @return
   */
  private List<UnitDijkstra> getListUnitDijkstra() {
    return listUnitDijkstra;
  }

  /**
   * Retourne l'index de listUnitDijkstra en fonction des coordonn�es
   * 
   * @param coordinate (unit� x et y : block du genericData) /!\ intimmement li�
   *                   au genericData
   * @return
   */
  private int getPos(Point coordinate, ScreenData screenData) {
    return coordinate.x + coordinate.y * screenData.getNbrBlocksPerLine();
  }

  /*
   * Initialisation de Dijkstra
   */
  private void init(Point startCoordinate, ScreenData screenData) {
    // Initialisation du block de d�but
    int posStart = getPos(startCoordinate, screenData);
    listUnitDijkstra.get(posStart).setDistance(0);
  }

  /**
   * Recherche (m�thode r�cursice)
   * 
   * @param currentCoordinate    : coordonn�es � g�rer (unit� x et y : block du
   *                             genericData)
   * @param previousUnitDijkstra
   */
  private void search(Point currentCoordinate, UnitDijkstra previousUnitDijkstra, ScreenData screenData) {
    // on rcup�re la position
    int currentPos = getPos(currentCoordinate, screenData);
    // on initialise le flag weightHasChanged
    boolean weightHasChanged = false;

    UnitDijkstra currentUnitDijkstra = listUnitDijkstra.get(currentPos);

    // on pose le poids
    if (previousUnitDijkstra == null) { // toute premi�re case
      currentUnitDijkstra.setDistance(0);
      currentUnitDijkstra.setCoordinate(currentCoordinate);
      currentUnitDijkstra.setPrevious(previousUnitDijkstra);
      weightHasChanged = true;
      // on doit faire les cases autour
    } else if (previousUnitDijkstra.getDistance() + 1 < currentUnitDijkstra.getDistance()) {
      // je pense qu'il y a une erruer dans la condition
      currentUnitDijkstra.setDistance(previousUnitDijkstra.getDistance() + 1);
      currentUnitDijkstra.setCoordinate(currentCoordinate);
      currentUnitDijkstra.setPrevious(previousUnitDijkstra);
      weightHasChanged = true;
    }

    // On r�cup�re le Block courant
    ScreenBlock screenBlockCurrent = currentUnitDijkstra.getScreenBlock();

    // D�finition des prochains points
    Point coordinateUp = new Point(currentCoordinate.x, currentCoordinate.y - 1);
    Point coordinateLeft = new Point(currentCoordinate.x - 1, currentCoordinate.y);
    Point coordinateRight = new Point(currentCoordinate.x + 1, currentCoordinate.y);
    Point coordinateDown = new Point(currentCoordinate.x, currentCoordinate.y + 1);

    // Lancement de la recherche en haut quand c'est possible
    if (weightHasChanged && !screenBlockCurrent.isUp()) {
      search(coordinateUp, currentUnitDijkstra, screenData);
    }

    // Lancement de la recherche � gauche quand c'est possible
    if (weightHasChanged && !screenBlockCurrent.isLeft()) {
      search(coordinateLeft, currentUnitDijkstra, screenData);
    }

    // Lancement de la recherche � droite quand c'est possible
    if (weightHasChanged && !screenBlockCurrent.isRight()) {
      search(coordinateRight, currentUnitDijkstra, screenData);
    }

    // Lancement de la recherche en bas quand c'est possible
    if (weightHasChanged && !screenBlockCurrent.isDown()) {
      search(coordinateDown, currentUnitDijkstra, screenData);
    }
  }
}
